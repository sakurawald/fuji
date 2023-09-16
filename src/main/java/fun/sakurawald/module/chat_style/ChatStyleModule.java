package fun.sakurawald.module.chat_style;

import com.google.common.collect.EvictingQueue;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.ServerMain;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.main_stats.MainStats;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Queue;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@Slf4j
public class ChatStyleModule {

    @SuppressWarnings("UnstableApiUsage")
    @Getter
    private static final Queue<Component> chatHistory = EvictingQueue.create(ConfigManager.configWrapper.instance().modules.chat_style.history.cache_size);
    private static final MiniMessage miniMessage = MiniMessage.builder().build();


    @SuppressWarnings("unused")
    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("chat")
                        .then(literal("format")
                                .then(argument("format", StringArgumentType.greedyString())
                                        .executes(ChatStyleModule::$format)
                                )));
    }

    private static int $format(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        String name = player.getGameProfile().getName();
        String format = StringArgumentType.getString(ctx, "format");
        ConfigManager.chatWrapper.instance().format.player2format.put(name, format);
        ConfigManager.chatWrapper.saveToDisk();
        return Command.SINGLE_SUCCESS;
    }


    private static Component resolvePositionTag(ServerPlayer source, Component component) {
        Component replacement = Component.text("%s (%d %d %d) %s".formatted(source.level().dimensionTypeId().location(),
                source.getBlockX(), source.getBlockY(), source.getBlockZ(), source.chunkPosition().toString())).color(NamedTextColor.GOLD);
        return component.replaceText(TextReplacementConfig.builder().match("(?<=^|\\s)pos(?=\\s|$)").replacement(replacement).build());
    }

    private static Component resolveItemTag(ServerPlayer source, Component component) {
        Component replacement =
                source.getMainHandItem().getDisplayName().asComponent().hoverEvent(source.getMainHandItem().asHoverEvent());
        return component.replaceText(TextReplacementConfig.builder().match("(?<=^|\\s)item(?=\\s|$)").replacement(replacement).build());
    }

    @SuppressWarnings("unused")
    private static String resolveMentionTag(ServerPlayer source, String str) {
        /* resolve player tag */
        ArrayList<ServerPlayer> mentionedPlayers = new ArrayList<>();
        for (ServerPlayer player : ServerMain.SERVER.getPlayerList().getPlayers()) {
            String name = player.getGameProfile().getName();
            // here we must continue so that mentionPlayers will not be added
            if (!str.contains(name)) continue;

            str = str.replace(name, "<aqua>%s</aqua>".formatted(name));
            mentionedPlayers.add(player);
        }

        /* run mention player task */
        new MentionPlayersTask(mentionedPlayers).startTask();
        return str;
    }

    public static void handleChatMessage(ServerPlayer source, String message) {
        /* resolve format */
        message = ConfigManager.chatWrapper.instance().format.player2format.getOrDefault(source.getGameProfile().getName(), message)
                .replace("%message%", message);
        message = resolveMentionTag(source, message);

        /* resolve stats */
        String format = ConfigManager.configWrapper.instance().modules.chat_style.format;
        format = format.replace("%message%", message);
        format = format.replace("%player%", source.getGameProfile().getName());
        MainStats stats = MainStats.uuid2stats.getOrDefault(source.getUUID().toString(), new MainStats());
        format = stats.update(source).resolve(format);

        /* resolve tags */
        Component component = miniMessage.deserialize(format, Formatter.date("date", LocalDateTime.now(ZoneId.systemDefault()))).asComponent();
        component = resolveItemTag(source, component);
        component = resolvePositionTag(source, component);
        chatHistory.add(component);
        // info so that it can be seen in the console
        log.info(PlainTextComponentSerializer.plainText().serialize(component));
        for (ServerPlayer player : ServerMain.SERVER.getPlayerList().getPlayers()) {
            player.sendMessage(component);
        }
    }

}
