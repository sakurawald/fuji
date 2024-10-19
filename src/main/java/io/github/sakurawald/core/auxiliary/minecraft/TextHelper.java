package io.github.sakurawald.core.auxiliary.minecraft;

import com.google.gson.JsonObject;
import com.mojang.brigadier.context.CommandContext;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.api.parsers.tag.TextTag;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.config.handler.impl.ResourceConfigurationHandler;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@UtilityClass
public class TextHelper {

    public static final Text TEXT_NEWLINE = Text.of("\n");
    public static final Text TEXT_SPACE = Text.of(" ");

    private static final NodeParser POWERFUL_PARSER = NodeParser.builder()
        .quickText()
        .simplifiedTextFormat()
        .globalPlaceholders()
        .markdown()
        .build();

    private static final NodeParser PLACEHOLDER_PARSER = NodeParser.builder()
        .globalPlaceholders().build();

    private static final Map<String, String> player2code = new HashMap<>();
    private static final Map<String, JsonObject> code2json = new HashMap<>();
    private static final JsonObject UNSUPPORTED_LANGUAGE_MARKER = new JsonObject();

    static {
        writeDefaultLanguageFilesIfAbsent();

        TagRegistry.registerDefault(
            TextTag.self(
                "newline",
                "formatting",
                true,
                (nodes, data, parser) -> new LiteralNode("\n")
            )
        );

    }

    private static void writeDefaultLanguageFilesIfAbsent() {
        for (String languageFile : ReflectionUtil.getGraph(ReflectionUtil.LANGUAGE_GRAPH_FILE_NAME)) {
            new ResourceConfigurationHandler("lang/" + languageFile).readStorage();
        }
    }

    /**
     * Clear the language file loaded into the memory.
     * Note that once the attempt to load a language file from storage is failed, a JsonObject marker named `UNSUPPORTED LANGUAGE` will be put into the map, leading the subsequent attempts simply return the marker.
     */
    public static void clearLoadedLanguageJsons() {
        code2json.clear();
    }

    public static void setClientSideLanguageCode(String playerName, String languageRepresentationUsedByMojang) {
        // mojang network protocol use a strange language representation, mojang use `en_us` instead of `en_US`
        player2code.put(playerName, convertToLanguageCode(languageRepresentationUsedByMojang));
    }

    private static void loadLanguageJsonIfAbsent(String languageCode) {
        if (code2json.containsKey(languageCode)) return;

        try {
            String languageFile = languageCode + ".json";
            ResourceConfigurationHandler resourceConfigurationHandler = new ResourceConfigurationHandler("lang/" + languageFile);

            //read it
            resourceConfigurationHandler.readStorage();

            code2json.put(languageCode, resourceConfigurationHandler.model().getAsJsonObject());
            LogUtil.info("language {} loaded.", languageCode);
        } catch (Exception e) {
            code2json.put(languageCode, UNSUPPORTED_LANGUAGE_MARKER);
            LogUtil.warn("failed to load language `{}`", languageCode);
        }
    }

    private String convertToLanguageCode(String input) {
        if (input == null || !input.contains("_")) {
            return input;
        }

        String[] parts = input.split("_");

        String language = parts[0].toLowerCase();
        String region = parts[1].toUpperCase();
        return language + "_" + region;
    }

    private @NotNull String getClientSideLanguageCode(@Nullable Object audience) {
        if (audience == null) return getDefaultLanguageCode();

        PlayerEntity player = switch (audience) {
            case ServerPlayerEntity serverPlayerEntity -> serverPlayerEntity;
            case PlayerEntity playerEntity -> playerEntity;
            case ServerCommandSource source when source.getPlayer() != null -> source.getPlayer();
            default -> null;
        };

        // always use default_language for non-player object.
        if (player == null) return getDefaultLanguageCode();

        return player2code.getOrDefault(player.getGameProfile().getName(), getDefaultLanguageCode());
    }

    private @NotNull JsonObject getLanguageJsonObject(String languageCode) {
        // load language object from disk for the first time
        loadLanguageJsonIfAbsent(languageCode);

        return code2json.get(languageCode);
    }


    public static @NotNull String getValue(@Nullable Object audience, String key) {
        String languageCode = getClientSideLanguageCode(audience);

        String value = getValue(languageCode, key);
        if (value != null) return value;

        // always fallback string for missing keys
        String fallbackValue = "(no key `%s` in language `%s`)".formatted(key, languageCode);
        LogUtil.warn("{} triggered by {}", fallbackValue, audience);
        return fallbackValue;
    }

