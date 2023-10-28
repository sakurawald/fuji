package io.github.sakurawald.mixin.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.sakurawald.module.skin.SkinRestorer;
import io.github.sakurawald.module.skin.provider.MojangSkinProvider;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("MissingUnique")
@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginNetworkHandlerMixin {

    @Shadow
    @Final
    static Logger LOGGER;

    @Shadow
    @Nullable
    private GameProfile authenticatedProfile;

    private CompletableFuture<Property> pendingSkins;

    private static void applyRestoredSkin(GameProfile gameProfile, Property skin) {
        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", skin);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "verifyLoginAndFinishConnectionSetup", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;"), cancellable = true)
    public void waitForSkin(CallbackInfo ci) {
        if (pendingSkins == null) {
            pendingSkins = CompletableFuture.supplyAsync(() -> {
                // the first time the player join, his skin is DEFAULT_SKIN (see #applyRestoredSkinHook)
                // then we try to get skin from mojang-server. if this failed, then set his skin to DEFAULT_SKIN
                // note: a fake-player will not trigger waitForSkin()
                LOGGER.info("Fetch skin for {}", authenticatedProfile.getName());

                if (SkinRestorer.getSkinStorage().getSkin(authenticatedProfile.getId()) == SkinRestorer.getSkinStorage().getDefaultSkin()) {
                    SkinRestorer.getSkinStorage().setSkin(authenticatedProfile.getId(), MojangSkinProvider.getSkin(authenticatedProfile.getName()));
                }
                return SkinRestorer.getSkinStorage().getSkin(authenticatedProfile.getId());
            });
        }

        // cancel the player's login until we finish fetching his skin
        if (!pendingSkins.isDone()) {
            ci.cancel();
        }
    }

    @Inject(method = "finishLoginAndWaitForClient", at = @At("HEAD"))
    public void applyRestoredSkinHook(GameProfile gameProfile, CallbackInfo ci) {
        if (pendingSkins != null)
            applyRestoredSkin(gameProfile, pendingSkins.getNow(SkinRestorer.getSkinStorage().getDefaultSkin()));
    }
}
