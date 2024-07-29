package io.github.sakurawald.module.initializer.command_toolbox.top;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.module.common.structure.Position;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

@Command
public class TopInitializer extends ModuleInitializer {

    @Command("top")
    private static int top(@Source ServerPlayerEntity player) {
        World world = player.getWorld();
        BlockPos topPosition = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, player.getBlockPos());

        Position position = Position.of(player).withY(topPosition.getY());
        position.teleport(player);

        MessageHelper.sendMessage(player, "top");
        return CommandHelper.Return.SUCCESS;
    }

    @Command("first second")
    public static int myCommand(CommandContext<ServerCommandSource> ctx, ServerPlayerEntity player) {

        player.sendMessage(Text.literal("you are a player"));
        return 0;
    }

}
