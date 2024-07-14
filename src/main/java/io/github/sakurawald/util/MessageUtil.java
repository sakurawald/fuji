package io.github.sakurawald.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import org.apache.commons.io.FileUtils;

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

    private String getClientSideLanguage(String player) {
        return player2lang.get(player);
    }

    private JsonObject getLanguage(String lang) {
        // if target language is missing, we fall back to default_language
        if (!lang2json.containsKey(lang)) {
            lang = Configs.configHandler.model().common.language.default_language;
        }

        loadLanguageIfAbsent(lang);
        return lang2json.get(lang);
    }

    public static String getString(Object audience, String key, Object... args) {
        /* get player */
        PlayerEntity player = switch (audience) {
            case ServerPlayerEntity serverPlayerEntity -> serverPlayerEntity;
            case PlayerEntity playerEntity -> playerEntity;
            case ServerCommandSource source when source.getPlayer() != null -> source.getPlayer();
            case null, default -> throw new RuntimeException("audience can't be null");
        };

        /* get lang */
        String lang = getClientSideLanguage(player.getGameProfile().getName());

        /* get json */
        JsonObject json = getLanguage(lang);

        /* get value */
        if (json.has(key)) {
            String value = json.get(key).getAsString();
            return formatString(value, args);
        }

        Fuji.LOGGER.error("Language '{}' miss the key '{}'", lang, key);
        return null;
    }

    private static String formatString(String string, Object... args) {
        if (args.length > 0) {
            return String.format(string, args);
        }
        return string;
    }

    public static void sendMessageToPlayerEntity(PlayerEntity player, String key, Object... args) {
        player.sendMessage(adventure.toNative(ofComponent(getString(player, key), args)));
    }

    public static Component ofComponent(Audience audience, String key, Object... args) {
        //note: if call ofString() directly with args, then we pass args to ofString(),
        // or else we pass args to ofComponent() to avoid args being formatted twice
        return ofComponent(getString(audience, key), args);
    }

    public static Component ofComponent(String str, Object... args) {
        return miniMessageParser.deserialize(formatString(str, args));
    }

    public static net.minecraft.text.Text ofVomponent(String str, Object... args) {
        return toVomponent(ofComponent(str, args));
    }

    public static net.minecraft.text.Text ofVomponent(Audience audience, String key, Object... args) {
        return toVomponent(ofComponent(audience, key, args));
    }

    public static net.minecraft.text.Text toVomponent(Component component) {
        return adventure.toNative(component);
    }

    public static List<net.minecraft.text.Text> ofVomponents(Audience audience, String key, Object... args) {
        String lines = getString(audience, key, args);

        List<net.minecraft.text.Text> ret = new ArrayList<>();
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
