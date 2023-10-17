package io.github.sakurawald.mixin.teleport_warmup;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.back.BackModule;
import io.github.sakurawald.module.teleport_warmup.Position;
import io.github.sakurawald.module.teleport_warmup.ServerPlayerAccessor;
import io.github.sakurawald.module.teleport_warmup.TeleportTicket;
import io.github.sakurawald.module.teleport_warmup.TeleportWarmupModule;
import io.github.sakurawald.util.CarpetUtil;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ServerPlayer.class)
@Slf4j
public abstract class ServerPlayerMixin implements ServerPlayerAccessor {
    @Unique
    private static final BackModule backModule = ModuleManager.getOrNewInstance(BackModule.class);
    @Unique
    private static final TeleportWarmupModule module = ModuleManager.getOrNewInstance(TeleportWarmupModule.class);
    @Unique
    public boolean sakurawald$inCombat;

    @Inject(method = "teleportTo(Lnet/minecraft/server/level/ServerLevel;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void $teleportTo(ServerLevel targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        // If we try to spawn a fake-player in end or nether, the fake-player will initially spawn in overworld
        // and teleport to the target world. This will cause the teleport warmup to be triggered.
        if (CarpetUtil.isFakePlayer(player)) return;

        if (!module.tickets.containsKey(player)) {
            module.tickets.put(player,
                    new TeleportTicket(player
                            , new Position(player.level(), player.position().x, player.position().y, player.position().z, player.getYRot(), player.getXRot())
                            , new Position(targetWorld, x, y, z, yaw, pitch), false));
            ci.cancel();
            return;
        } else {
            TeleportTicket ticket = module.tickets.get(player);
            if (!ticket.ready) {
                MessageUtil.sendActionBar(player, "teleport_warmup.another_teleportation_in_progress");
                ci.cancel();
                return;
            }
        }

        // let's do teleport now.
        if (backModule != null) {
            backModule.updatePlayer(player);
        }
    }

    @Inject(method = "hurt", at = @At("RETURN"))
    public void $hurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        // If damage was actually applied...
        if (cir.getReturnValue()) {
            ServerPlayer player = (ServerPlayer) (Object) this;
            if (module.tickets.containsKey(player)) {
                module.tickets.get(player).bossbar.removeViewer(player);
                module.tickets.remove(player);
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
