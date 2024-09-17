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
import java.nio.file.Path;


public class ObjectConfigurationHandler<T> extends ConfigurationHandler<T> {

    final Class<T> typeOfModel;

    private ObjectConfigurationHandler(Path path, Class<T> typeOfModel) {
        super(path);
        this.typeOfModel = typeOfModel;
    }

    public ObjectConfigurationHandler(@NotNull String other, Class<T> typeOfModel) {
        this(Fuji.CONFIG_PATH.resolve(other), typeOfModel);
    }

    public void readFromDisk() {
        try {
            if (!path.toFile().exists()) {
                writeToDisk();
            } else {
                // read older json from disk
                @Cleanup Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.path.toFile())));
                JsonElement currentJsonElement = JsonParser.parseReader(reader);

                // merge older json with newer json
                T defaultJsonInstance = typeOfModel.getDeclaredConstructor().newInstance();
                JsonElement defaultJsonElement = gson.toJsonTree(defaultJsonInstance, typeOfModel);
                mergeJsonTree(currentJsonElement, defaultJsonElement);

                // read merged json
                model = gson.fromJson(currentJsonElement, typeOfModel);

                this.writeToDisk();
            }

        } catch (Exception e) {
            LogUtil.error("load config failed: ", e);
        }
    }


    public void writeToDisk() {
        try {
            // Should we generate a default config instance ?
            if (!this.path.toFile().exists()) {
                LogUtil.info("write default configuration: {}", this.path.toFile().getAbsolutePath());
                //noinspection ResultOfMethodCallIgnored
                this.path.toFile().getParentFile().mkdirs();
                this.model = typeOfModel.getDeclaredConstructor().newInstance();
            }

            // Save.
            JsonWriter jsonWriter = gson.newJsonWriter(new BufferedWriter(new FileWriter(this.path.toFile())));
            gson.toJson(this.model, typeOfModel, jsonWriter);
            jsonWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
