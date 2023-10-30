package io.github.sakurawald.module.seen;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.SeenGSON;
import io.github.sakurawald.config.base.ConfigWrapper;
import io.github.sakurawald.config.base.ObjectConfigWrapper;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import io.github.sakurawald.util.TimeUtil;
import lombok.Getter;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings("LombokGetterMayBeUsed")

public class SeenModule extends AbstractModule {

    @Getter
    private final ConfigWrapper<SeenGSON> data = new ObjectConfigWrapper<>("seen.json", SeenGSON.class);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
        data.loadFromDisk();
    }

    @Override
    public void onReload() {
        data.loadFromDisk();
    }

    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("seen").then(CommandUtil.offlinePlayerArgument().executes(this::$seen)));
    }

    @SuppressWarnings("SameReturnValue")
    private int $seen(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        String target = StringArgumentType.getString(ctx, "player");
        if (data.instance().player2seen.containsKey(target)) {
            Long time = data.instance().player2seen.get(target);
            MessageUtil.sendMessage(player, "seen.success", target, TimeUtil.getFormattedDate(time));
        } else {
            MessageUtil.sendMessage(player, "seen.fail");
        }
        return Command.SINGLE_SUCCESS;
    }

}
