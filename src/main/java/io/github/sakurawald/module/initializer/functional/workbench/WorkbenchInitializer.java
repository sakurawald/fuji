package io.github.sakurawald.module.initializer.functional.workbench;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

public class WorkbenchInitializer extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("workbench").executes(this::$workbench));
    }

    private int $workbench(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.playerOnlyCommand(ctx, player -> {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, p) -> new CraftingScreenHandler(i, inventory, ScreenHandlerContext.create(p.getWorld(), p.getBlockPos())) {
                @Override
                public boolean canUse(PlayerEntity player) {
                    return true;
                }
            }, Text.translatable("container.crafting")));
            player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return CommandHelper.Return.SUCCESS;
        });
    }
}
