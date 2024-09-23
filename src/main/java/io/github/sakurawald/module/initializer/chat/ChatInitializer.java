package io.github.sakurawald.module.initializer.chat;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.config.transformer.impl.MoveFileIntoModuleConfigDirectoryTransformer;
import io.github.sakurawald.core.job.impl.MentionPlayersJob;
import io.github.sakurawald.core.structure.RegexRewriteEntry;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.chat.config.model.ChatConfigModel;
import io.github.sakurawald.module.initializer.chat.config.model.ChatFormatModel;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ChatInitializer extends ModuleInitializer {

    private static final BaseConfigurationHandler<ChatFormatModel> chatFormatHandler = new ObjectConfigurationHandler<>("chat.json", ChatFormatModel.class)
        .addTransformer(new MoveFileIntoModuleConfigDirectoryTransformer(Fuji.CONFIG_PATH.resolve("chat.json"), ChatInitializer.class));

    public static final BaseConfigurationHandler<ChatConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, ChatConfigModel.class);

    private static Map<Pattern, String> patterns;

    @Override
    public void onInitialize() {
        compilePatterns();

        registerPosPlaceholder();
        registerPrefixPlaceholder();
        registerSuffixPlaceholder();
    }

    @Override
    public void onReload() {
        compilePatterns();
    }

    private void compilePatterns() {
        patterns = new HashMap<>();
        for (RegexRewriteEntry regexRewriteEntry : config.getModel().rewrite.regex) {
            patterns.put(java.util.regex.Pattern.compile(regexRewriteEntry.regex), regexRewriteEntry.replacement);
        }
    }

    private void registerPosPlaceholder() {
        Placeholders.register(
            Identifier.of(Fuji.MOD_ID, "pos"),
            (ctx, arg) -> {
                if (ctx.player() == null) PlaceholderResult.invalid();
                ServerPlayerEntity player = ctx.player();

                int x = player.getBlockX();
                int y = player.getBlockY();
                int z = player.getBlockZ();
                String dim_name = player.getWorld().getRegistryKey().getValue().toString();
                String dim_display_name = LocaleHelper.getValue(player, dim_name);
                String hoverString = LocaleHelper.getValue(player, "chat.current_pos");
                switch (dim_name) {
                    case "minecraft:overworld":
                        hoverString += "\n" + LocaleHelper.getValue(player, "minecraft:the_nether")
                            + ": %d %s %d".formatted(x / 8, y, z / 8);
                        break;
                    case "minecraft:the_nether":
                        hoverString += "\n" + LocaleHelper.getValue(player, "minecraft:overworld")
                            + ": %d %s %d".formatted(x * 8, y, z * 8);
                        break;
                }

                String clickCommand = LocaleHelper.getValue(player, "chat.xaero_waypoint_add.command");

                Text text = LocaleHelper.getTextByKey(player, "placeholder.pos", x, y, z, dim_display_name)
                    .copy()
                    .fillStyle(Style.EMPTY
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Text.literal(hoverString + "\n")
                                .append(LocaleHelper.getTextByKey(player, "chat.xaero_waypoint_add"))
                        ))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand))
                    );

                return PlaceholderResult.value(text);
            });

    }

    private void registerPrefixPlaceholder() {
        Placeholders.register(
            Identifier.of(Fuji.MOD_ID, "player_prefix"),
            (ctx, arg) -> {
                if (ctx.player() == null) PlaceholderResult.invalid();

                ServerPlayerEntity player = ctx.player();
                String prefix = PermissionHelper.getPrefix(player.getUuid());
                return PlaceholderResult.value(LocaleHelper.getTextByValue(player, prefix));
            });
    }

    private void registerSuffixPlaceholder() {
        Placeholders.register(
            Identifier.of(Fuji.MOD_ID, "player_suffix"),
            (ctx, arg) -> {
                if (ctx.player() == null) PlaceholderResult.invalid();

                ServerPlayerEntity player = ctx.player();
                String prefix = PermissionHelper.getSuffix(player.getUuid());
                return PlaceholderResult.value(LocaleHelper.getTextByValue(player, prefix));
            });
    }

    @CommandNode("chat format set")
    private static int $format(@CommandSource ServerPlayerEntity player, GreedyString format) {
        /* save the format*/
        String name = player.getGameProfile().getName();
        String $format = format.getValue();
        chatFormatHandler.getModel().format.player2format.put(name, $format);
        chatFormatHandler.writeStorage();

        /* feedback */
        $format = LocaleHelper.getValue(player, "chat.format.set").replace("%s", $format);
        $format = $format.replace("%message%", LocaleHelper.getValue(player, "chat.format.show"));
        Text text = LocaleHelper.getTextByValue(null, $format);

        player.sendMessage(text);
        return CommandHelper.Return.SUCCESS;
    }

    @CommandNode("chat format reset")
    private static int $reset(@CommandSource ServerPlayerEntity player) {
        String name = player.getGameProfile().getName();
        chatFormatHandler.getModel().format.player2format.remove(name);
        chatFormatHandler.writeStorage();
        LocaleHelper.sendMessageByKey(player, "chat.format.reset");
        return CommandHelper.Return.SUCCESS;
    }


    private static String resolveMentionTag(@NotNull String string) {
        /* resolve player tag */
        List<ServerPlayerEntity> mentionedPlayers = new ArrayList<>();

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
            MentionPlayersJob.requestJob(config.getModel().mention_player, mentionedPlayers);
        }

        return string;
    }

    private static String resolvePatterns(String string) {
        for (Map.Entry<Pattern, String> entry : patterns.entrySet()) {
            string = entry.getKey().matcher(string).replaceAll(entry.getValue());
        }
        return string;
    }

    public static @NotNull Text parseText(@NotNull ServerPlayerEntity player, String message) {
        /* parse message */
        message = resolvePatterns(message);
        message = resolveMentionTag(message);
        message = chatFormatHandler.getModel().format.player2format.getOrDefault(player.getGameProfile().getName(), message)
            .replace("%message%", message);

        /* parse format */
        String format = config.getModel().format;

        /* combine */
        String string = format.replace("%message%", message);
        return LocaleHelper.getTextByValue(player, string);
    }

}
