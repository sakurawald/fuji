package fun.sakurawald.mixin.teleport_warmup;

import fun.sakurawald.module.back.BackModule;
import fun.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import fun.sakurawald.module.teleport_warmup.Position;
import fun.sakurawald.module.teleport_warmup.ServerPlayerAccessor;
import fun.sakurawald.module.teleport_warmup.TeleportTicket;
import fun.sakurawald.module.teleport_warmup.TeleportWarmupModule;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static fun.sakurawald.util.MessageUtil.sendActionBar;


@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements ServerPlayerAccessor {

    @Unique
    public boolean sakurawald$inCombat;

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void $teleportTo(ServerLevel targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        // If we try to spawn a fake-player in end or nether, the fake-player will initially spawn in overworld
        // and teleport to the target world. This will cause the teleport warmup to be triggered.
        if (BetterFakePlayerModule.isFakePlayer(player)) return;

        if (!TeleportWarmupModule.tickets.containsKey(player)) {
            TeleportWarmupModule.tickets.put(player,
                    new TeleportTicket(player
                            , new Position(player.level(), player.position().x, player.position().y, player.position().z, player.getYRot(), player.getXRot())
                            , new Position(targetWorld, x, y, z, yaw, pitch), false));
            ci.cancel();
        } else {
            TeleportTicket ticket = TeleportWarmupModule.tickets.get(player);
            if (!ticket.ready) {
                sendActionBar(player, "teleport_warmup.another_teleportation_in_progress");
                ci.cancel();
            }
        }

        // let's do teleport now.
        BackModule.updatePlayer(player);
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    public void $hurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        // If damage was actually applied...
        if (cir.getReturnValue()) {
            ServerPlayer player = (ServerPlayer) (Object) this;
            if (TeleportWarmupModule.tickets.containsKey(player)) {
                TeleportWarmupModule.tickets.get(player).bossbar.removeViewer(player);
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
