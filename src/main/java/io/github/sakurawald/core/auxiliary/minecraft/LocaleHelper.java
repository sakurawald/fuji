package io.github.sakurawald.core.auxiliary.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.api.parsers.tag.TextTag;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.config.handler.impl.ResourceConfigHandler;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class LocaleHelper {

    private static final NodeParser POWERFUL_PARSER = NodeParser.builder()
        .quickText()
        .simplifiedTextFormat()
        .globalPlaceholders()
        .markdown()
        .build();

    private static final NodeParser PLACEHOLDER_PARSER = NodeParser.builder()
        .globalPlaceholders().build();

    private static final FabricServerAudiences ADVENTURE_INSTANCE = FabricServerAudiences.of(ServerHelper.getDefaultServer());

    private static final Map<String, String> player2code = new HashMap<>();
    private static final Map<String, JsonObject> code2json = new HashMap<>();
    private static final JsonObject UNSUPPORTED_LANGUAGE_MARKER = new JsonObject();

    static {
        writeDefaultLanguageFiles();

        TagRegistry.registerDefault(
            TextTag.self(
                "newline",
                "formatting",
                true,
                (nodes, data, parser) -> new LiteralNode("\n")
            )
        );

    }

    private static void writeDefaultLanguageFiles() {
        for (String languageFile : ReflectionUtil.getGraph(ReflectionUtil.LANGUAGE_GRAPH_FILE_NAME)) {
            new ResourceConfigHandler("lang/" + languageFile).loadFromDisk();
        }
    }

    // clear the map to remove `UNSUPPORTED LANGUAGE`
    public static void clearLoadedLanguageJsons() {
        code2json.clear();
    }

    public static void setClientSideLanguageCode(String playerName, String languageRepresentationUsedByMojang) {
        // mojang network protocol use a strange language representation, mojang use `en_us` instead of `en_US`
        player2code.put(playerName, convertToLanguageCode(languageRepresentationUsedByMojang));
    }

    private static void loadLanguageJsonIfAbsent(String languageCode) {
        if (code2json.containsKey(languageCode)) return;

        InputStream is;
        try {
            is = FileUtils.openInputStream(Fuji.CONFIG_PATH.resolve("lang").resolve(languageCode + ".json").toFile());

            code2json.put(languageCode, JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject());
            LogUtil.info("language {} loaded.", languageCode);
        } catch (IOException e) {
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

    private static @NotNull String resolveArgs(@NotNull String string, Object @NotNull ... args) {
        if (args.length > 0) {
            return String.format(string, args);
        }
        return string;
    }

    public static @NotNull String resolvePlaceholder(@Nullable Object audience, String value) {
        Component component = LocaleHelper.getText(PLACEHOLDER_PARSER, audience, false, value).asComponent();
        return flatten(component);
    }

    /* This is the core method to map `String` into `Component`.
     *  All methods that return `Vomponent` are converted from this method.
     * */
    private static @NotNull Text getText(@NonNull NodeParser parser, @Nullable Object audience, boolean isKey, String keyOrValue, Object... args) {
        String value = isKey ? getValue(audience, keyOrValue) : keyOrValue;

        // resolve args
        value = resolveArgs(value, args);

        PlaceholderContext placeholderContext;
        if (audience instanceof PlayerEntity playerEntity) {
            placeholderContext = PlaceholderContext.of(playerEntity);
        } else {
            placeholderContext = PlaceholderContext.of(ServerHelper.getDefaultServer());
        }
        ParserContext parserContext = ParserContext.of(PlaceholderContext.KEY, placeholderContext);

        return parser.parseText(TextNode.of(value), parserContext);
    }

    private static @NotNull Text getText(@Nullable Object audience, boolean isKey, String keyOrValue, Object... args) {
        return getText(POWERFUL_PARSER, audience, isKey, keyOrValue, args);
    }

    public static @NotNull Text getTextByKey(@Nullable Object audience, String key, Object... args) {
        return getText(audience, true, key, args);
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

    public static @NotNull Text toText(@NotNull Component component) {
        return ADVENTURE_INSTANCE.toNative(component);
    }

    public static String flatten(@NotNull Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static void sendMessageByKey(@NotNull Audience audience, String key, Object... args) {
        audience.sendMessage(getTextByKey(audience, key, args));
    }

    public static void sendActionBarByKey(@NotNull Audience audience, String key, Object... args) {
        audience.sendActionBar(getTextByKey(audience, key, args));
    }

    public static void sendBroadcastByKey(@NotNull String key, Object... args) {
        // fix: log broadcast for console
        Component component = getTextByKey(null, key, args).asComponent();
        LogUtil.info(flatten(component));

        for (ServerPlayerEntity player : ServerHelper.getDefaultServer().getPlayerManager().getPlayerList()) {
            LocaleHelper.sendMessageByKey(player, key, args);
        }
    }

}
