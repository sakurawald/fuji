package io.github.sakurawald.core.config.transformer.abst;

import com.jayway.jsonpath.DocumentContext;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import lombok.Getter;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ConfigurationTransformer {

    @Getter
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

    public void logConsole(String message, Object... args) {
        Object[] fullArgs = new Object[args.length + 1];
        fullArgs[0] = this.path;
        System.arraycopy(args, 0, fullArgs, 1, args.length);

        LogUtil.warn("transform configuration file `{}`: " + message, fullArgs);
    }

    public void renameKey(String jsonPath, String oldKeyName, String newKeyName) {
        this.logConsole("rename key from `{}` to `{}`", oldKeyName, newKeyName);
        context.renameKey(jsonPath, oldKeyName, newKeyName);
    }

    @SneakyThrows
    public void writeStorage() {
        this.logConsole("write storage");
        String json = BaseConfigurationHandler.getGson().toJson(context.json());
        Files.writeString(this.path, json);
    }

    public void deleteKey(String jsonPath) {
        this.logConsole("delete key from `{}`", jsonPath);
        context.delete(jsonPath);
    }

    public Object read(String jsonPath) {
        return context.read(jsonPath);
    }

    public void set(String jsonPath, Object newValue) {
        this.logConsole("set key `{}`: {}", jsonPath, newValue);
        context.set(jsonPath, newValue);
    }

}
