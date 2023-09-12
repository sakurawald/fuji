package fun.sakurawald.mixin.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fun.sakurawald.module.skin.SkinRestorer;
import fun.sakurawald.module.skin.io.SkinStorage;
import fun.sakurawald.module.skin.provider.MojangSkinProvider;
import net.minecraft.server.level.ServerPlayer;
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
    @Nullable GameProfile gameProfile;
    private CompletableFuture<Property> pendingSkins;

    private static void applyRestoredSkin(ServerPlayer playerEntity, Property skin) {
        playerEntity.getGameProfile().getProperties().removeAll("textures");
        playerEntity.getGameProfile().getProperties().put("textures", skin);
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "handleAcceptedLogin", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;"), cancellable = true)
    public void waitForSkin(CallbackInfo ci) {
        if (pendingSkins == null) {
            pendingSkins = CompletableFuture.supplyAsync(() -> {
                LOGGER.debug("Fetching {}'s skin", gameProfile.getName());
                if (SkinRestorer.getSkinStorage().getSkin(gameProfile.getId()) == SkinStorage.DEFAULT_SKIN)
                    SkinRestorer.getSkinStorage().setSkin(gameProfile.getId(), MojangSkinProvider.getSkin(gameProfile.getName()));

                return SkinRestorer.getSkinStorage().getSkin(gameProfile.getId());
            });
        }

        if (!pendingSkins.isDone()) {
            ci.cancel();
        }
    }

    @Inject(method = "placeNewPlayer", at = @At("HEAD"))
    public void applyRestoredSkinHook(ServerPlayer player, CallbackInfo ci) {
        if (pendingSkins != null)
            applyRestoredSkin(player, pendingSkins.getNow(SkinStorage.DEFAULT_SKIN));
    }
}
