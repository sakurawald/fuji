package io.github.sakurawald.module.mixin.teleport_warmup;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.back.BackModule;
import io.github.sakurawald.module.initializer.teleport_warmup.Position;
import io.github.sakurawald.module.initializer.teleport_warmup.ServerPlayerCombatStateAccessor;
import io.github.sakurawald.module.initializer.teleport_warmup.TeleportTicket;
import io.github.sakurawald.module.initializer.teleport_warmup.TeleportWarmupModule;
import io.github.sakurawald.util.CarpetUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ServerPlayerEntity.class)

public abstract class ServerPlayerMixin implements ServerPlayerCombatStateAccessor {
    @Unique
    private static final BackModule backModule = ModuleManager.getInitializer(BackModule.class);
    @Unique
    private static final TeleportWarmupModule module = ModuleManager.getInitializer(TeleportWarmupModule.class);
    @Unique
    public boolean fuji$inCombat;

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void $teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // If we try to spawn a fake-player in the end or nether, the fake-player will initially spawn in overworld
        // and teleport to the target world. This will cause the teleport warmup to be triggered.
        if (CarpetUtil.isFakePlayer(player)) return;

        String playerName = player.getGameProfile().getName();
        if (!module.tickets.containsKey(playerName)) {
            module.tickets.put(playerName,
                    new TeleportTicket(player
                            , Position.of(player), new Position(targetWorld, x, y, z, yaw, pitch), false));
            ci.cancel();
            return;
        } else {
            TeleportTicket ticket = module.tickets.get(playerName);
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

    @Inject(method = "damage", at = @At("RETURN"))
    public void $damage(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        // If damage was actually applied...
        if (cir.getReturnValue()) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            String playerName = player.getGameProfile().getName();
            if (module.tickets.containsKey(playerName)) {
                module.tickets.get(playerName).bossbar.removeViewer(player);
                module.tickets.remove(playerName);
            }
        }
    }

    @Inject(method = "enterCombat", at = @At("RETURN"))
    public void $enterCombat(CallbackInfo ci) {
        fuji$inCombat = true;
    }

    @Inject(method = "endCombat", at = @At("RETURN"))
    public void $leaveCombat(CallbackInfo ci) {
        fuji$inCombat = false;
    }

    @Override
    public boolean fuji$inCombat() {
        return fuji$inCombat;
    }
}
