package fun.sakurawald.module.pvp_toggle;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.ArrayList;
import java.util.Objects;

public class PvpModule {
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

        ArrayList<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        if (!whitelist.contains(player)) {
            whitelist.add(player);
            ConfigManager.pvpWrapper.saveToDisk();

            MessageUtil.feedback(source, "PvP for you is now on.", ChatFormatting.DARK_AQUA);
            return Command.SINGLE_SUCCESS;
        }

        MessageUtil.feedback(source, "You already have PvP on!", ChatFormatting.DARK_AQUA);
        return 0;
    }

    private static int $off(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String player = Objects.requireNonNull(source.getPlayer()).getGameProfile().getName();

        ArrayList<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        if (whitelist.contains(player)) {
            whitelist.remove(player);
            ConfigManager.pvpWrapper.saveToDisk();

            MessageUtil.feedback(source, "PvP for you is now off.", ChatFormatting.DARK_AQUA);
            return Command.SINGLE_SUCCESS;
        }

        MessageUtil.feedback(source, "You already have PvP off!", ChatFormatting.DARK_AQUA);
        return 0;
    }

    private static int $status(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String player = Objects.requireNonNull(source.getPlayer()).getGameProfile().getName();
        ArrayList<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        MessageUtil.feedback(source, "PvP for you is " + (whitelist.contains(player) ? "on" : "off"), ChatFormatting.DARK_AQUA);
        return Command.SINGLE_SUCCESS;
    }

    private static int $list(CommandContext<CommandSourceStack> ctx) {
        ArrayList<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        MessageUtil.feedback(ctx.getSource(), "Players with PvP on: " + whitelist, ChatFormatting.DARK_AQUA);
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean contains(String name) {
        return ConfigManager.pvpWrapper.instance().whitelist.contains(name);
    }
}
