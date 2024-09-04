package io.github.sakurawald.module.initializer.afk;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.accessor.PlayerCombatExtension;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.afk.accessor.AfkStateAccessor;
import io.github.sakurawald.module.initializer.afk.job.AfkMarkerJob;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;


public class AfkInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> new AfkMarkerJob().schedule());

    }

    // note: issue command will update lastLastActionTime, so it's impossible to use /afk to disable afk
    @CommandNode("afk")
    private int $afk(@CommandSource ServerPlayerEntity player) {
        if (!player.isOnGround()
                || player.isOnFire()
                || player.inPowderSnow
                || ((PlayerCombatExtension) player).fuji$inCombat()) {

            MessageHelper.sendMessage(player, "afk.on.failed");
            return CommandHelper.Return.FAIL;
        }

        ((AfkStateAccessor) player).fuji$setAfk(true);
        MessageHelper.sendMessage(player, "afk.on");
        return CommandHelper.Return.SUCCESS;
    }

    public static boolean isAfk(Entity entity) {
        if (entity instanceof ServerPlayerEntity) {
            AfkStateAccessor afkStateAccessor = (AfkStateAccessor) entity;
            return afkStateAccessor.fuji$isAfk();
        }
        return false;
    }

    public static boolean isPlayerActuallyMovedItself(MovementType movementType, Vec3d vec3d) {
        // if a player itself moved.
        if (movementType == MovementType.PLAYER) {
            // filter zero movement: Vec3d.ZERO
            return Double.compare(vec3d.x, 0) != 0
                    || Double.compare(vec3d.y, 0) != 0
                    || Double.compare(vec3d.z, 0) != 0;
        }

        return false;
    }

}
