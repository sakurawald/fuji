package io.github.sakurawald.module.chat_style;

import com.google.common.collect.EvictingQueue;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.ServerMain;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.chat_style.display.DisplayHelper;
import io.github.sakurawald.module.chat_style.mention.MentionPlayersJob;
import io.github.sakurawald.module.main_stats.MainStats;
import io.github.sakurawald.module.main_stats.MainStatsModule;
import io.github.sakurawald.util.MessageUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Queue;
import java.util.function.Supplier;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@SuppressWarnings("UnstableApiUsage")
@Slf4j
public class ChatStyleModule extends AbstractModule {

    private final MiniMessage miniMessage = MiniMessage.builder().build();
    private final MainStatsModule mainStatsModule = ModuleManager.getOrNewInstance(MainStatsModule.class);
    @Getter
    private Queue<Component> chatHistory;

    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.chat_style.enable;
    }

    @Override
    public void onInitialize() {
        chatHistory = EvictingQueue.create(ConfigManager.configWrapper.instance().modules.chat_style.history.cache_size);

        CommandRegistrationCallback.EVENT.register(this::registerCommand);
    }

    @Override
    public void onReload() {
        EvictingQueue<Component> newQueue = EvictingQueue.create(ConfigManager.configWrapper.instance().modules.chat_style.history.cache_size);
        newQueue.addAll(chatHistory);
        chatHistory.clear();
        chatHistory = newQueue;
    }

    @SuppressWarnings("unused")
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
                Commands.literal("chat")
                        .then(literal("format")
                                .then(argument("format", StringArgumentType.greedyString())
                                        .executes(this::$format)
                                )));
    }

    private int $format(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        String name = player.getGameProfile().getName();
        String format = StringArgumentType.getString(ctx, "format");
        ConfigManager.chatWrapper.instance().format.player2format.put(name, format);
        ConfigManager.chatWrapper.saveToDisk();
        return Command.SINGLE_SUCCESS;
    }


    private Component resolvePositionTag(ServerPlayer player, Component component) {
        Component replacement = Component.text("%s (%d %d %d) %s".formatted(player.serverLevel().dimension().location(),
                player.getBlockX(), player.getBlockY(), player.getBlockZ(), player.chunkPosition().toString())).color(NamedTextColor.GOLD);
        return component.replaceText(TextReplacementConfig.builder().match("(?<=^|\\s)pos(?=\\s|$)").replacement(replacement).build());
    }

    private Component resolveItemTag(ServerPlayer player, Component component) {
        String displayUUID = DisplayHelper.createItemDisplay(player);
        Component replacement =
                player.getMainHandItem().getDisplayName().asComponent()
                        .hoverEvent(MessageUtil.ofComponent(player, "display.click.prompt"))
                        .clickEvent(displayCallback(displayUUID));
        return component.replaceText(TextReplacementConfig.builder().match("(?<=^|\\s)item(?=\\s|$)").replacement(replacement).build());
    }

    private Component resolveInvTag(ServerPlayer player, Component component) {
        String displayUUID = DisplayHelper.createInventoryDisplay(player);
        Component replacement =
                MessageUtil.ofComponent(player, "display.inventory.text")
                        .hoverEvent(MessageUtil.ofComponent(player, "display.click.prompt"))
                        .clickEvent(displayCallback(displayUUID));
        return component.replaceText(TextReplacementConfig.builder().match("(?<=^|\\s)inv(?=\\s|$)").replacement(replacement).build());
    }

    private Component resolveEnderTag(ServerPlayer player, Component component) {
        String displayUUID = DisplayHelper.createEnderChestDisplay(player);
        Component replacement =
                MessageUtil.ofComponent(player, "display.ender_chest.text")
                        .hoverEvent(MessageUtil.ofComponent(player, "display.click.prompt"))
                        .clickEvent(displayCallback(displayUUID));
        return component.replaceText(TextReplacementConfig.builder().match("(?<=^|\\s)ender(?=\\s|$)").replacement(replacement).build());
    }

    @NotNull
    private ClickEvent displayCallback(String displayUUID) {
        return ClickEvent.callback(audience -> {
            if (audience instanceof CommandSourceStack css && css.getPlayer() != null) {
                DisplayHelper.viewDisplay(css.getPlayer(), displayUUID);
            }
        }, ClickCallback.Options.builder().lifetime(Duration.of(ConfigManager.configWrapper.instance().modules.chat_style.display.expiration_duration_s, ChronoUnit.SECONDS))
                .uses(Integer.MAX_VALUE).build());
    }

    @SuppressWarnings("unused")
    private String resolveMentionTag(ServerPlayer player, String str) {
        /* resolve player tag */
        ArrayList<ServerPlayer> mentionedPlayers = new ArrayList<>();

        String[] playerNames = ServerMain.SERVER.getPlayerNames();
        // fix: mention the longest name first
        Arrays.sort(playerNames, Comparator.comparingInt(String::length).reversed());

        for (String playerName : playerNames) {
            // here we must continue so that mentionPlayers will not be added
            if (!str.contains(playerName)) continue;
            str = str.replace(playerName, "<aqua>%s</aqua>".formatted(playerName));
            mentionedPlayers.add(ServerMain.SERVER.getPlayerList().getPlayerByName(playerName));
        }

        /* run mention player task */
        if (!mentionedPlayers.isEmpty()) {
            MentionPlayersJob.scheduleJob(mentionedPlayers);
        }
        return str;
    }

    public void broadcastChatMessage(ServerPlayer player, String message) {
        /* resolve format */
        message = ConfigManager.chatWrapper.instance().format.player2format.getOrDefault(player.getGameProfile().getName(), message)
                .replace("%message%", message);
        message = resolveMentionTag(player, message);
        String format = ConfigManager.configWrapper.instance().modules.chat_style.format;
        format = format.replace("%message%", message);
        format = format.replace("%player%", player.getGameProfile().getName());

        /* resolve stats */
        if (mainStatsModule != null) {
            MainStats stats = MainStats.uuid2stats.getOrDefault(player.getUUID().toString(), new MainStats());
            format = stats.update(player).resolve(ServerMain.SERVER, format);
        }

        /* resolve tags */
        Component component = miniMessage.deserialize(format, Formatter.date("date", LocalDateTime.now(ZoneId.systemDefault()))).asComponent();
        component = resolveItemTag(player, component);
        component = resolveInvTag(player, component);
        component = resolveEnderTag(player, component);
        component = resolvePositionTag(player, component);
        chatHistory.add(component);
        // info so that it can be seen in the console
        log.info(PlainTextComponentSerializer.plainText().serialize(component));
        for (ServerPlayer receiver : ServerMain.SERVER.getPlayerList().getPlayers()) {
            receiver.sendMessage(component);
        }
    }

}
