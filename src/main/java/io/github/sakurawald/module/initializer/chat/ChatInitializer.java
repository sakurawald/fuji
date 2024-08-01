package io.github.sakurawald.module.initializer.chat;

import com.google.common.collect.EvictingQueue;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.command.adapter.wrapper.GreedyString;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.config.handler.ObjectConfigHandler;
import io.github.sakurawald.config.model.ChatModel;
import io.github.sakurawald.module.common.job.impl.MentionPlayersJob;
import io.github.sakurawald.module.common.structure.RegexRewriteEntry;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.chat.display.DisplayHelper;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.minecraft.PermissionHelper;
import io.github.sakurawald.util.minecraft.ServerHelper;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

public class ChatInitializer extends ModuleInitializer {
    public static final ConfigHandler<ChatModel> chatHandler = new ObjectConfigHandler<>("chat.json", ChatModel.class);

    private final MiniMessage miniMessage = MiniMessage.builder().build();

    private Map<Pattern, String> patterns;

    @Getter
    private Queue<Component> chatHistory;

    @Override
    public void onInitialize() {
        chatHandler.loadFromDisk();
        chatHistory = EvictingQueue.create(Configs.configHandler.model().modules.chat.history.buffer_size);

        compilePatterns();

        registerItemPlaceholder();
        registerInvPlaceholder();
        registerEnderPlaceholder();
        registerPosPlaceholder();
        registerPrefixPlaceholder();
        registerSuffixPlaceholder();
    }

    @Override
    public void onReload() {
        chatHandler.loadFromDisk();

        EvictingQueue<Component> newQueue = EvictingQueue.create(Configs.configHandler.model().modules.chat.history.buffer_size);
        newQueue.addAll(chatHistory);
        chatHistory.clear();
        chatHistory = newQueue;

        compilePatterns();
    }

    private void compilePatterns() {
        patterns = new HashMap<>();

        for (RegexRewriteEntry regexRewriteEntry : Configs.configHandler.model().modules.chat.rewrite.regex) {
            patterns.put(java.util.regex.Pattern.compile(regexRewriteEntry.regex), regexRewriteEntry.replacement);
        }

    }


