package io.github.sakurawald.core.config.handler.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import org.jetbrains.annotations.NotNull;

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

    private static @NotNull JsonElement readJsonTreeFromResource(@NotNull String resourcePath) {
        InputStream inputStream = Fuji.class.getResourceAsStream(resourcePath);
        assert inputStream != null;
        Reader reader = new BufferedReader(new InputStreamReader(inputStream));
        return JsonParser.parseReader(reader);
    }

    /**
     * for resource configuration handler, the type of model is JsonElement, which equals to the type of data tree.
     */
    @Override
    public JsonElement getDefaultModel() {
        return readJsonTreeFromResource(this.resourcePath);
    }

}