    private static String getDefaultLanguageCode() {
        // allow user to write `en_us` in `config.json`.
        return convertToLanguageCode(Configs.configHandler.model().core.language.default_language);
    }

    private static boolean isDefaultLanguageCode(String languageCode) {
        return languageCode.equals(getDefaultLanguageCode());
    }

    private static @Nullable String getValue(String languageCode, String key) {
        /* get json */
        JsonObject languageJson = getLanguageJsonObject(languageCode);

        /* use fallback language if the client-side language is not supported in the server-side. */
        if (languageJson == UNSUPPORTED_LANGUAGE_MARKER) {
            languageCode = getDefaultLanguageCode();
            languageJson = getLanguageJsonObject(languageCode);
        }

        /* get value */
        if (languageJson.has(key)) {
            return languageJson.get(key).getAsString();
        }

        // use partial locale
        if (!isDefaultLanguageCode(languageCode)) {
            return getValue(getDefaultLanguageCode(), key);
        }

        // if the language key is missing in the default language, then we have nothing to do.
        return null;
    }

    public static @NotNull String getValue(@Nullable Object audience, String key, Object... args) {
        return resolveArgs(getValue(audience, key), args);
    }

    private static @NotNull String resolveArgs(@NotNull String string, Object... args) {
        if (args.length > 0) {
            try {
                return String.format(string, args);
            } catch (Exception e) {
                LogUtil.warn("""
                    Failed to resolve args for language value `{}` with args `{}`

                    It's like a syntax mistake in the language file.
                    """, string, args);
            }
        }
        return string;
    }

    public static @NotNull String resolvePlaceholder(@Nullable Object audience, String value) {
        return TextHelper.getText(PLACEHOLDER_PARSER, audience, false, value).getString();
    }

    /* This is the core method to map `String` into `Text`.
     *  All methods that return `Vomponent` are converted from this method.
     * */
    private static @NotNull Text getText(@NonNull NodeParser parser, @Nullable Object audience, boolean isKey, String keyOrValue, Object... args) {
        String value = isKey ? getValue(audience, keyOrValue) : keyOrValue;

        // resolve args
        value = resolveArgs(value, args);

        PlaceholderContext placeholderContext = makePlaceholderContext(audience);
        ParserContext parserContext = ParserContext.of(PlaceholderContext.KEY, placeholderContext);

        return parser.parseText(TextNode.of(value), parserContext);
    }

    private static @NotNull PlaceholderContext makePlaceholderContext(@Nullable Object audience) {
        /* extract the player from source */
        if (audience instanceof ServerCommandSource) {
            audience = ((ServerCommandSource) audience).getPlayer();
        }

        /* case type */
        PlaceholderContext placeholderContext;
        if (audience instanceof PlayerEntity playerEntity) {
            placeholderContext = PlaceholderContext.of(playerEntity);
        } else {
            placeholderContext = PlaceholderContext.of(ServerHelper.getDefaultServer());
        }

        return placeholderContext;
    }

    private static @NotNull Text getText(@Nullable Object audience, boolean isKey, String keyOrValue, Object... args) {
        return getText(POWERFUL_PARSER, audience, isKey, keyOrValue, args);
    }

    public static @NotNull Text getTextByKey(@Nullable Object audience, String key, Object... args) {
        return getText(audience, true, key, args);
    }

    public static String getKeywordValue(@Nullable Object audience, String keyword) {
        return getValue(audience, "keyword." + keyword);
    }

    public static MutableText getTextWithKeyword(@Nullable Object audience, String key, String keyword) {
        String replacement = getKeywordValue(audience, keyword);
        return Text.literal(getValue(audience, key, replacement));
    }

    public static @NotNull Text getTextByValue(@Nullable Object audience, String value, Object... args) {
        return getText(audience, false, value, args);
    }

    private static @NotNull List<Text> getTextList(@Nullable Object audience, boolean isKey, String keyOrValue) {
        String lines = isKey ? getValue(audience, keyOrValue) : keyOrValue;

        List<Text> ret = new ArrayList<>();
        for (String line : lines.split("\n|<newline>")) {
            ret.add(getTextByValue(audience, line));
        }
        return ret;
    }

    public static @NotNull List<Text> getTextListByKey(@Nullable Object audience, String key) {
        return getTextList(audience, true, key);
    }

    public static @NotNull List<Text> getTextListByValue(@Nullable Object audience, String value) {
        return getTextList(audience, false, value);
    }

