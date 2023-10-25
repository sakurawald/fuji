package io.github.sakurawald.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.sakurawald.ServerMain;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@UtilityClass
@Slf4j
public class MessageUtil {
    private static final FabricServerAudiences adventure = FabricServerAudiences.of(ServerMain.SERVER);
    @Getter
    private static final HashMap<String, String> player2lang = new HashMap<>();
    private static final HashMap<String, JsonObject> lang2json = new HashMap<>();
    private static final String DEFAULT_LANG = "en_us";
    private static final MiniMessage miniMessage = MiniMessage.builder().build();
    private static final Path LANGUAGE_PATH = ServerMain.CONFIG_PATH.resolve("language");

    static {
        loadLanguages();
    }

    public static void loadLanguages() {
        File[] languages = LANGUAGE_PATH.toFile().listFiles();
        if (languages == null) {
            log.error("Failed to load languages");
            return;
        }
        for (File language : languages) {
            log.info("Loading Language File: %s".formatted(language.getName()));
            addLanguage(language.getName());
        }

    }

    private static void addLanguage(String fileName) {
        InputStream input;
        try {
            input = FileUtils.openInputStream(LANGUAGE_PATH.resolve(fileName).toFile());
            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(input)).getAsJsonObject();
            lang2json.put(fileName.replace(".json", ""), jsonObject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String ofString(Audience audience, String key, Object... args) {
        JsonObject lang;
        if (audience instanceof ServerPlayer player) {
            lang = lang2json.getOrDefault(player2lang.getOrDefault(player.getGameProfile().getName(), DEFAULT_LANG), lang2json.get(DEFAULT_LANG));
        } else if (audience instanceof CommandSourceStack source && source.getPlayer() != null) {
            lang = lang2json.getOrDefault(player2lang.getOrDefault(source.getPlayer().getGameProfile().getName(), DEFAULT_LANG), lang2json.get(DEFAULT_LANG));
        } else {
            // null audience will return this
            lang = lang2json.get(DEFAULT_LANG);
        }

        String value = lang.get(key).getAsString();
        if (args.length > 0) {
            value = String.format(value, args);
        }
        return value;
    }

    public static Component ofComponent(Audience audience, String key, Object... args) {
        return ofComponentFromMiniMessage(ofString(audience, key, args));
    }

    public static Component ofComponentFromMiniMessage(String str) {
        return miniMessage.deserialize(str);
    }

    public static net.minecraft.network.chat.Component ofVomponentFromMiniMessage(String str) {
        return toVomponent(ofComponentFromMiniMessage(str));
    }

    public static net.minecraft.network.chat.Component ofVomponent(Audience audience, String key, Object... args) {
        return adventure.toNative(ofComponent(audience, key, args));
    }

    public static net.minecraft.network.chat.Component toVomponent(Component component) {
        return adventure.toNative(component);
    }

    public static List<net.minecraft.network.chat.Component> ofVomponents(Audience audience, String key, Object... args) {
        String lines = ofString(audience, key, args);
        List<net.minecraft.network.chat.Component> ret = new ArrayList<>();
        for (String line : lines.split("\n")) {
            ret.add(adventure.toNative(miniMessage.deserialize(line)));
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
        log.info(PlainTextComponentSerializer.plainText().serialize(ofComponent(null, key, args)));

        for (ServerPlayer player : ServerMain.SERVER.getPlayerList().getPlayers()) {
            sendMessage(player, key, args);
        }
    }

}
