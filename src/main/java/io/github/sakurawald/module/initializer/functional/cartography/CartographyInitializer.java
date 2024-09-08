package io.github.sakurawald.module.initializer.functional.cartography;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

public class CartographyInitializer extends ModuleInitializer {
    @CommandNode("cartography")
    private int $cartography(@CommandSource ServerPlayerEntity player) {
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, p) -> new CartographyTableScreenHandler(i, inventory, ScreenHandlerContext.create(p.getWorld(), p.getBlockPos())) {
            @Override
            public boolean canUse(PlayerEntity player) {
                return true;
            }
        }, Text.translatable("container.cartography_table")));
        player.incrementStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);
        return CommandHelper.Return.SUCCESS;
    }
}
