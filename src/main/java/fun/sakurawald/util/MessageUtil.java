package fun.sakurawald.util;

import fun.sakurawald.ModMain;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MessageUtil {

    public static String resolve(String str, ServerPlayerEntity player) {
        return str.replace("%player%", player.getName().getString());
    }

    public static void broadcast(String string, Formatting formatting) {
        broadcast(Text.literal(string).formatted(formatting), false);
    }

    public static void broadcast(String string) {
        broadcast(string, false);
    }

    public static void broadcast(String string, boolean overlay) {
        broadcast(Text.of(string), overlay);
    }

    public static void broadcast(Text text) {
        broadcast(text, false);
    }

    public static void broadcast(Text text, boolean overlay) {
        ModMain.SERVER.getPlayerManager().broadcast(text, overlay);
    }

    public static void feedback(ServerCommandSource source, String string) {
        feedback(source, string, false);
    }

    public static void feedback(ServerCommandSource source, String string, Formatting formatting) {
        feedback(source, Text.literal(string).formatted(formatting), false);
    }

    public static void feedback(ServerCommandSource source, String string, boolean overlay) {
        feedback(source, Text.of(string), overlay);
    }

    public static void feedback(ServerCommandSource source, Text text) {
        feedback(source, text, false);
    }

    public static void feedback(ServerCommandSource source, Text text, boolean overlay) {
        source.sendFeedback(() -> text, overlay);
    }

    public static void message(ServerPlayerEntity player, String string, boolean overlay) {
        player.sendMessage(Text.of(resolve(string, player)), overlay);
    }

    public static void message(ServerPlayerEntity player, Text text, boolean overlay) {
        player.sendMessage(text, overlay);
    }
}
