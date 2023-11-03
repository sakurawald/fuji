package io.github.sakurawald.module.initializer.workbench;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;


public class WorkbenchModule extends ModuleInitializer {

    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("workbench").executes(this::$workbench));
    }

    @SuppressWarnings("SameReturnValue")
    private int $workbench(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        SimpleGui simpleGui = new SimpleGui(ExtendedScreenHandlerType.CRAFTING, player, false) {
            @Override
            public void onCraftRequest(ResourceLocation recipeId, boolean shift) {
                super.onCraftRequest(recipeId, shift);
            }
        };
        simpleGui.open();

        return Command.SINGLE_SUCCESS;
    }

}
