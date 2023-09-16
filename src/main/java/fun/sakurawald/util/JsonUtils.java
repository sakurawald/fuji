package fun.sakurawald.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsonUtils {

    @Getter
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static JsonObject parseJson(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }
}
