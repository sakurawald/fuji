package io.github.sakurawald.config.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import io.github.sakurawald.ServerMain;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class ResourceConfigWrapper extends ConfigWrapper<JsonElement> {

    final String resourcePath;
    JsonElement configInstance;

    public ResourceConfigWrapper(File file, String resourcePath) {
        super(file);
        this.file = file;
        this.resourcePath = resourcePath;
    }

    public ResourceConfigWrapper(String resourcePath) {
        this(ServerMain.CONFIG_PATH.resolve(resourcePath).toFile(), resourcePath);
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
                if (!this.merged) {
                    this.merged = true;
                    JsonElement newerJsonElement = ResourceConfigWrapper.getJsonElement(this.resourcePath);
                    mergeJson(olderJsonElement, newerJsonElement);
                }

                // read merged json
                configInstance = olderJsonElement;
                this.saveToDisk();
            }

        } catch (IOException e) {
            log.error("Load config failed: " + e.getMessage());
        }
    }


    public JsonElement toJsonElement() {
        return gson.toJsonTree(this.configInstance);
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveToDisk() {
        try {
            // Should we generate a default config instance ?
            if (!file.exists()) {
                this.file.getParentFile().mkdirs();
                this.configInstance = ResourceConfigWrapper.getJsonElement(this.resourcePath);
            }

            // Save.
            JsonWriter jsonWriter = gson.newJsonWriter(new BufferedWriter(new FileWriter(this.file)));
            gson.toJson(this.configInstance, jsonWriter);
            jsonWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonElement instance() {
        return configInstance;
    }
}
