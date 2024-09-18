package io.github.sakurawald.core.config.transformer.abst;

import com.jayway.jsonpath.DocumentContext;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ConfigurationTransformer {

    private Path path;
    private DocumentContext context;

    @SneakyThrows
    public void configure(Path path) {
        this.path = path;
        this.context = BaseConfigurationHandler.getJsonPathParser().parse(path.toFile());
    }

    public abstract void apply();

    public boolean exists(String jsonPath) {
        try {
            this.context.read(jsonPath);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean notExists(String jsonPath) {
        return !exists(jsonPath);
    }

    public void renameKey(String jsonPath, String oldKeyName, String newKeyName) {
        LogUtil.warn("transform configuration file `{}`: rename key from `{}` to `{}`", this.path, oldKeyName, newKeyName);
        context.renameKey(jsonPath, oldKeyName, newKeyName);
    }

    @SneakyThrows
    public void writeStorage() {
        LogUtil.warn("transform configuration file `{}`: write storage", this.path);
        String json = BaseConfigurationHandler.getGson().toJson(context.json());
        Files.writeString(this.path, json);
    }

}
