package io.github.sakurawald.module.initializer.command_toolbox.top;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.structure.Position;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class TopInitializer extends ModuleInitializer {

    @CommandNode("top")
    private int top(@CommandSource ServerPlayerEntity player) {
        World world = player.getWorld();
        BlockPos topPosition = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, player.getBlockPos());

        Position position = Position.of(player).withY(topPosition.getY());
        position.teleport(player);

        LocaleHelper.sendMessageByKey(player, "top");
        return CommandHelper.Return.SUCCESS;
    }

}
