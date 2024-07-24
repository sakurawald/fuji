package io.github.sakurawald.module.mixin.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.sakurawald.module.initializer.skin.SkinRestorer;
import io.github.sakurawald.module.initializer.skin.provider.MojangSkinProvider;
import io.github.sakurawald.util.LogUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import net.minecraft.server.network.ServerLoginNetworkHandler;

@SuppressWarnings("MissingUnique")
@Mixin(ServerLoginNetworkHandler.class)
public abstract class ServerLoginNetworkHandlerMixin {

    @Shadow
    @Nullable
    private GameProfile profile;

    private CompletableFuture<Property> pendingSkins;

    private static void applyRestoredSkin(GameProfile gameProfile, Property skin) {
        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", skin);
    }

    @Inject(method = "tickVerify", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;checkCanJoin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/text/Text;"), cancellable = true)
    public void waitForSkin(CallbackInfo ci) {
        if (pendingSkins == null) {
            pendingSkins = CompletableFuture.supplyAsync(() -> {
                // the first time the player join, his skin is DEFAULT_SKIN (see #applyRestoredSkinHook)
                // then we try to get skin from mojang-server. if this failed, then set his skin to DEFAULT_SKIN
                // note: a fake-player will not trigger waitForSkin()
                LogUtil.info("Fetch skin for {}", profile.getName());

                if (SkinRestorer.getSkinStorage().getSkin(profile.getId()) == SkinRestorer.getSkinStorage().getDefaultSkin()) {
                    SkinRestorer.getSkinStorage().setSkin(profile.getId(), MojangSkinProvider.getSkin(profile.getName()));
                }
                return SkinRestorer.getSkinStorage().getSkin(profile.getId());
            });
        }

        // cancel the player's login until we finish fetching his skin
        if (!pendingSkins.isDone()) {
            ci.cancel();
        }
    }

    @Inject(method = "sendSuccessPacket", at = @At("HEAD"))
    public void applyRestoredSkinHook(GameProfile gameProfile, CallbackInfo ci) {
        if (pendingSkins != null)
            applyRestoredSkin(gameProfile, pendingSkins.getNow(SkinRestorer.getSkinStorage().getDefaultSkin()));
    }
}
