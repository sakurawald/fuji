package io.github.sakurawald.module.initializer.command_alias;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_alias.structure.CommandAliasEntry;
import io.github.sakurawald.util.minecraft.ServerHelper;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.ServerCommandSource;

import java.util.List;

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

    private LiteralArgumentBuilder<ServerCommandSource> walk(CommandDispatcher<ServerCommandSource> dispatcher, LiteralArgumentBuilder<ServerCommandSource> parent, CommandAliasEntry entry, int level) {
        // edge case
        if (entry.from.size() == 1) {
            return literal(entry.from.getFirst()).redirect(dispatcher.findNode(entry.to));
        }

        List<String> names = entry.from;
        String name = names.get(level);

        if (parent == null) {
            parent = literal(name);
            return walk(dispatcher, parent, entry, level + 1);
        }

        LiteralArgumentBuilder<ServerCommandSource> child = literal(name);
        if (level + 1 == names.size()) {
            child.redirect(dispatcher.findNode(entry.to));
            return parent.then(child);
        }

        LiteralArgumentBuilder<ServerCommandSource> value = walk(dispatcher, child, entry, level + 1);
        return parent.then(value);
    }

    private void registerCommandAliasEntry(CommandDispatcher<ServerCommandSource> dispatcher, CommandAliasEntry entry) {
        LiteralArgumentBuilder<ServerCommandSource> root = walk(dispatcher, null, entry, 0);
        dispatcher.register(root);
    }
}
