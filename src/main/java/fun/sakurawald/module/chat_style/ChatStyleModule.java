package fun.sakurawald.module.chat_style;

import fun.sakurawald.ModMain;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.chat_history.ChatHistoryModule;
import fun.sakurawald.module.main_stats.MainStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("resource")
public class ChatStyleModule {
    private static final MiniMessage miniMessage = MiniMessage.builder().build();

    private static final ScheduledExecutorService mentionExecutor = Executors.newScheduledThreadPool(1);


    private static Component resolvePositionTag(ServerPlayer source, Component component) {
        Component replacement = Component.text("%s (%d %d %d) %s".formatted(source.level().dimensionTypeId().location(),
                source.getBlockX(), source.getBlockY(), source.getBlockZ(), source.chunkPosition().toString())).color(NamedTextColor.GOLD);
        return component.replaceText(TextReplacementConfig.builder().match("(?<=^|\\s)pos(?=\\s|$)").replacement(replacement).build());
    }

    private static Component resolveItemTag(ServerPlayer source, Component component) {
        ItemStack itemStack = source.getMainHandItem();
        Component replacement =
                itemStack.getDisplayName().asComponent().hoverEvent(source.getMainHandItem().asHoverEvent());
        return component.replaceText(TextReplacementConfig.builder().match("(?<=^|\\s)item(?=\\s|$)").replacement(replacement).build());
    }

    private static String resolveMentionTag(ServerPlayer source, String str) {
        /* resolve player tag */
        ArrayList<ServerPlayer> mentionedPlayers = new ArrayList<>();
        for (ServerPlayer player : ModMain.SERVER.getPlayerList().getPlayers()) {
            String name = player.getGameProfile().getName();
            if (!str.contains(name)) continue;

            str = str.replace(name, "<aqua>%s<aqua><reset>".formatted(name));
            mentionedPlayers.add(player);
        }

        /* run mention player task */
        MentionPlayersTask mentionPlayersTask = new MentionPlayersTask(mentionedPlayers);
        ScheduledFuture<?> scheduledFuture = mentionExecutor.scheduleAtFixedRate(mentionPlayersTask, 0, 1, TimeUnit.SECONDS);
        mentionPlayersTask.setScheduledFuture(scheduledFuture);

        return str;
    }

    public static void handleChatMessage(ServerPlayer source, String message) {
        /* resolve stats */
        String input = ConfigManager.configWrapper.instance().modules.chat_style.format;
        input = input.replace("%message%", message);
        input = resolveMentionTag(source, input);
        input = input.replace("%player%", source.getGameProfile().getName());
        MainStats stats = MainStats.uuid2stats.getOrDefault(source.getUUID().toString(), new MainStats());
        stats.update(source);
        input = stats.resolve(input);

        /* resolve tags */
        Component component = miniMessage.deserialize(input, Formatter.date("date", LocalDateTime.now(ZoneId.systemDefault()))).asComponent();
        component = resolveItemTag(source, component);
        component = resolvePositionTag(source, component);
        ChatHistoryModule.CACHE.add(component);
        for (ServerPlayer player : ModMain.SERVER.getPlayerList().getPlayers()) {
            player.sendMessage(component);
        }
    }
}
