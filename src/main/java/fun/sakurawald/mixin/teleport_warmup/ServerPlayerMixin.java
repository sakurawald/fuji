package fun.sakurawald.mixin.teleport_warmup;

import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.teleport_warmup.ServerPlayerAccessor;
import fun.sakurawald.module.teleport_warmup.TeleportTicket;
import fun.sakurawald.module.teleport_warmup.TeleportWarmupModule;
import fun.sakurawald.util.CarpetUtil;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements ServerPlayerAccessor {

    @Unique
    public boolean sakurawald$inCombat;

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void $teleportTo(ServerLevel targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (CarpetUtil.isFakePlayer(player)) return;

        if (!TeleportWarmupModule.tickets.containsKey(player)) {
            TeleportWarmupModule.tickets.put(player, new TeleportTicket(player, targetWorld, player.position(), new Vec3(x, y, z), yaw, pitch, false));
            ci.cancel();
        } else {
            TeleportTicket ticket = TeleportWarmupModule.tickets.get(player);
            if (!ticket.ready) {
                MessageUtil.message(player, ConfigManager.configWrapper.instance().modules.teleport_warmup.in_progress_message, true);
                ci.cancel();
            }
        }
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    public void $hurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        // If damage was actually applied...
        if (cir.getReturnValue()) {
            ServerPlayer player = (ServerPlayer) (Object) this;
            if (TeleportWarmupModule.tickets.containsKey(player)) {
                TeleportWarmupModule.tickets.get(player).bossbar.setVisible(false);
                TeleportWarmupModule.tickets.remove(player);
            }
        }
    }

    @Inject(method = "onEnterCombat", at = @At("RETURN"))
    public void $onEnterCombat(CallbackInfo ci) {
        sakurawald$inCombat = true;
    }

    @Inject(method = "onLeaveCombat", at = @At("RETURN"))
    public void $onLeaveCombat(CallbackInfo ci) {
        sakurawald$inCombat = false;
    }

    @Override
    public boolean sakurawald$inCombat() {
        return sakurawald$inCombat;
    }
}
