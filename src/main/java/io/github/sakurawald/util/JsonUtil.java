package io.github.sakurawald.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class JsonUtil {

    @SuppressWarnings("RedundantIfStatement")
    public static boolean sameType(@NotNull JsonElement a, @NotNull JsonElement b) {
        if (a.isJsonObject() && b.isJsonObject()) return true;
        if (a.isJsonArray() && b.isJsonArray()) return true;
        if (a.isJsonNull() && b.isJsonNull()) return true;
        if (a.isJsonPrimitive() && b.isJsonPrimitive()) {
            JsonPrimitive aa = a.getAsJsonPrimitive();
            JsonPrimitive bb = b.getAsJsonPrimitive();

            if (aa.isString() && bb.isString()) return true;
            if (aa.isBoolean() && bb.isBoolean()) return true;
            if (aa.isNumber() && bb.isNumber()) return true;
        }

        return false;
    }

    public static boolean existsNode(@NotNull JsonObject root, @NotNull String path){
        String[] nodes = path.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            String node = nodes[i];
            if (!root.has(node)) return false;
            if (!root.isJsonObject()) return false;

            root = root.getAsJsonObject(node);
        }

        return root.has(nodes[nodes.length - 1]);
    }
}
