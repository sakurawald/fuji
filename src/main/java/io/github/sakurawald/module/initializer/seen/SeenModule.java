package io.github.sakurawald.module.initializer.seen;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.SeenModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.TimeUtil;
import io.github.sakurawald.util.MessageUtil;
import lombok.Getter;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

@SuppressWarnings("LombokGetterMayBeUsed")

public class SeenModule extends ModuleInitializer {

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

    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("seen").then(CommandUtil.offlinePlayerArgument().executes(this::$seen)));
    }

    @SuppressWarnings("SameReturnValue")
    private int $seen(CommandContext<CommandSourceStack> ctx) {
        String target = StringArgumentType.getString(ctx, "player");
        if (data.model().player2seen.containsKey(target)) {
            Long time = data.model().player2seen.get(target);
            MessageUtil.sendMessage(ctx.getSource(), "seen.success", target, TimeUtil.toStandardDateFormat(time));
        } else {
            MessageUtil.sendMessage(ctx.getSource(), "seen.fail");
        }
        return Command.SINGLE_SUCCESS;
    }

}
