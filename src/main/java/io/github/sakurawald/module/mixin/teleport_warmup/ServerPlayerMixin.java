package io.github.sakurawald.module.mixin.teleport_warmup;

import io.github.sakurawald.core.auxiliary.minecraft.EntityHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.RegistryHelper;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.manager.impl.bossbar.BossBarTicket;
import io.github.sakurawald.core.structure.SpatialPose;
import io.github.sakurawald.core.structure.TeleportTicket;
import io.github.sakurawald.module.initializer.teleport_warmup.TeleportWarmupInitializer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayerEntity.class, priority = 1000 - 500)
public abstract class ServerPlayerMixin {

    @Unique
    public @Nullable TeleportTicket getTeleportTicket(@NotNull ServerPlayerEntity player) {
        for (BossBarTicket ticket : Managers.getBossBarManager().getTickets()) {
            if (ticket instanceof TeleportTicket teleportTicket) {
                if (player.equals(teleportTicket.getPlayer())) {
                    return teleportTicket;
                }
            }
        }
        return null;
    }

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void interceptTeleportAndAddTicket(@NotNull ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, @NotNull CallbackInfo ci) {
        if (!TeleportWarmupInitializer.config.getModel().dimension.list.contains(RegistryHelper.ofString(targetWorld))) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // If we try to spawn a fake-player in the end or nether, the fake-player will initially spawn in overworld
        // and teleport to the target world. This will cause the teleport warmup to be triggered.
        if (EntityHelper.isNonRealPlayer(player)) return;

        TeleportTicket ticket = getTeleportTicket(player);
        if (ticket == null) {
            ticket = TeleportTicket.make(
                player
                , SpatialPose.of(player)
                , new SpatialPose(targetWorld, x, y, z, yaw, pitch)
                , TeleportWarmupInitializer.config.getModel().warmup_second * 1000
                , TeleportWarmupInitializer.config.getModel().interrupt_distance
            );
            Managers.getBossBarManager().addTicket(ticket);
            ci.cancel();
        } else {
            if (!ticket.isCompleted()) {
                LocaleHelper.sendActionBarByKey(player, "teleport_warmup.another_teleportation_in_progress");
                ci.cancel();
            }
        }

        // yeah, let's do teleport now.
    }

    @Inject(method = "damage", at = @At("RETURN"))
    public void abortTicketIfGetDamaged(DamageSource damageSource, float amount, @NotNull CallbackInfoReturnable<Boolean> cir) {
        // If damage was actually applied...
        if (cir.getReturnValue()) {
            ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
            if (EntityHelper.isNonRealPlayer(player)) return;

            TeleportTicket ticket = getTeleportTicket(player);
            if (ticket != null) {
                ticket.setAborted(true);
            }
        }
    }
}
