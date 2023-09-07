package fun.sakurawald.mixin.teleport_warmup;

import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.teleport_warmup.ServerPlayerEntityAccessor;
import fun.sakurawald.module.teleport_warmup.TeleportTicket;
import fun.sakurawald.module.teleport_warmup.TeleportWarmupModule;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityAccessor {

    @Unique
    public boolean sakurawald$inCombat;

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (!TeleportWarmupModule.tickets.containsKey(player)) {
            TeleportWarmupModule.tickets.put(player, new TeleportTicket(player, targetWorld, player.getPos(), new Vec3d(x, y, z), yaw, pitch, false));
            ci.cancel();
        } else if (!(TeleportWarmupModule.tickets.get(player).ready)) {
            MessageUtil.message(player, ConfigManager.configWrapper.instance().modules.teleport_warmup.in_progress_message, true);
            ci.cancel();
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    public void onDamage(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        // If damage was actually applied...
        if (cir.getReturnValue()) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            if (TeleportWarmupModule.tickets.containsKey(player)) {
                TeleportWarmupModule.tickets.get(player).bossbar.setVisible(false);
                TeleportWarmupModule.tickets.remove(player);
            }
        }
    }

    @Inject(method = "enterCombat", at = @At("RETURN"))
    public void onEnterCombat(CallbackInfo ci) {
        sakurawald$inCombat = true;
    }

    @Inject(method = "endCombat", at = @At("RETURN"))
    public void onExitCombat(CallbackInfo ci) {
        sakurawald$inCombat = false;
    }

    @Override
    public boolean sakurawald$inCombat() {
        return sakurawald$inCombat;
    }
}
