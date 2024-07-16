package io.github.sakurawald.module.initializer.test;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


@Slf4j
public class TestInitializer extends ModuleInitializer {

    private static int clearChat(CommandContext<ServerCommandSource> ctx) {
        for (int i = 0; i < 50; i++) {
            ctx.getSource().sendMessage(Component.empty());
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int $run(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();


        return 1;
    }

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        dispatcher.register(
                CommandManager.literal("test").requires(s -> s.hasPermissionLevel(4))
                        .then(CommandManager.literal("clear-chat").executes(TestInitializer::clearChat))
                        .then(CommandManager.literal("run").executes(TestInitializer::$run))
        );
    }
}
