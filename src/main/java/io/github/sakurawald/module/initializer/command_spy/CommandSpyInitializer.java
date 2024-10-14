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

        // verify command source
        ServerCommandSource source = parseResults.getContext().getSource();
        if (!CommandSpyInitializer.config.model().spy_on_console
            && source.getPlayer() == null) return;

        // ignore
        String string = parseResults.getReader().getString();
        if (config.model().ignore.stream().anyMatch(string::matches)) {
            return;
        }

        // log
        String name = source.getName();
        LogUtil.info("{} issued the server command: /{}", name, string);
    }
}
