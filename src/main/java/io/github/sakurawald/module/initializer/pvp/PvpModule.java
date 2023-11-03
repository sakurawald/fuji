package io.github.sakurawald.module.initializer.pvp;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Objects;


public class PvpModule extends ModuleInitializer {

    @Override
    public void onInitialize() {
        Configs.pvpHandler.loadFromDisk();
    }

    @Override
    public void onReload() {
        Configs.pvpHandler.loadFromDisk();
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("pvp")
                        .then(Commands.literal("on").executes(this::$on))
                        .then(Commands.literal("off").executes(this::$off))
                        .then(Commands.literal("list").executes(this::$list))
                        .then(Commands.literal("status").executes(this::$status))
        );
    }

    private int $on(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String player = Objects.requireNonNull(source.getPlayer()).getGameProfile().getName();

        HashSet<String> whitelist = Configs.pvpHandler.model().whitelist;
        if (!whitelist.contains(player)) {
            whitelist.add(player);
            Configs.pvpHandler.saveToDisk();

            MessageUtil.sendMessage(source, "pvp.on");

            return Command.SINGLE_SUCCESS;
        }

        MessageUtil.sendMessage(source, "pvp.on.already");
        return 0;
    }

    private int $off(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String player = Objects.requireNonNull(source.getPlayer()).getGameProfile().getName();

        HashSet<String> whitelist = Configs.pvpHandler.model().whitelist;
        if (whitelist.contains(player)) {
            whitelist.remove(player);
            Configs.pvpHandler.saveToDisk();

            MessageUtil.sendMessage(source, "pvp.off");
            return Command.SINGLE_SUCCESS;
        }

        MessageUtil.sendMessage(source, "pvp.off.already");
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private int $status(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        HashSet<String> whitelist = Configs.pvpHandler.model().whitelist;
        player.sendMessage(MessageUtil.ofComponent(player, "pvp.status")
                .append(whitelist.contains(player.getGameProfile().getName()) ? MessageUtil.ofComponent(player, "on") : MessageUtil.ofComponent(player, "off")));
        return Command.SINGLE_SUCCESS;
    }

    private int $list(CommandContext<CommandSourceStack> ctx) {
        HashSet<String> whitelist = Configs.pvpHandler.model().whitelist;
        MessageUtil.sendMessage(ctx.getSource(), "pvp.list", whitelist);
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean contains(String name) {
        return Configs.pvpHandler.model().whitelist.contains(name);
    }

}
