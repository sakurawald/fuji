package io.github.sakurawald.module.initializer.command_alias;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.structure.CommandPathMappingEntry;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_alias.config.model.CommandAliasConfigModel;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

public class CommandAliasInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<CommandAliasConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, CommandAliasConfigModel.class);

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register((server -> {
            CommandDispatcher<ServerCommandSource> dispatcher = ServerHelper.getDefaultServer().getCommandManager().getDispatcher();
            for (CommandPathMappingEntry entry : config.getModel().alias) {
                registerCommandAliasEntry(dispatcher, entry);
            }
        }));
    }

    private void registerCommandAliasEntry(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, @NotNull CommandPathMappingEntry entry) {
        LiteralArgumentBuilder<ServerCommandSource> builder = null;

        for (int i = entry.from.size() - 1; i >= 0; i--) {
            String name = entry.from.get(i);

            if (builder == null) {
                CommandNode<ServerCommandSource> target = dispatcher.findNode(entry.to);

                if (target == null) {
                    LogUtil.warn("[command alias] can't find the target command node for alias entry: {}", entry);
                    return;
                }

                builder = CommandManager.literal(name).redirect(target);
                continue;
            }

            builder = CommandManager.literal(name).then(builder);
        }

        dispatcher.register(builder);
    }
}
