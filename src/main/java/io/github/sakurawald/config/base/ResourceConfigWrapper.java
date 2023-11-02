package io.github.sakurawald.config.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import io.github.sakurawald.Fuji;
import lombok.Cleanup;

import java.io.*;


public class ResourceConfigWrapper extends ConfigWrapper<JsonElement> {

    final String resourcePath;

    public ResourceConfigWrapper(File file, String resourcePath) {
        super(file);
        this.file = file;
        this.resourcePath = resourcePath;
    }

    public ResourceConfigWrapper(String resourcePath) {
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
            Fuji.log.error("Load config failed: " + e.getMessage());
        }
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

}
