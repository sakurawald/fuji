package fun.sakurawald.module.pvp_toggle;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class PvpModule {
    public static LiteralCommandNode<ServerCommandSource> registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        return dispatcher.register(
                CommandManager.literal("pvp")
                        .then(
                                CommandManager.literal("on")
                                        .executes(PvpModule::enablePvp)
                        )
                        .then(
                                CommandManager.literal("off")
                                        .executes(PvpModule::disablePvp)
                        )
                        .then(
                                CommandManager.literal("list")
                                        .executes(PvpModule::listPlayers)
                        )
                        .then(
                                CommandManager.literal("status")
                                        .executes(PvpModule::pvpStatus)
                        )
        );
    }

    private static int enablePvp(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        GameProfile player = Objects.requireNonNull(source.getPlayer()).getGameProfile();

        if (!PvpWhitelist.contains(player)) {
            PvpWhitelist.addPlayer(player);

            MessageUtil.feedback(source, "PvP for you is now on.", Formatting.DARK_AQUA);
            return 1;
        }

        MessageUtil.feedback(source, "You already have PvP on!", Formatting.DARK_AQUA);
        return 0;
    }

    private static int disablePvp(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        GameProfile player = Objects.requireNonNull(source.getPlayer()).getGameProfile();

        if (PvpWhitelist.contains(player)) {
            PvpWhitelist.removePlayer(player);
            MessageUtil.feedback(source, "PvP for you is now off.", Formatting.DARK_AQUA);
            return 1;
        }

        MessageUtil.feedback(source, "You already have PvP off!", Formatting.DARK_AQUA);
        return 0;
    }


    private static int pvpStatus(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        GameProfile player = Objects.requireNonNull(source.getPlayer()).getGameProfile();
        MessageUtil.feedback(source, "PvP for you is " + (PvpWhitelist.contains(player) ? "on" : "off"), Formatting.DARK_AQUA);
        return 1;
    }

    private static int listPlayers(CommandContext<ServerCommandSource> ctx) {
        MessageUtil.feedback(ctx.getSource(), "Players with PvP on: " + String.join(", ", PvpWhitelist.getPlayers()), Formatting.DARK_AQUA);
        return 1;
    }
}
