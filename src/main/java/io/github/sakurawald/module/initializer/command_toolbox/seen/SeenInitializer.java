package io.github.sakurawald.module.initializer.command_toolbox.seen;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.DateUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.OfflinePlayerName;
import io.github.sakurawald.core.config.handler.abst.ConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_toolbox.seen.config.model.SeenModel;
import lombok.Getter;
import net.minecraft.server.command.ServerCommandSource;

@SuppressWarnings("LombokGetterMayBeUsed")
public class SeenInitializer extends ModuleInitializer {

    @Getter
    private final ConfigurationHandler<SeenModel> data = new ObjectConfigurationHandler<>("seen.json", SeenModel.class);

    @Override
    public void onInitialize() {
        data.loadFromDisk();
    }

    @Override
    public void onReload() {
        data.loadFromDisk();
    }

    @CommandNode("seen")
    @CommandRequirement(level = 4)
    private int $seen(@CommandSource CommandContext<ServerCommandSource> ctx, OfflinePlayerName playerName) {
        String target = playerName.getValue();

        if (data.getModel().player2seen.containsKey(target)) {
            Long time = data.getModel().player2seen.get(target);
            LocaleHelper.sendMessageByKey(ctx.getSource(), "seen.success", target, DateUtil.toStandardDateFormat(time));
        } else {
            LocaleHelper.sendMessageByKey(ctx.getSource(), "seen.fail");
        }
        return CommandHelper.Return.SUCCESS;
    }

}
