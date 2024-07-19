package io.github.sakurawald.module.initializer.kit;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.kit.gui.KitEditorGui;
import io.github.sakurawald.util.CommandUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class KitInitializer extends ModuleInitializer {

    private final Path STORAGE_PATH = Fuji.CONFIG_PATH.resolve("kit");

    @Override
    public void onInitialize() {
        STORAGE_PATH.toFile().mkdirs();
    }

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                literal("kit").requires(s -> s.hasPermissionLevel(4))
                        .then(literal("editor").executes(this::$editor))
                        .then(literal("give")
                                .then(argument("player", EntityArgumentType.player()).executes(this::$give)))
        );
    }

    private int $editor(CommandContext<ServerCommandSource> ctx) {

        return CommandUtil.playerOnlyCommand(ctx, player -> {

            Random random = new Random();
            List<Integer> entities = new ArrayList<>();
            for (int i = 0; i < 200; i++) {
                entities.add(random.nextInt(10000));
            }


            KitEditorGui kitEditorGui = new KitEditorGui(player, entities);
            kitEditorGui.open();

            return Command.SINGLE_SUCCESS;
        });
    }

    private int $give(CommandContext<ServerCommandSource> ctx) {
        return 0;
    }

}
