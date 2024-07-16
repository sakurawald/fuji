package generator;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import io.github.sakurawald.config.handler.ConfigHandler;
import io.github.sakurawald.module.ModuleManager;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.spongepowered.asm.mixin.Mixin;

import java.io.*;
import java.util.Iterator;

public class MixinRegistryGenTest {

    @SneakyThrows
    @Test
    void generate() {

        /* read file */
        File file = new File("src/main/resources/fuji.mixins.json");
        @Cleanup Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        JsonElement jsonElement = JsonParser.parseReader(reader);
        JsonArray mixinsArray = jsonElement.getAsJsonObject().get("mixins").getAsJsonArray();
        Iterator<JsonElement> iterator = mixinsArray.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }

       /* reflect */
        String mixinPackage = ModuleManager.class.getPackageName() + ".mixin";
        Reflections reflections = new Reflections(mixinPackage);
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(Mixin.class)) {
            String mixinName = clazz.getName().substring(mixinPackage.length() + 1);
            mixinsArray.add(mixinName);
        }

        Gson gson = ConfigHandler.getGson();
        JsonWriter jsonWriter = gson.newJsonWriter(new BufferedWriter(new FileWriter(file)));
        gson.toJson(jsonElement, jsonWriter);
        jsonWriter.close();
    }

}

