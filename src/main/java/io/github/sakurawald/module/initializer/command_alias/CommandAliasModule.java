package io.github.sakurawald.module.initializer.command_alias;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandAliasModule extends ModuleInitializer {
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register((server -> {
            CommandDispatcher<ServerCommandSource> dispatcher = Fuji.SERVER.getCommandManager().getDispatcher();
            for (CommandAliasEntry entry : Configs.configHandler.model().modules.command_alias.alias) {
                resolveCommandAliasEntry(dispatcher, entry);
            }
        }));
    }

    private void resolveCommandAliasEntry(CommandDispatcher<ServerCommandSource> dispatcher, CommandAliasEntry entry) {
        CommandNode<ServerCommandSource> target = dispatcher.findNode(entry.to);

         LiteralArgumentBuilder<ServerCommandSource> builder;

         switch (entry.from.size()) {
             case 1:
                 builder = literal(entry.from.get(0)).redirect(target);
                 break;
             case 2:
                 builder = literal(entry.from.get(0)).then(literal(entry.from.get(1)).redirect(target));
                 break;
             case 3:
                 builder = literal(entry.from.get(0)).then(literal(entry.from.get(1)).then(literal(entry.from.get(2)).redirect(target)));
                 break;
             case 4:
                 builder = literal(entry.from.get(0)).then(literal(entry.from.get(1)).then(literal(entry.from.get(2))).then(literal(entry.from.get(3)).redirect(target)));
                 break;
             case 5:
                 builder = literal(entry.from.get(0)).then(literal(entry.from.get(1)).then(literal(entry.from.get(2))).then(literal(entry.from.get(3)).then(literal(entry.from.get(4)).redirect(target))));
                 break;
             default:
                 Fuji.LOGGER.warn("The command alias is too long !");
                 return;
         }

        dispatcher.register(builder);
    }
}
