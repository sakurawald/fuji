package io.github.sakurawald.module.initializer.command_toolbox.seen;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.DateUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.TextHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.OfflinePlayerName;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.transformer.impl.MoveFileIntoModuleConfigDirectoryTransformer;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_toolbox.seen.config.model.SeenDataModel;
import lombok.Getter;
import net.minecraft.server.command.ServerCommandSource;

public class SeenInitializer extends ModuleInitializer {

    @Getter
    private static final BaseConfigurationHandler<SeenDataModel> data = new ObjectConfigurationHandler<>("seen.json", SeenDataModel.class)
        .addTransformer(new MoveFileIntoModuleConfigDirectoryTransformer(Fuji.CONFIG_PATH.resolve("seen.json"), SeenInitializer.class));

    @CommandNode("seen")
    @CommandRequirement(level = 4)
    private static int $seen(@CommandSource ServerCommandSource source, OfflinePlayerName playerName) {
        String target = playerName.getValue();

        if (data.model().player2seen.containsKey(target)) {
            Long time = data.model().player2seen.get(target);
            TextHelper.sendMessageByKey(source, "seen.success", target, DateUtil.toStandardDateFormat(time));
        } else {
            TextHelper.sendMessageByKey(source, "seen.fail");
        }
        return CommandHelper.Return.SUCCESS;
    }

}
