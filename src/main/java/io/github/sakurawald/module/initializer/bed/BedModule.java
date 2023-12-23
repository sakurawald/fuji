package io.github.sakurawald.module.initializer.bed;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;


public class BedModule extends ModuleInitializer {

    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("bed").executes(this::$bed));
    }

    private int $bed(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, (player) -> {
            BlockPos respawnPosition = player.getRespawnPosition();
            ResourceKey<Level> respawnDimension = player.getRespawnDimension();
            ServerLevel serverLevel = Fuji.SERVER.getLevel(respawnDimension);
            if (respawnPosition == null || serverLevel == null) {
                MessageUtil.sendMessage(player, "bed.not_found");
                return Command.SINGLE_SUCCESS;
            }

            player.teleportTo(serverLevel, respawnPosition.getX(), respawnPosition.getY(), respawnPosition.getZ(), player.getYRot(), player.getXRot());
            MessageUtil.sendMessage(player, "bed.success");
            return Command.SINGLE_SUCCESS;
        });
    }
}
