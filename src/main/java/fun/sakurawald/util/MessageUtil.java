package fun.sakurawald.util;

import assets.sakurawald.ResourceLoader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fun.sakurawald.ServerMain;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@UtilityClass
@Slf4j
public class MessageUtil {

    private static final HashMap<String, JsonObject> lang2json = new HashMap<>();
    @Getter
    private static final HashMap<String, String> player2lang = new HashMap<>();

    private static final String DEFAULT_LANG = "en_us";
    private static final MiniMessage miniMessage = MiniMessage.builder().build();

    static {
        addLanguage("en_us");
        addLanguage("zh_cn");
    }

    private static void addLanguage(String lang) {
        InputStream input = ResourceLoader.class.getResourceAsStream("lang/%s.json".formatted(lang));
        if (input == null) {
            log.warn("Language File Not Found: %s.json".formatted(lang));
            return;
        }
        JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(input)).getAsJsonObject();
        lang2json.put(lang, jsonObject);
    }


    public static String ofString(Audience audience, String key, Object... args) {
        JsonObject lang;
        if (audience instanceof ServerPlayer player) {
            lang = lang2json.getOrDefault(player2lang.getOrDefault(player.getGameProfile().getName(), DEFAULT_LANG), lang2json.get(DEFAULT_LANG));
        } else if (audience instanceof CommandSourceStack source && source.getPlayer() != null) {
            lang = lang2json.getOrDefault(player2lang.getOrDefault(source.getPlayer().getGameProfile().getName(), DEFAULT_LANG), lang2json.get(DEFAULT_LANG));
        } else {
            lang = lang2json.get(DEFAULT_LANG);
        }

        String value = lang.get(key).getAsString();
        if (args.length > 0) {
            value = String.format(value, args);
        }
        return value;
    }

    public static Component ofComponent(Audience audience, String key, Object... args) {
        return miniMessage.deserialize(MessageUtil.ofString(audience, key, args));
    }

    public static void sendMessage(Audience audience, String key, Object... args) {
        audience.sendMessage(ofComponent(audience, key, args));
    }

    public static void sendActionBar(Audience audience, String key, Object... args) {
        audience.sendActionBar(ofComponent(audience, key, args));
    }

    public static void sendBroadcast(String key, Object... args) {
        for (ServerPlayer player : ServerMain.SERVER.getPlayerList().getPlayers()) {
            sendMessage(player, key, args);
        }
    }

    public static List<net.minecraft.network.chat.Component> buildComponents(String str) {
        List<net.minecraft.network.chat.Component> ret = new ArrayList<>();
        for (String s : str.split("\n")) {
            ret.add(net.minecraft.network.chat.Component.literal(s));
        }
        return ret;
    }
}
