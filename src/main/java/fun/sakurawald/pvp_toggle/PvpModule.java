package fun.sakurawald.pvp_toggle;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
        GameProfile player = source.getPlayer().getGameProfile();

        if (!PvpWhitelist.contains(player)) {
            PvpWhitelist.addPlayer(player);
            feedback(source, "PvP for you is now on.");
            return 1;
        }

        feedback(source, "You already have PvP on!");
        return 0;
    }

    private static int disablePvp(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        GameProfile player = source.getPlayer().getGameProfile();

        if (PvpWhitelist.contains(player)) {
            PvpWhitelist.removePlayer(player);
            feedback(source, "PvP for you is now off.");
            return 1;
        }

        feedback(source, "You already have PvP off!");
        return 0;
    }

    public static void feedback(ServerCommandSource source, String content) {
        source.sendFeedback(() -> Text.literal(content).formatted(Formatting.DARK_AQUA), false);
    }

    private static int pvpStatus(CommandContext<ServerCommandSource> ctx) {
        ServerCommandSource source = ctx.getSource();
        GameProfile player = source.getPlayer().getGameProfile();
        feedback(source, "PvP for you is " + (PvpWhitelist.contains(player) ? "on" : "off"));
        return 1;
    }

    private static int listPlayers(CommandContext<ServerCommandSource> ctx) {
        String[] playerNames = ctx.getSource().getServer().getPlayerNames();
        feedback(ctx.getSource(), "Players with PvP on: " + String.join(", ", PvpWhitelist.getPlayers()));
        return 1;
    }
}
