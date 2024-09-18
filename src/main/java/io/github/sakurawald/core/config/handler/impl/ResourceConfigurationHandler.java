package io.github.sakurawald.core.config.handler.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;


public class ResourceConfigurationHandler extends BaseConfigurationHandler<JsonElement> {

    final String resourcePath;

    private ResourceConfigurationHandler(Path path, String resourcePath) {
        super(path);
        this.resourcePath = resourcePath;
    }

    public ResourceConfigurationHandler(@NotNull String resourcePath) {
        this(Fuji.CONFIG_PATH.resolve(resourcePath), resourcePath);
    }

    private static @Nullable JsonElement readJsonTreeFromResource(@NotNull String resourcePath) {
        InputStream inputStream = Fuji.class.getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("Resource not found: " + resourcePath);
        }
        Reader reader = new BufferedReader(new InputStreamReader(inputStream));
        return JsonParser.parseReader(reader);
    }

    /**
     * for resource configuration handler, the type of model is JsonElement, which equals to the type of data tree.
     */
    @Override
    protected JsonElement getDefaultModel() {
        return readJsonTreeFromResource(this.resourcePath);
    }

}
