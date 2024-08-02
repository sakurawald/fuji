package io.github.sakurawald.module.initializer.command_toolbox.tppos;

import io.github.sakurawald.command.argument.adapter.wrapper.Dimension;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.common.structure.Position;
import io.github.sakurawald.module.common.structure.TeleportSetup;
import io.github.sakurawald.module.common.structure.random_teleport.RandomTeleport;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.IdentifierHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public class TpposInitializer extends ModuleInitializer {

    @Command("tppos")
    @CommandPermission(level = 4)
    int tppos(@CommandSource ServerPlayerEntity player
            , Optional<Dimension> dimension
            , Optional<Double> x
            , Optional<Double> y
            , Optional<Double> z
            , Optional<Float> yaw
            , Optional<Float> pitch
            , Optional<Integer> centerX
            , Optional<Integer> centerZ
            , Optional<Boolean> circle
            , Optional<Integer> minRange
            , Optional<Integer> maxRange
            , Optional<Integer> minY
            , Optional<Integer> maxY
            , Optional<Integer> maxTryTimes
    ) {

        ServerWorld world = dimension.isPresent() ? dimension.get().getWorld() : player.getServerWorld();

        if (x.isPresent() || y.isPresent() || z.isPresent()) {
            double $x = x.orElse(player.getX());
            double $y = y.orElse(player.getY());
            double $z = z.orElse(player.getZ());
            float $yaw = yaw.orElse(player.getYaw());
            float $pitch = pitch.orElse(player.getPitch());
            Position position = new Position(world, $x, $y, $z, $yaw, $pitch);
            position.teleport(player);
            return CommandHelper.Return.SUCCESS;
        }

        int $centerX = centerX.orElse((int) world.getWorldBorder().getCenterX());
        int $centerZ = centerZ.orElse((int) world.getWorldBorder().getCenterZ());
        boolean $circle = circle.orElse(false);
        int $minRange = minRange.orElse(0);
        int $maxRange = maxRange.orElse((int) world.getWorldBorder().getSize() / 2);
        int $minY = minY.orElse(world.getBottomY());
        int $maxY = maxY.orElse(world.getTopY());
        int $maxTryTimes = maxTryTimes.orElse(8);

        TeleportSetup teleportSetup = new TeleportSetup(IdentifierHelper.ofString(world), $centerX, $centerZ, $circle, $minRange, $maxRange, $minY
                , $maxY, $maxTryTimes);

        RandomTeleport.request(player, teleportSetup,null);

        return CommandHelper.Return.SUCCESS;
    }

}
