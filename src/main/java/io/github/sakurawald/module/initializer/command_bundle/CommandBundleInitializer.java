package io.github.sakurawald.module.initializer.command_bundle;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.processor.CommandAnnotationProcessor;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.event.impl.ServerLifecycleEvents;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_bundle.config.model.CommandBundleConfigModel;
import io.github.sakurawald.module.initializer.command_bundle.structure.BundleCommandDescriptor;
import lombok.SneakyThrows;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;


@CommandNode("command-bundle")
@CommandRequirement(level = 4)
public class CommandBundleInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<CommandBundleConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, CommandBundleConfigModel.class);

    private static final List<LiteralArgumentBuilder<ServerCommandSource>> registeredBundleCommands = new ArrayList<>();

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LogUtil.info("register bundle commands.");
            register();
        });
    }

    @Override
    public void onReload() {
        unregister();
        register();
    }

    @CommandNode("register")
    private static int register() {
        registerCommandBundles();
        CommandHelper.updateCommandTree();
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("un-register")
    private static int unregister() {
        RootCommandNode<ServerCommandSource> root = CommandAnnotationProcessor.getDispatcher().getRoot();
        registeredBundleCommands.forEach(it -> {
            LiteralCommandNode<ServerCommandSource> navigationNode = it.build();
            com.mojang.brigadier.tree.CommandNode<ServerCommandSource> targetNode = root.getChild(navigationNode.getName());

            if (unregister(targetNode, navigationNode)) {
                root.getChildren().removeIf(p -> p.getName().equals(navigationNode.getName()));
            }
        });
        registeredBundleCommands.clear();

        CommandHelper.updateCommandTree();
        return CommandHelper.Return.SUCCESS;
    }

    private static boolean unregister(
        com.mojang.brigadier.tree.CommandNode<ServerCommandSource> targetNode
        , com.mojang.brigadier.tree.CommandNode<ServerCommandSource> navigationNode
    ) {

        /* go down */
        navigationNode.getChildren()
            .stream()
            .toList()
            .forEach(child -> {
                if (unregister(targetNode.getChild(child.getName()), child)) {
                    targetNode.getChildren().removeIf(it -> it.getName().equals(child.getName()));
                }
            });

        /* remove leaf node */
        return targetNode.getChildren().isEmpty();
    }

    @CommandNode("list")
    private static int list(@CommandSource CommandContext<ServerCommandSource> ctx) {
        registeredBundleCommands.forEach(it -> ctx.getSource().sendMessage(Text.literal(CommandHelper.buildCommandNodePath(it.build()))));
        return CommandHelper.Return.SUCCESS;
    }

    @SneakyThrows
    private static void registerCommandBundles() {
        config.getModel().getEntries().stream()
            .map(BundleCommandDescriptor::make)
            .forEach(it -> registeredBundleCommands.add(it.register()));
    }
}
