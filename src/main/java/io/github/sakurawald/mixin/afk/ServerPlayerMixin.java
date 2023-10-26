package io.github.sakurawald.mixin.afk;

import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.afk.ServerPlayerAccessor_afk;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.TextReplacementConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.sakurawald.util.MessageUtil.ofComponent;
import static io.github.sakurawald.util.MessageUtil.toVomponent;

@Mixin(ServerPlayer.class)
@Slf4j
public abstract class ServerPlayerMixin implements ServerPlayerAccessor_afk {

    @Unique
    private final ServerPlayer player = (ServerPlayer) (Object) this;
    @Shadow
    @Final
    public MinecraftServer server;
    @Shadow private long lastActionTime;
    @Unique
    private boolean afk = false;

    @Unique
    private long lastLastActionTime = 0;

    @Inject(method = "getTabListDisplayName", at = @At("HEAD"), cancellable = true)
    public void getTabListDisplayName(CallbackInfoReturnable<Component> cir) {
        ServerPlayerAccessor_afk accessor = (ServerPlayerAccessor_afk) player;

        if (accessor.sakurawald$isAfk()) {
            cir.setReturnValue(Component.literal("afk " + player.getGameProfile().getName()));
            net.kyori.adventure.text.@NotNull Component component = ofComponent(ConfigManager.configWrapper.instance().modules.afk.format)
                    .replaceText(TextReplacementConfig.builder().match("%player_display_name%").replacement(player.getDisplayName()).build());
            cir.setReturnValue(toVomponent(component));
        } else {
            cir.setReturnValue(null);
        }
    }


    @Inject(method = "resetLastActionTime", at = @At("HEAD"))
    public void resetLastActionTime(CallbackInfo ci) {
        // note: update lastLastActionTime here
        sakurawald$setLastLastActionTime(this.lastActionTime);

        if (sakurawald$isAfk()) {
            sakurawald$setAfk(false);
            MessageUtil.sendBroadcast("afk.off.broadcast", player.getGameProfile().getName());
        }
    }

    @Override
    public void sakurawald$setAfk(boolean flag) {
        this.afk = flag;
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, (ServerPlayer) (Object) this));
    }

    @Override
    public boolean sakurawald$isAfk() {
        return this.afk;
    }

    @Override
    public void sakurawald$setLastLastActionTime(long lastActionTime) {
        this.lastLastActionTime = lastActionTime;
    }

    @Override
    public long sakurawald$getLastLastActionTime() {
        return this.lastLastActionTime;
    }

}
