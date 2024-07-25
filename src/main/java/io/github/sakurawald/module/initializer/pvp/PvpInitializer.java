package io.github.sakurawald.module.initializer.pvp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.PvPModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import java.util.HashSet;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;


public class PvpInitializer extends ModuleInitializer {

    public static final ConfigHandler<PvPModel> pvpHandler = new ObjectConfigHandler<>("pvp.json", PvPModel.class);

    @Override
    public void onInitialize() {
        pvpHandler.loadFromDisk();
    }

    @Override
    public void onReload() {
        pvpHandler.loadFromDisk();
    }

    @SuppressWarnings("unused")
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(
                literal("pvp")
                        .then(literal("on").executes(this::$on))
                        .then(literal("off").executes(this::$off))
                        .then(literal("list").executes(this::$list))
                        .then(literal("status").executes(this::$status))
        );
    }

    private int $on(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            HashSet<String> whitelist = pvpHandler.model().whitelist;
            String name = player.getGameProfile().getName();
            if (!whitelist.contains(name)) {
                whitelist.add(name);
                pvpHandler.saveToDisk();

                MessageHelper.sendMessage(player, "pvp.on");

                return CommandHelper.Return.SUCCESS;
            }

            MessageHelper.sendMessage(player, "pvp.on.already");
            return CommandHelper.Return.SUCCESS;
        });
    }

    private int $off(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            HashSet<String> whitelist = pvpHandler.model().whitelist;
            String name = player.getGameProfile().getName();
            if (whitelist.contains(name)) {
                whitelist.remove(name);
                pvpHandler.saveToDisk();

                MessageHelper.sendMessage(player, "pvp.off");
                return CommandHelper.Return.SUCCESS;
            }

            MessageHelper.sendMessage(player, "pvp.off.already");
            return 0;
        });
    }

    @SuppressWarnings("SameReturnValue")
    private int $status(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.Pattern.playerOnlyCommand(ctx, player -> {
            HashSet<String> whitelist = pvpHandler.model().whitelist;
            player.sendMessage(MessageHelper.ofComponent(player, "pvp.status")
                    .append(whitelist.contains(player.getGameProfile().getName()) ? MessageHelper.ofComponent(player, "on") : MessageHelper.ofComponent(player, "off")));
            return CommandHelper.Return.SUCCESS;
        });
    }

    private int $list(CommandContext<ServerCommandSource> ctx) {
        HashSet<String> whitelist = pvpHandler.model().whitelist;
        MessageHelper.sendMessage(ctx.getSource(), "pvp.list", whitelist);
        return CommandHelper.Return.SUCCESS;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean contains(String name) {
        return pvpHandler.model().whitelist.contains(name);
    }

}
