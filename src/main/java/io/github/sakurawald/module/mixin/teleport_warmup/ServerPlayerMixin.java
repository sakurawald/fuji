package io.github.sakurawald.module.mixin.teleport_warmup;

import io.github.sakurawald.core.auxiliary.minecraft.EntityHelper;
import io.github.sakurawald.core.auxiliary.minecraft.RegistryHelper;
import io.github.sakurawald.core.auxiliary.minecraft.TextHelper;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.structure.SpatialPose;
import io.github.sakurawald.core.structure.TeleportTicket;
import io.github.sakurawald.module.initializer.teleport_warmup.TeleportWarmupInitializer;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(value = ServerPlayerEntity.class, priority = 1000 - 500)
public abstract class ServerPlayerMixin {

    @Inject(method = "teleport", at = @At("HEAD"), cancellable = true)
    public void interceptTeleportAndAddTicket(ServerWorld serverWorld, double x, double y, double z, Set<PositionFlag> set, float yaw, float pitch, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        // check blacklist
        if (!TeleportWarmupInitializer.config.model().dimension.blacklist.contains(RegistryHelper.ofString(serverWorld))) {
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
                , new SpatialPose(serverWorld, x, y, z, yaw, pitch)
                , TeleportWarmupInitializer.config.model().warmup_second * 1000
                , TeleportWarmupInitializer.config.model().interruptible
            );
            Managers.getBossBarManager().addTicket(ticket);
            cir.cancel();
        } else if (!ticket.isCompleted()) {
            TextHelper.sendActionBarByKey(player, "teleport_warmup.another_teleportation_in_progress");
            cir.cancel();
        }

        // yeah, let's do teleport now.
    }

}
