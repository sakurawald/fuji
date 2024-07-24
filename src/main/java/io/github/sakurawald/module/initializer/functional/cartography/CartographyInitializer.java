package io.github.sakurawald.module.initializer.functional.cartography;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CartographyTableScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

public class CartographyInitializer extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("cartography").executes(this::$cartography));
    }

    private int $cartography(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.playerOnlyCommand(ctx, player -> {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, p) -> new CartographyTableScreenHandler(i, inventory, ScreenHandlerContext.create(p.getWorld(), p.getBlockPos())) {
                @Override
                public boolean canUse(PlayerEntity player) {
                    return true;
                }
            }, Text.translatable("container.cartography_table")));
            player.incrementStat(Stats.INTERACT_WITH_CARTOGRAPHY_TABLE);
            return CommandHelper.Return.SUCCESS;
        });
    }
}
