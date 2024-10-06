package io.github.sakurawald.module.mixin.teleport_warmup;

import io.github.sakurawald.core.auxiliary.minecraft.EntityHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.RegistryHelper;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.structure.SpatialPose;
import io.github.sakurawald.core.structure.TeleportTicket;
import io.github.sakurawald.module.initializer.teleport_warmup.TeleportWarmupInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayerEntity.class, priority = 1000 - 500)
public abstract class ServerPlayerMixin {

    @Inject(method = "teleport(Lnet/minecraft/server/world/ServerWorld;DDDFF)V", at = @At("HEAD"), cancellable = true)
    public void interceptTeleportAndAddTicket(@NotNull ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch, @NotNull CallbackInfo ci) {
        // check blacklist
        if (!TeleportWarmupInitializer.config.model().dimension.blacklist.contains(RegistryHelper.ofString(targetWorld))) {
            return;
        }

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // If we try to spawn a fake-player in the end or nether, the fake-player will initially spawn in overworld
        // and teleport to the target world. This will cause the teleport warmup to be triggered.
        if (EntityHelper.isNonRealPlayer(player)) return;

        TeleportTicket ticket = TeleportWarmupInitializer.getTeleportTicket(player);
        if (ticket == null) {
            ticket = TeleportTicket.make(
                player
                , SpatialPose.of(player)
                , new SpatialPose(targetWorld, x, y, z, yaw, pitch)
                , TeleportWarmupInitializer.config.model().warmup_second * 1000
                , TeleportWarmupInitializer.config.model().interruptible
            );
            Managers.getBossBarManager().addTicket(ticket);
            ci.cancel();
        } else if (!ticket.isCompleted()) {
            LocaleHelper.sendActionBarByKey(player, "teleport_warmup.another_teleportation_in_progress");
            ci.cancel();
        }

        // yeah, let's do teleport now.
    }

}
