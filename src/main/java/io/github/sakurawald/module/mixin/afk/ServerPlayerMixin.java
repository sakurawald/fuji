package io.github.sakurawald.module.mixin.afk;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.afk.AfkStateAccessor;
import io.github.sakurawald.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static io.github.sakurawald.util.MessageUtil.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerMixin implements AfkStateAccessor {

    @Unique
    private final ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

    @Shadow
    @Final
    public MinecraftServer server;

    @Unique
    private boolean afk = false;

    @Unique
    private long lastLastActionTime = 0;

    @Inject(method = "getPlayerListName", at = @At("HEAD"), cancellable = true)
    public void $getPlayerListName(CallbackInfoReturnable<Text> cir) {
        AfkStateAccessor accessor = (AfkStateAccessor) player;

        if (accessor.fuji$isAfk()) {
            cir.setReturnValue(Text.literal("afk " + player.getGameProfile().getName()));
            cir.setReturnValue(ofText(player, false, Configs.configHandler.model().modules.afk.format));
        } else {
            cir.setReturnValue(null);
        }
    }


    @Inject(method = "updateLastActionTime", at = @At("HEAD"))
    public void $updateLastActionTime(CallbackInfo ci) {
        if (fuji$isAfk()) {
            fuji$setAfk(false);
        }
    }

    @Override
    public void fuji$setAfk(boolean flag) {
        this.afk = flag;
        this.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, (ServerPlayerEntity) (Object) this));
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
