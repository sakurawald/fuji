package io.github.sakurawald.module.initializer.functional.workbench;

import io.github.sakurawald.command.annotation.CommandNode;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

public class WorkbenchInitializer extends ModuleInitializer {

    @CommandNode("workbench")
    private int $workbench(@CommandSource ServerPlayerEntity player) {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, p) -> new CraftingScreenHandler(i, inventory, ScreenHandlerContext.create(p.getWorld(), p.getBlockPos())) {
                @Override
                public boolean canUse(PlayerEntity player) {
                    return true;
                }
            }, Text.translatable("container.crafting")));
            player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            return CommandHelper.Return.SUCCESS;
    }
}
