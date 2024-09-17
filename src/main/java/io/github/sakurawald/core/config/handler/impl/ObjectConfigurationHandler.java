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


public class ObjectConfigurationHandler<T> extends ConfigurationHandler<T> {

    final Class<T> typeOfModel;

    private ObjectConfigurationHandler(File file, Class<T> typeOfModel) {
        super(file);
        this.file = file;
        this.typeOfModel = typeOfModel;
    }

    public ObjectConfigurationHandler(@NotNull String other, Class<T> typeOfModel) {
        this(Fuji.CONFIG_PATH.resolve(other).toFile(), typeOfModel);
    }

    public void loadFromDisk() {
        // Does the file exist?
        try {
            if (!file.exists()) {
                saveToDisk();
            } else {
                // read older json from disk
                @Cleanup Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
                JsonElement currentJsonElement = JsonParser.parseReader(reader);

                // merge older json with newer json
                T defaultJsonInstance = typeOfModel.getDeclaredConstructor().newInstance();
                JsonElement defaultJsonElement = gson.toJsonTree(defaultJsonInstance, typeOfModel);
                mergeJson(currentJsonElement, defaultJsonElement);

                // read merged json
                model = gson.fromJson(currentJsonElement, typeOfModel);

                this.saveToDisk();
            }

        } catch (Exception e) {
            LogUtil.error("load config failed: ", e);
        }
    }


    public void saveToDisk() {
        try {
            // Should we generate a default config instance ?
            if (!file.exists()) {
                LogUtil.info("write default configuration: {}", this.file.getAbsolutePath());
                //noinspection ResultOfMethodCallIgnored
                this.file.getParentFile().mkdirs();
                this.model = typeOfModel.getDeclaredConstructor().newInstance();
            }

            // Save.
            JsonWriter jsonWriter = gson.newJsonWriter(new BufferedWriter(new FileWriter(this.file)));
            gson.toJson(this.model, typeOfModel, jsonWriter);
            jsonWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
