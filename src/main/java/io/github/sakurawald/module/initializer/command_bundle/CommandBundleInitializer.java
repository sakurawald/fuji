package io.github.sakurawald.module.initializer.command_bundle;

import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.event.impl.ServerLifecycleEvents;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_bundle.config.model.CommandBundleConfigModel;
import io.github.sakurawald.module.initializer.command_bundle.structure.BundleCommandDescriptor;
import lombok.SneakyThrows;

@CommandNode("command-bundle")
@CommandRequirement(level = 4)
public class CommandBundleInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<CommandBundleConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, CommandBundleConfigModel.class);

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            LogUtil.info("register bundle commands.");
            registerCommandBundles();
        });
    }

    @SneakyThrows
    private static void registerCommandBundles() {
        config.getModel().getEntries().stream()
            .map(BundleCommandDescriptor::make)
            .forEach(BundleCommandDescriptor::register);
    }
}
