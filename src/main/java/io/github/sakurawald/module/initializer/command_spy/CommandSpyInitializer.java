package io.github.sakurawald.module.initializer.command_spy;

import com.mojang.brigadier.ParseResults;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_spy.config.model.CommandSpyConfigModel;
import net.minecraft.server.command.ServerCommandSource;

public class CommandSpyInitializer extends ModuleInitializer {
    public static final BaseConfigurationHandler<CommandSpyConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, CommandSpyConfigModel.class);

    public static void process(ParseResults<ServerCommandSource> parseResults) {
        // verify
        ServerCommandSource source = parseResults.getContext().getSource();
        if (!CommandSpyInitializer.config.getModel().spy_on_console
            && source.getPlayer() == null) return;

        // spy
        String name = source.getName();
        String string = parseResults.getReader().getString();
        LogUtil.info("{} issued the server command: /{}", name, string);
    }
}
