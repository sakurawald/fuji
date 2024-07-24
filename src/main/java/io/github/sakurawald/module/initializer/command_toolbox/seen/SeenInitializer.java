package io.github.sakurawald.module.initializer.command_toolbox.seen;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.SeenModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.DateUtil;
import io.github.sakurawald.util.minecraft.MessageHelper;
import lombok.Getter;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
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

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("seen").then(CommandHelper.offlinePlayerArgument().executes(this::$seen)));
    }

    @SuppressWarnings("SameReturnValue")
    private int $seen(CommandContext<ServerCommandSource> ctx) {
        String target = StringArgumentType.getString(ctx, "player");
        if (data.model().player2seen.containsKey(target)) {
            Long time = data.model().player2seen.get(target);
            MessageHelper.sendMessage(ctx.getSource(), "seen.success", target, DateUtil.toStandardDateFormat(time));
        } else {
            MessageHelper.sendMessage(ctx.getSource(), "seen.fail");
        }
        return Command.SINGLE_SUCCESS;
    }

}
