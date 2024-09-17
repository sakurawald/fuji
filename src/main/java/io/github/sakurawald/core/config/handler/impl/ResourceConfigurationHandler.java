package io.github.sakurawald.core.config.handler.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.handler.abst.ConfigurationHandler;
import lombok.Cleanup;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;


public class ResourceConfigurationHandler extends ConfigurationHandler<JsonElement> {

    final String resourcePath;

    private ResourceConfigurationHandler(File file, String resourcePath) {
        super(file);
        this.resourcePath = resourcePath;
    }

    public ResourceConfigurationHandler(@NotNull String resourcePath) {
        this(Fuji.CONFIG_PATH.resolve(resourcePath).toFile(), resourcePath);
    }

    public void loadFromDisk() {
        // Does the file exist?
        try {
            if (!file.exists()) {
                saveToDisk();
            } else {
                // read older json from disk
                @Cleanup Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
                JsonElement olderJsonElement = JsonParser.parseReader(reader);

                // merge older json with newer json
                JsonElement newerJsonElement = ResourceConfigurationHandler.getJsonElement(this.resourcePath);
                assert newerJsonElement != null;

                mergeJson(olderJsonElement, newerJsonElement);

                // read merged json
                model = olderJsonElement;
                this.saveToDisk();
            }

        } catch (Exception e) {
            LogUtil.error("load config failed", e);
        }
    }


    public void saveToDisk() {
        try {
            // Should we generate a default config instance ?
            if (!file.exists()) {
                LogUtil.info("write default configuration: {}", this.file.getAbsolutePath());
                Files.createDirectories(this.file.getParentFile().toPath());
                this.model = ResourceConfigurationHandler.getJsonElement(this.resourcePath);
            }

            // Save.
            JsonWriter jsonWriter = gson.newJsonWriter(new BufferedWriter(new FileWriter(this.file)));
            gson.toJson(this.model, jsonWriter);
            jsonWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
