package io.github.sakurawald.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.node.TextNode;
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

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class MessageUtil {
    private static final FabricServerAudiences adventure = FabricServerAudiences.of(Fuji.SERVER);
    @Getter
    private static final Map<String, String> player2lang = new HashMap<>();
    @Getter
    private static final Map<String, JsonObject> lang2json = new HashMap<>();
    private static final MiniMessage miniMessageParser = MiniMessage.builder().build();

    static {
        writeDefaultLanguageFiles();
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
        }
    }

    private String getClientSideLanguage(Object audience) {
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

    private JsonObject getLanguage(String lang) {
        // if target language is missing, we fall back to default_language
        if (!lang2json.containsKey(lang)) {
            lang = Configs.configHandler.model().common.language.default_language;
        }

        loadLanguageIfAbsent(lang);
        return lang2json.get(lang);
    }

    public static String getString(@Nullable Object audience, String key, Object... args) {
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

    private static String formatString(String string, Object... args) {
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
    public static Component ofComponent(@Nullable Audience audience, boolean isKey, String keyOrString, MiniMessage serializer, Object... args) {
        String string = isKey ? getString(audience, keyOrString, args) : keyOrString;

        PlaceholderContext placeholderContext;
        if (audience instanceof PlayerEntity playerEntity) {
            placeholderContext = PlaceholderContext.of(playerEntity);
        } else {
            placeholderContext = PlaceholderContext.of(Fuji.SERVER);
        }

        // placeholder parser
        Component component = Placeholders.parseText(TextNode.of(string), placeholderContext).asComponent();
        string = PlainTextComponentSerializer.plainText().serialize(component);

        // minimessage parser
        if (serializer == null) {
            serializer = miniMessageParser;
        }
        return serializer.deserialize(string);
    }

    public static Component ofComponent(Audience audience, boolean isKey, String keyOrString, Object... args) {
        return ofComponent(audience, isKey, keyOrString, miniMessageParser, args);
    }

    public static Component ofComponent(Audience audience, String key, Object... args) {
        return ofComponent(audience, true, key, null, args);
    }

    public static Text ofVomponent(Audience audience, String key, Object... args) {
        return toVomponent(ofComponent(audience, key, args));
    }

    public static Text ofVomponent(String str, Object... args) {
        return toVomponent(ofComponent(null, false, str, args));
    }

    public static Text toVomponent(Component component) {
        return adventure.toNative(component);
    }

    public static List<Text> ofVomponents(Audience audience, String key, Object... args) {
        String lines = getString(audience, key, args);

        List<Text> ret = new ArrayList<>();
        for (String line : lines.split("\n")) {
            ret.add(ofVomponent(line));
        }
        return ret;
    }

    public static void sendMessage(Audience audience, String key, Object... args) {
        audience.sendMessage(ofComponent(audience, key, args));
    }

    public static void sendActionBar(Audience audience, String key, Object... args) {
        audience.sendActionBar(ofComponent(audience, key, args));
    }

    public static void sendBroadcast(String key, Object... args) {
        // fix: log broadcast for console
        Fuji.LOGGER.info(PlainTextComponentSerializer.plainText().serialize(ofComponent(null, key, args)));

        for (ServerPlayerEntity player : Fuji.SERVER.getPlayerManager().getPlayerList()) {
            sendMessage(player, key, args);
        }
    }

}
