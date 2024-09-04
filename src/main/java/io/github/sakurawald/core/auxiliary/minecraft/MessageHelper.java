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
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.config.handler.impl.ResourceConfigHandler;
import io.github.sakurawald.core.auxiliary.LogUtil;
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
public class MessageHelper {
    public static final NodeParser POWERFUL_PARSER = NodeParser.builder()
            .quickText()
            .simplifiedTextFormat()
            .globalPlaceholders()
            .markdown()
            .build();

    public static final NodeParser PLACEHOLDER_PARSER = NodeParser.builder()
            .globalPlaceholders().build();

    private static final FabricServerAudiences adventure = FabricServerAudiences.of(ServerHelper.getDefaultServer());
    private static final Map<String, String> player2lang = new HashMap<>();
    private static final Map<String, JsonObject> lang2json = new HashMap<>();
    private static final JsonObject UNSUPPORTED_LANGUAGE = new JsonObject();

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

    // clear the map to remove `UNSUPPORTED LANGUAGE`
    public static void forgetLoadedLanguages() {
        lang2json.clear();
    }

    public static void setClientSideLanguage(String playerName, String language) {
        player2lang.put(playerName, language);
    }

    private static void writeDefaultLanguageFiles() {
        new ResourceConfigHandler("lang/en_us.json").loadFromDisk();
        new ResourceConfigHandler("lang/zh_cn.json").loadFromDisk();
        new ResourceConfigHandler("lang/zh_tw.json").loadFromDisk();
    }

    private static void loadLanguageIfAbsent(String lang) {
        if (lang2json.containsKey(lang)) return;

        InputStream is;
        try {
            is = FileUtils.openInputStream(Fuji.CONFIG_PATH.resolve("lang").resolve(lang + ".json").toFile());
            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            lang2json.put(lang, jsonObject);
            LogUtil.info("Language {} loaded.", lang);
        } catch (IOException e) {
            LogUtil.error("Failed to load language '{}'", lang);
            lang2json.put(lang, UNSUPPORTED_LANGUAGE);
        }
    }

    private @NotNull String getClientSideLanguage(@Nullable Object audience) {
        String defaultLanguage = Configs.configHandler.model().common.language.default_language;

        if (audience == null) {
            return defaultLanguage;
        }

        PlayerEntity player = switch (audience) {
            case ServerPlayerEntity serverPlayerEntity -> serverPlayerEntity;
            case PlayerEntity playerEntity -> playerEntity;
            case ServerCommandSource source when source.getPlayer() != null -> source.getPlayer();
            default -> null;
        };

        return player == null ? defaultLanguage : player2lang.getOrDefault(player.getGameProfile().getName(), defaultLanguage);
    }

    private @NotNull JsonObject getLanguage(String lang) {
        // if target language is missing, we fall back to default_language
        if (!lang2json.containsKey(lang) && lang2json.get(lang) == UNSUPPORTED_LANGUAGE) {
            lang = Configs.configHandler.model().common.language.default_language;
        }

        loadLanguageIfAbsent(lang);
        return lang2json.get(lang);
    }

    public static @NotNull String getString(@Nullable Object audience, String key, Object... args) {
        /* get lang */
        String lang = getClientSideLanguage(audience);

        /* get json */
        JsonObject json = getLanguage(lang);

        /* get value */
        if (json.has(key)) {
            String value = json.get(key).getAsString();
            return formatString(value, args);
        }

        String errorString = "Language '%s' miss the key '%s'".formatted(lang, key);
        LogUtil.error(errorString);
        return errorString;
    }

    private static @NotNull String formatString(@NotNull String string, Object @NotNull ... args) {
        if (args.length > 0) {
            return String.format(string, args);
        }
        return string;
    }

    public static void sendMessageToPlayerEntity(@NotNull PlayerEntity player, String key, Object... args) {
        player.sendMessage(adventure.toNative(ofComponent(null, false, getString(player, key), args)));
    }

    /* This is the core method to map `String` into `Component`.
     *  All methods that return `Vomponent` are converted from this method.
     * */
    public static @NotNull Text ofText(@NonNull NodeParser parser, @Nullable Audience audience, boolean isKey, String keyOrString, Object... args) {
        String string = isKey ? getString(audience, keyOrString, args) : keyOrString;

        PlaceholderContext placeholderContext;
        if (audience instanceof PlayerEntity playerEntity) {
            placeholderContext = PlaceholderContext.of(playerEntity);
        } else {
            placeholderContext = PlaceholderContext.of(ServerHelper.getDefaultServer());
        }
        ParserContext parserContext = ParserContext.of(PlaceholderContext.KEY, placeholderContext);

        return parser.parseText(TextNode.of(string), parserContext);
    }

    public static @NotNull Text ofText(@Nullable Audience audience, boolean isKey, String keyOrString, Object... args) {
        return ofText(POWERFUL_PARSER, audience, isKey, keyOrString, args);
    }

    public static @NotNull String ofString(@Nullable Audience audience, String string) {
        return PlainTextComponentSerializer.plainText().serialize(MessageHelper.ofText(PLACEHOLDER_PARSER, audience, false, string).asComponent());
    }


    public static @NotNull Text ofText(@Nullable Audience audience, String key, Object... args) {
        return ofText(audience, true, key, args);
    }

    public static @NotNull Text ofText(String str, Object... args) {
        return ofText(null, false, str, args);
    }

    public static @NotNull List<Text> ofTextList(@Nullable Audience audience, boolean isKey, String keyOrString, Object... args) {
        String lines = isKey ? getString(audience, keyOrString, args) : keyOrString;

        List<Text> ret = new ArrayList<>();
        for (String line : lines.split("\n|<newline>")) {
            ret.add(ofText(line));
        }
        return ret;
    }

    public static @NotNull List<Text> ofTextList(@Nullable Audience audience, String key, Object... args) {
        return ofTextList(audience, true, key, args);
    }

    public static @NotNull Text toText(@NotNull Component component) {
        return adventure.toNative(component);
    }

    public static @NotNull Component ofComponent(@Nullable Audience audience, boolean isKey, String keyOrString, Object... args) {
        return ofText(audience, isKey, keyOrString, args).asComponent();
    }

    public static @NotNull Component ofComponent(@Nullable Audience audience, String key, Object... args) {
        return ofComponent(audience, true, key, args);
    }

    public static void sendMessage(@NotNull Audience audience, String key, Object... args) {
        audience.sendMessage(ofComponent(audience, key, args));
    }

    public static void sendActionBar(@NotNull Audience audience, String key, Object... args) {
        audience.sendActionBar(ofComponent(audience, key, args));
    }

    public static void sendBroadcast(@NotNull String key, Object... args) {
        // fix: log broadcast for console
        LogUtil.info(PlainTextComponentSerializer.plainText().serialize(ofComponent(null, key, args)));

        for (ServerPlayerEntity player : ServerHelper.getDefaultServer().getPlayerManager().getPlayerList()) {
            MessageHelper.sendMessage(player, key, args);
        }
    }
}
