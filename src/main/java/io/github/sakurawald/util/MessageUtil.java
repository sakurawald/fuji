package io.github.sakurawald.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.TagParser;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import eu.pb4.placeholders.api.parsers.tag.TextTag;
import eu.pb4.placeholders.impl.textparser.MergedParser;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.handler.ResourceConfigHandler;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.pb4.placeholders.api.Placeholders.DEFAULT_PLACEHOLDER_PARSER;

@UtilityClass
public class MessageUtil {
    private static final MergedParser MERGED_PARSER = new MergedParser(
            new NodeParser[]{
                    DEFAULT_PLACEHOLDER_PARSER
                    , TagParser.QUICK_TEXT_WITH_STF
            });
    private static final FabricServerAudiences adventure = FabricServerAudiences.of(Fuji.SERVER);
    @Getter
    private static final Map<String, String> player2lang = new HashMap<>();
    @Getter
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

    private static void writeDefaultLanguageFiles() {
        new ResourceConfigHandler("lang/en_us.json").loadFromDisk();
        new ResourceConfigHandler("lang/zh_cn.json").loadFromDisk();
    }

    private static void loadLanguageIfAbsent(String lang) {
        if (lang2json.containsKey(lang)) return;

        InputStream is;
        try {
            is = FileUtils.openInputStream(Fuji.CONFIG_PATH.resolve("lang").resolve(lang + ".json").toFile());
            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(is)).getAsJsonObject();
            lang2json.put(lang, jsonObject);
            Fuji.LOGGER.info("Language {} loaded.", lang);
        } catch (IOException e) {
            Fuji.LOGGER.error("Failed to load language '{}'", lang);
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

        return player == null ? defaultLanguage : player2lang.get(player.getGameProfile().getName());
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
        Fuji.LOGGER.error(errorString);
        return errorString;
    }

    private static @NotNull String formatString(String string, Object... args) {
        if (args.length > 0) {
            return String.format(string, args);
        }
        return string;
    }

    public static void sendMessageToPlayerEntity(PlayerEntity player, String key, Object... args) {
        player.sendMessage(adventure.toNative(ofComponent(null, false, getString(player, key), args)));
    }


    /* This is the core method to map `String` into `Component`.
     *  All methods that return `Vomponent` are converted from this method.
     * */
    public static @NotNull Component ofComponent(@Nullable Audience audience, boolean isKey, String keyOrString, Object... args) {
        String string = isKey ? getString(audience, keyOrString, args) : keyOrString;

        PlaceholderContext placeholderContext;
        if (audience instanceof PlayerEntity playerEntity) {
            placeholderContext = PlaceholderContext.of(playerEntity);
        } else {
            placeholderContext = PlaceholderContext.of(Fuji.SERVER);
        }
        ParserContext parserContext = ParserContext.of(PlaceholderContext.KEY, placeholderContext);

        return MERGED_PARSER.parseText(TextNode.of(string), parserContext).asComponent();
    }

    public static @NotNull Component ofComponent(@Nullable Audience audience, String key, Object... args) {
        return ofComponent(audience, true, key, args);
    }

    public static @NotNull Text ofVomponent(@Nullable Audience audience, String key, Object... args) {
        return toVomponent(ofComponent(audience, key, args));
    }

    public static @NotNull Text ofVomponent(String str, Object... args) {
        return toVomponent(ofComponent(null, false, str, args));
    }

    public static @NotNull Text toVomponent(Component component) {
        return adventure.toNative(component);
    }

    public static List<Text> ofVomponents(@Nullable Audience audience, String key, Object... args) {
        String lines = getString(audience, key, args);

        List<Text> ret = new ArrayList<>();
        for (String line : lines.split("\n")) {
            ret.add(ofVomponent(line));
        }
        return ret;
    }

    public static void sendMessage(@NotNull Audience audience, String key, Object... args) {
        audience.sendMessage(ofComponent(audience, key, args));
    }

    public static void sendActionBar(@NotNull Audience audience, String key, Object... args) {
        audience.sendActionBar(ofComponent(audience, key, args));
    }

    public static void sendBroadcast(@NotNull String key, Object... args) {
        // fix: log broadcast for console
        Fuji.LOGGER.info(PlainTextComponentSerializer.plainText().serialize(ofComponent(null, key, args)));

        for (ServerPlayerEntity player : Fuji.SERVER.getPlayerManager().getPlayerList()) {
            sendMessage(player, key, args);
        }
    }
}
