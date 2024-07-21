package generator;

import com.google.gson.*;
import io.github.sakurawald.module.ModuleManager;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.spongepowered.asm.mixin.Mixin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MixinRegistryGenTest {

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
    void generate() {

        /* read file */
        File file = new File("src/main/resources/fuji.mixins.json");
        @Cleanup Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        JsonElement jsonElement = JsonParser.parseReader(reader);

        List<String> registeredMixins = new ArrayList<>();
        registeredMixins.addAll(collectMixins(jsonElement, "mixins"));
        registeredMixins.addAll(collectMixins(jsonElement, "client"));
        registeredMixins.addAll(collectMixins(jsonElement, "server"));

       /* reflect */
        String mixinPackage = ModuleManager.class.getPackageName() + ".mixin";
        Reflections reflections = new Reflections(mixinPackage);
        List<String> unregisteredMixins = new ArrayList<>();
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Mixin.class)) {
            String mixinName = clazz.getName().substring(mixinPackage.length() + 1);

            if (!registeredMixins.contains(mixinName)) {
                unregisteredMixins.add(mixinName);
            }
        }

        if (!unregisteredMixins.isEmpty()) {
            throw new RuntimeException("Mixins not registered: " + unregisteredMixins);
        }

    }
}

