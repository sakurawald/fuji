package io.github.sakurawald.module.pvp_toggle;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Objects;
import java.util.function.Supplier;


@Slf4j
public class PvpModule extends AbstractModule {

    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.pvp_toggle.enable;
    }

    @Override
    public void onInitialize() {
        ConfigManager.pvpWrapper.loadFromDisk();
        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

    @Override
    public void onReload() {
        ConfigManager.pvpWrapper.loadFromDisk();
    }

    @SuppressWarnings("unused")
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

        HashSet<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        if (!whitelist.contains(player)) {
            whitelist.add(player);
            ConfigManager.pvpWrapper.saveToDisk();

            MessageUtil.sendMessage(source, "pvp_toggle.on");

            return Command.SINGLE_SUCCESS;
        }

        MessageUtil.sendMessage(source, "pvp_toggle.on.already");
        return 0;
    }

    private int $off(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String player = Objects.requireNonNull(source.getPlayer()).getGameProfile().getName();

        HashSet<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        if (whitelist.contains(player)) {
            whitelist.remove(player);
            ConfigManager.pvpWrapper.saveToDisk();

            MessageUtil.sendMessage(source, "pvp_toggle.off");
            return Command.SINGLE_SUCCESS;
        }

        MessageUtil.sendMessage(source, "pvp_toggle.off.already");
        return 0;
    }

    @SuppressWarnings("SameReturnValue")
    private int $status(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return Command.SINGLE_SUCCESS;

        HashSet<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        player.sendMessage(MessageUtil.ofComponent(player, "pvp_toggle.status")
                .append(whitelist.contains(player.getGameProfile().getName()) ? MessageUtil.ofComponent(player, "on") : MessageUtil.ofComponent(player, "off")));
        return Command.SINGLE_SUCCESS;
    }

    private int $list(CommandContext<CommandSourceStack> ctx) {
        HashSet<String> whitelist = ConfigManager.pvpWrapper.instance().whitelist;
        MessageUtil.sendMessage(ctx.getSource(), "pvp_toggle.list", whitelist);
        return Command.SINGLE_SUCCESS;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean contains(String name) {
        return ConfigManager.pvpWrapper.instance().whitelist.contains(name);
    }

}
