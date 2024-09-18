package io.github.sakurawald.core.config.transformer.impl;

import com.google.gson.JsonObject;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.transformer.abst.ConfigurationTransformer;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FlattenTransformer extends ConfigurationTransformer {

    private static final String ENABLE = "enable";
    final String jsonPath;
    private boolean applied;
    private final String topLevel;

    public FlattenTransformer(String jsonPath, String topLevel) {
        this.jsonPath = jsonPath;
        this.topLevel = topLevel;
    }

    @SneakyThrows
    public void flatten(JsonObject parent, String level) {
        Path outPath = Fuji.CONFIG_PATH.resolve(level + ".json");
        if (Files.exists(outPath)) return;

        /* collect submodule */
        List<String> submodule = new ArrayList<>();
        for (String key : parent.keySet()) {
            if (parent.get(key).isJsonObject() && parent.getAsJsonObject(key).has(ENABLE)) {
                submodule.add(key);
            }
        }

        /* write storage */
        JsonObject copy = parent.deepCopy();
        copy.remove(ENABLE);
        submodule.forEach(copy::remove);

        // ignore the transformation if the tree only has 1 node named "enable"
        if (!copy.keySet().isEmpty()) {
            this.applied = true;
            logConsole("flatten tree `{}` into `{}`", level, outPath);
            String json = BaseConfigurationHandler.getGson().toJson(copy);
            Files.writeString(outPath, json);
        }

        /* walk */
        submodule.forEach(key -> flatten(parent.getAsJsonObject(key), level + "." + key));
    }

    @Override
    public void apply() {
        JsonObject root = (JsonObject) read(this.jsonPath);
        this.flatten(root, this.topLevel);

        // set new value if applied
        if (this.applied) {
            JsonObject newValue = root.deepCopy();
            root.keySet().forEach(key -> {
                if (!key.equals(ENABLE)) {
                    newValue.remove(key);
                }
            });

            set(this.jsonPath, newValue);
            writeStorage();
        }
    }

}
