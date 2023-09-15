package fun.sakurawald.util;

import fun.sakurawald.ModMain;
import lombok.experimental.UtilityClass;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

@UtilityClass
public class MessageUtil {

    public static String resolve(String str, ServerPlayer player) {
        return str.replace("%player%", player.getName().getString());
    }

    public static void broadcast(String string, ChatFormatting formatting) {
        broadcast(Component.literal(string).withStyle(formatting), false);
    }

    public static void broadcast(String string) {
        broadcast(string, false);
    }

    public static void broadcast(String string, boolean overlay) {
        broadcast(Component.nullToEmpty(string), overlay);
    }

    public static void broadcast(Component text) {
        broadcast(text, false);
    }

    public static void broadcast(Component text, boolean overlay) {
        ModMain.SERVER.getPlayerList().broadcastSystemMessage(text, overlay);
    }

    public static void feedback(CommandSourceStack source, String string) {
        feedback(source, string, false);
    }

    public static void feedback(CommandSourceStack source, String string, ChatFormatting formatting) {
        feedback(source, Component.literal(string).withStyle(formatting), false);
    }

    public static void feedback(CommandSourceStack source, String string, boolean overlay) {
        feedback(source, Component.nullToEmpty(string), overlay);
    }

    public static void feedback(CommandSourceStack source, Component text) {
        feedback(source, text, false);
    }

    public static void feedback(CommandSourceStack source, Component text, boolean overlay) {
        source.sendSuccess(() -> text, overlay);
    }

    public static void message(ServerPlayer player, String string, boolean overlay) {
        player.displayClientMessage(Component.nullToEmpty(resolve(string, player)), overlay);
    }

    public static void message(ServerPlayer player, Component text, boolean overlay) {
        player.displayClientMessage(text, overlay);
    }
}