    private void registerEnderPlaceholder() {
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "ender"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid();

                    ServerPlayerEntity player = ctx.player();
                    String displayUUID = DisplayHelper.createEnderChestDisplay(player);
                    Component replacement =
                            MessageHelper.ofComponent(player, "display.ender_chest.text")
                                    .hoverEvent(MessageHelper.ofComponent(player, "display.click.prompt"))
                                    .clickEvent(buildDisplayClickEvent(displayUUID));
                    return PlaceholderResult.value(MessageHelper.toText(replacement));
                });
    }

    private void registerInvPlaceholder() {
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "inv"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid();

                    ServerPlayerEntity player = ctx.player();
                    String displayUUID = DisplayHelper.createInventoryDisplay(player);
                    Component replacement =
                            MessageHelper.ofComponent(player, "display.inventory.text")
                                    .hoverEvent(MessageHelper.ofComponent(player, "display.click.prompt"))
                                    .clickEvent(buildDisplayClickEvent(displayUUID));

                    return PlaceholderResult.value(MessageHelper.toText(replacement));
                });
    }

    public void registerItemPlaceholder() {
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "item"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid();

                    ServerPlayerEntity player = ctx.player();
                    String displayUUID = DisplayHelper.createItemDisplay(player);

                    Component component =
                            MessageHelper.ofComponent(player, "display.item.text")
                                    .replaceText(builder -> builder.matchLiteral("[item]").replacement(Component.translatable(player.getMainHandStack().getTranslationKey())))
                                    .hoverEvent(MessageHelper.ofComponent(player, "display.click.prompt"))
                                    .clickEvent(buildDisplayClickEvent(displayUUID));
                    return PlaceholderResult.value(MessageHelper.toText(component));
                });
    }

    public void registerPosPlaceholder() {
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "pos"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid();

                    ServerPlayerEntity player = ctx.player();

                    int x = player.getBlockX();
                    int y = player.getBlockY();
                    int z = player.getBlockZ();
                    String dim_name = player.getWorld().getRegistryKey().getValue().toString();
                    String dim_display_name = MessageHelper.getString(player, dim_name);

                    String clickCommand = MessageHelper.getString(player, "chat.xaero_waypoint_add.command", x, y, z, dim_name.replaceAll(":", "\\$"));

                    String hoverString = MessageHelper.getString(player, "chat.current_pos");
                    switch (dim_name) {
                        case "minecraft:overworld":
                            hoverString += "\n" + MessageHelper.getString(player, "minecraft:the_nether")
                                    + ": %d %s %d".formatted(x / 8, y, z / 8);
                            break;
                        case "minecraft:the_nether":
                            hoverString += "\n" + MessageHelper.getString(player, "minecraft:overworld")
                                    + ": %d %s %d".formatted(x * 8, y, z * 8);
                            break;
                    }

                    Component component = MessageHelper.ofComponent(player, true, "placeholder.pos", x, y, z, dim_display_name)
                            .clickEvent(ClickEvent.runCommand(clickCommand))
                            .hoverEvent(Component.text(hoverString + "\n").append(MessageHelper.ofComponent(player, "chat.xaero_waypoint_add")));

                    return PlaceholderResult.value(MessageHelper.toText(component));
                });

    }

    private void registerPrefixPlaceholder() {
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_prefix"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid();

                    ServerPlayerEntity player = ctx.player();
                    String prefix = PermissionHelper.getPrefix(player);
                    return PlaceholderResult.value(MessageHelper.ofText(prefix));
                });
    }

    private void registerSuffixPlaceholder() {
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_suffix"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid();

                    ServerPlayerEntity player = ctx.player();
                    String prefix = PermissionHelper.getSuffix(player);
                    return PlaceholderResult.value(MessageHelper.ofText(prefix));
                });
    }

    @Command("chat format set")
    private int $format(@CommandSource ServerPlayerEntity player, GreedyString format) {
        /* save the format*/
        String name = player.getGameProfile().getName();
        String $format = format.getString();
        chatHandler.model().format.player2format.put(name, $format);
        chatHandler.saveToDisk();

        /* feedback */
        $format = MessageHelper.getString(player, "chat.format.set").replace("%s", $format);
        Component component = miniMessage.deserialize($format).asComponent()
                .replaceText(builder -> builder.match("%message%").replacement(MessageHelper.ofComponent(player, "chat.format.show")));

        player.sendMessage(component);
        return CommandHelper.Return.SUCCESS;
    }

    @Command("chat format reset")
    private int $reset(@CommandSource ServerPlayerEntity player) {
        String name = player.getGameProfile().getName();
        chatHandler.model().format.player2format.remove(name);
        chatHandler.saveToDisk();
        MessageHelper.sendMessage(player, "chat.format.reset");
        return CommandHelper.Return.SUCCESS;
    }


    @NotNull
    private ClickEvent buildDisplayClickEvent(String displayUUID) {
        return ClickEvent.callback(audience -> {
            if (audience instanceof ServerCommandSource css && css.getPlayer() != null) {
                DisplayHelper.viewDisplay(css.getPlayer(), displayUUID);
            }
        }, ClickCallback.Options.builder().lifetime(Duration.of(Configs.configHandler.model().modules.chat.display.expiration_duration_s, ChronoUnit.SECONDS))
                .uses(Integer.MAX_VALUE).build());
    }

    private String resolveMentionTag(@NotNull String string) {
        /* resolve player tag */
        ArrayList<ServerPlayerEntity> mentionedPlayers = new ArrayList<>();

        String[] playerNames = ServerHelper.getDefaultServer().getPlayerNames();
        // fix: mention the longest name first
        Arrays.sort(playerNames, Comparator.comparingInt(String::length).reversed());

        for (String playerName : playerNames) {
            // here we must continue so that mentionPlayers will not be added
            if (!string.contains(playerName)) continue;
            string = string.replace(playerName, "<aqua>%s</aqua>".formatted(playerName));
            mentionedPlayers.add(ServerHelper.getDefaultServer().getPlayerManager().getPlayer(playerName));
        }

        /* run mention player task */
        if (!mentionedPlayers.isEmpty()) {
            MentionPlayersJob.scheduleJob(mentionedPlayers);
        }

        return string;
    }

    public String resolvePatterns(String string) {
        for (Map.Entry<Pattern, String> entry : patterns.entrySet()) {
            string = entry.getKey().matcher(string).replaceAll(entry.getValue());
        }
        return string;
    }

    public @NotNull Text parseText(@NotNull ServerPlayerEntity player, String message) {
        /* parse message */
        message = resolvePatterns(message);
        message = resolveMentionTag(message);
        message = chatHandler.model().format.player2format.getOrDefault(player.getGameProfile().getName(), message)
                .replace("%message%", message);

        /* parse format */
        String format = Configs.configHandler.model().modules.chat.format;

        /* combine */
        String string = format.replace("%message%", message);
        return MessageHelper.ofText(player, false, string);
    }

}