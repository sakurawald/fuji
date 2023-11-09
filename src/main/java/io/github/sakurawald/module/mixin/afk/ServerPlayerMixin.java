package io.github.sakurawald.module.mixin.afk;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.afk.ServerPlayerAccessor_afk;
import io.github.sakurawald.util.MessageUtil;
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

public abstract class ServerPlayerMixin implements ServerPlayerAccessor_afk {

    @Unique
    private final ServerPlayer player = (ServerPlayer) (Object) this;
    @Shadow
    @Final
    public MinecraftServer server;
    @Unique
    private boolean afk = false;

    @Unique
    private long lastLastActionTime = 0;

    @Inject(method = "getTabListDisplayName", at = @At("HEAD"), cancellable = true)
    public void getTabListDisplayName(CallbackInfoReturnable<Component> cir) {
        ServerPlayerAccessor_afk accessor = (ServerPlayerAccessor_afk) player;

        if (accessor.fuji$isAfk()) {
            cir.setReturnValue(Component.literal("afk " + player.getGameProfile().getName()));
            net.kyori.adventure.text.@NotNull Component component = ofComponent(Configs.configHandler.model().modules.afk.format)
                    .replaceText(TextReplacementConfig.builder().match("%player_display_name%").replacement(player.getDisplayName()).build());
            cir.setReturnValue(toVomponent(component));
        } else {
            cir.setReturnValue(null);
        }
    }


    @Inject(method = "resetLastActionTime", at = @At("HEAD"))
    public void resetLastActionTime(CallbackInfo ci) {
        if (fuji$isAfk()) {
            fuji$setAfk(false);
        }
    }

    @Override
    public void fuji$setAfk(boolean flag) {
        this.afk = flag;
        this.server.getPlayerList().broadcastAll(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, (ServerPlayer) (Object) this));
        MessageUtil.sendBroadcast(this.afk ? "afk.on.broadcast" : "afk.off.broadcast", this.player.getGameProfile().getName());
    }

    @Override
    public boolean fuji$isAfk() {
        return this.afk;
    }

    @Override
    public void fuji$setLastLastActionTime(long lastActionTime) {
        this.lastLastActionTime = lastActionTime;
    }

    @Override
    public long fuji$getLastLastActionTime() {
        return this.lastLastActionTime;
    }

}
