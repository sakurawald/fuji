package checker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.classgraph.ClassInfo;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.spongepowered.asm.mixin.Mixin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CheckMixinRegistryTest {

    private List<JsonElement> collectJsonArray(JsonElement jsonElement, String key) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (!jsonObject.has(key)) return List.of();

        JsonArray mixinsArray = jsonObject.get(key).getAsJsonArray();
        return mixinsArray.asList();
    }

    private List<String> collectMixins(JsonElement jsonElement, String key) {
        List<JsonElement> jsonElements = collectJsonArray(jsonElement, key);
        return jsonElements.stream().map(JsonElement::getAsString).toList();
    }

    @SneakyThrows
    @Test
    void test() {

        /* read file */
        File file = new File("src/main/resources/fuji.mixins.json");
        @Cleanup Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        JsonElement jsonElement = JsonParser.parseReader(reader);

        List<String> registeredMixins = new ArrayList<>();
        registeredMixins.addAll(collectMixins(jsonElement, "mixins"));
        registeredMixins.addAll(collectMixins(jsonElement, "client"));
        registeredMixins.addAll(collectMixins(jsonElement, "server"));

        /* reflect */
        String mixinPackage = Fuji.class.getPackageName() + ".module.mixin";
        List<String> unregisteredMixins = new ArrayList<>();

        for (ClassInfo classInfo : ReflectionUtil.getClassAnnotationInfoScanResult().getClassesWithAnnotation(Mixin.class)) {
            String mixinName = classInfo.getName().substring(mixinPackage.length() + 1);

            if (!registeredMixins.contains(mixinName)) {
                unregisteredMixins.add(mixinName);
            }
        }

        if (!unregisteredMixins.isEmpty()) {
            throw new RuntimeException("Mixins not registered: " + unregisteredMixins);
        }

    }
}

