package io.github.sakurawald.module.initializer.functional.enchantment;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class EnchantmentInitializer extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("enchantment").executes(this::$enchantment));
    }

    private int $enchantment(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.playerOnlyCommand(ctx, player -> {
            player.openHandledScreen(new SimpleNamedScreenHandlerFactory((i, inventory, p) -> new MyEnchantmentScreenHandler(i, inventory, ScreenHandlerContext.create(p.getWorld(), p.getBlockPos())) {
            }, Text.translatable("container.enchant")));
            return CommandHelper.Return.SUCCESS;
        });
    }
}
