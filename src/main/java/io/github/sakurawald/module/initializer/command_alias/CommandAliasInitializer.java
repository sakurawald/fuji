package io.github.sakurawald.module.initializer.command_alias;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_alias.structure.CommandAliasEntry;
import io.github.sakurawald.auxiliary.minecraft.ServerHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandAliasInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register((server -> {
            CommandDispatcher<ServerCommandSource> dispatcher = ServerHelper.getDefaultServer().getCommandManager().getDispatcher();
            for (CommandAliasEntry entry : Configs.configHandler.model().modules.command_alias.alias) {
                registerCommandAliasEntry(dispatcher, entry);
            }
        }));
    }

    private void registerCommandAliasEntry(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, @NotNull CommandAliasEntry entry) {
        LiteralArgumentBuilder<ServerCommandSource> builder = null;

        for (int i = entry.from.size() - 1; i >= 0; i--) {
            String name = entry.from.get(i);

            if (builder == null) {
                CommandNode<ServerCommandSource> target = dispatcher.findNode(entry.to);
                builder = literal(name).redirect(target);
                continue;
            }

            builder = literal(name).then(builder);
        }

        dispatcher.register(builder);
    }
}
