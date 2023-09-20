package fun.sakurawald.module.pvp_toggle;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import static fun.sakurawald.util.MessageUtil.resolve;
import static fun.sakurawald.util.MessageUtil.sendMessage;


@Slf4j
public class PvpModule {
    @SuppressWarnings("unused")
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("pvp")
                        .then(Commands.literal("on").executes(PvpModule::$on))
                        .then(Commands.literal("off").executes(PvpModule::$off))
                        .then(Commands.literal("list").executes(PvpModule::$list))
                        .then(Commands.literal("status").executes(PvpModule::$status))
        );
    }

    private static int $on(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String player = Objects.requireNonNull(source.getPlayer()).getGameProfile().getName();

        HashSet<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        if (!whitelist.contains(player)) {
            whitelist.add(player);
            ConfigManager.pvpWrapper.saveToDisk();

            sendMessage(source, "pvp_toggle.on");

            return Command.SINGLE_SUCCESS;
        }

        sendMessage(source, "pvp_toggle.on.already");
        return 0;
    }

    private static int $off(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String player = Objects.requireNonNull(source.getPlayer()).getGameProfile().getName();

        HashSet<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        if (whitelist.contains(player)) {
            whitelist.remove(player);
            ConfigManager.pvpWrapper.saveToDisk();

            sendMessage(source, "pvp_toggle.off");
            return Command.SINGLE_SUCCESS;
        }

        sendMessage(source, "pvp_toggle.off.already");
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private static int $status(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        HashSet<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        player.sendMessage(resolve(player, "pvp_toggle.status")
                .append(whitelist.contains(player.getGameProfile().getName()) ? resolve(player, "on") : resolve(player, "off")));
        return Command.SINGLE_SUCCESS;
    }

    private static int $list(CommandContext<CommandSourceStack> ctx) {
        HashSet<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        sendMessage(ctx.getSource(), "pvp_toggle.list", whitelist);
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean contains(String name) {
        return ConfigManager.pvpWrapper.instance().whitelist.contains(name);
    }
}
