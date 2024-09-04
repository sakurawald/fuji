package io.github.sakurawald.module.initializer.command_toolbox.seen;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.command.argument.wrapper.OfflinePlayerName;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigHandler;
import io.github.sakurawald.module.initializer.command_toolbox.seen.config.model.SeenModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.DateUtil;
import io.github.sakurawald.core.auxiliary.minecraft.MessageHelper;
import lombok.Getter;
import net.minecraft.server.command.ServerCommandSource;

@SuppressWarnings("LombokGetterMayBeUsed")

public class SeenInitializer extends ModuleInitializer {

    @Getter
    private final ConfigHandler<SeenModel> data = new ObjectConfigHandler<>("seen.json", SeenModel.class);

    @Override
    public void onInitialize() {
        data.loadFromDisk();
    }

    @Override
    public void onReload() {
        data.loadFromDisk();
    }

    @CommandNode("seen")
    private int $seen(@CommandSource CommandContext<ServerCommandSource> ctx, OfflinePlayerName playerName) {
        String target = playerName.getString();

        if (data.model().player2seen.containsKey(target)) {
            Long time = data.model().player2seen.get(target);
            MessageHelper.sendMessage(ctx.getSource(), "seen.success", target, DateUtil.toStandardDateFormat(time));
        } else {
            MessageHelper.sendMessage(ctx.getSource(), "seen.fail");
        }
        return CommandHelper.Return.SUCCESS;
    }

}