    public static void sendMessageByKey(@NotNull Object audience, String key, Object... args) {
        Text text = getTextByKey(audience, key, args);

        /* extract the source */
        if (audience instanceof CommandContext<?> ctx) {
            audience = ctx.getSource();
        }

        /* dispatch by type */
        if (audience instanceof PlayerEntity playerEntity) {
            playerEntity.sendMessage(text);
            return;
        }

        if (audience instanceof ServerCommandSource serverCommandSource) {
            serverCommandSource.sendMessage(text);
            return;
        }

        LogUtil.error("""
            Can't send message to unknown audience type: {}
            Key: {}
            Args: {}
            """, audience == null ? null : audience.getClass().getName(), key, args);
    }

    public static void sendActionBarByKey(@NotNull ServerPlayerEntity player, String key, Object... args) {
        player.sendMessage(getTextByKey(player, key, args), true);
    }

    public static void sendBroadcastByKey(@NotNull String key, Object... args) {
        // fix: log broadcast for console
        Text text = getTextByKey(null, key, args);
        LogUtil.info(text.getString());

        for (ServerPlayerEntity player : ServerHelper.getDefaultServer().getPlayerManager().getPlayerList()) {
            TextHelper.sendMessageByKey(player, key, args);
        }
    }

    public static MutableText replaceBracketedText(Text text, String charSeq, Text replacement) {
        // verify the placeholder of replaceText
        if (!charSeq.startsWith("[") || !charSeq.endsWith("]")) {
            throw new IllegalArgumentException("The `charSeq` parameter must starts with '[' and ends with ']'");
        }

        return replaceText(text, charSeq, () -> replacement);
    }

    public static MutableText replaceText(Text text, String charSeq, Supplier<Text> replacementSupplier) {
        return replaceText0(text, charSeq, replacementSupplier, Text.empty(), new ArrayList<>());
    }

    private static String visitString(TextContent textContent) {
        StringBuilder stringBuilder = new StringBuilder();
        textContent.visit(string -> {
            stringBuilder.append(string);
            return Optional.empty();
        });
        return stringBuilder.toString();
    }

    private static MutableText replaceText0(Text text, String marker, Supplier<Text> replacement, MutableText builder, List<Style> stylePath) {
        /* pass down style */
        ArrayList<Style> newStylePath = new ArrayList<>(stylePath);
        newStylePath.add(text.getStyle());

        /* process the atom */
        splitText(text, marker, replacement, newStylePath).forEach(builder::append);

        /* iterate children */
        text.getSiblings().forEach(it -> replaceText0(it, marker, replacement, builder, newStylePath));
        return builder;
    }

    private static MutableText fillStyles(MutableText text, List<Style> stylePath) {
        stylePath.forEach(text::fillStyle);
        return text;
    }

    private static List<Text> splitText(Text text, String marker, Supplier<Text> replacementSupplier, List<Style> stylePath) {

        /* get the string */
        String string = visitString(text.getContent());

        /* get the split points */
        List<Integer> splitPoints = new ArrayList<>();
        int fromIndex = 0;
        while (fromIndex < string.length()) {
            int i = string.indexOf(marker, fromIndex);
            // break if no found the marker
            if (i == -1) break;

            splitPoints.add(i);
            fromIndex = i + marker.length();
        }

        /* construct result texts */
        List<Text> ret = new ArrayList<>();
        int beginIndex = 0;
        Text replacement = null;
        for (Integer splitPoint : splitPoints) {
            int endIndex = splitPoint;

            String part = string.substring(beginIndex, endIndex);

            // the part is empty, if the string starts with marker or ends with marker.
            if (!part.isEmpty()) {
                // add non-marker.
                MutableText mutableText = MutableText.of(PlainTextContent.of(part));
                fillStyles(mutableText, stylePath);
                ret.add(mutableText);
            }

            // replace the marker with replacement
            if (replacement == null) {
                replacement = replacementSupplier.get();
            }
            MutableText styledReplacement = replacement.copy();
            fillStyles(styledReplacement, stylePath);
            ret.add(styledReplacement);

            beginIndex = splitPoint + marker.length();
        }

        // handle the tail
        if (beginIndex < string.length()) {
            String part = string.substring(beginIndex);

            MutableText mutableText = MutableText.of(PlainTextContent.of(part));
            fillStyles(mutableText, stylePath);
            ret.add(mutableText);
        }

        return ret;
    }

}
