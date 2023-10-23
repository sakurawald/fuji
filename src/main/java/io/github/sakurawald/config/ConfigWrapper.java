package io.github.sakurawald.config;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import io.github.sakurawald.ServerMain;
import io.github.sakurawald.module.works.work_type.Work;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigWrapper<T> {
    static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .registerTypeAdapter(Work.class, new Work.WorkTypeAdapter())
            .create();

    final File file;
    final Class<T> configClass;
    T configInstance;

    boolean merged = false;

    @SuppressWarnings("unused")
    public ConfigWrapper(File file, Class<T> configClass) {
        this.file = file;
        this.configClass = configClass;
    }

    public ConfigWrapper(String child, Class<T> configClass) {
        this.file = new File(ServerMain.CONFIG_PATH.toString(), child);
        this.configClass = configClass;
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
                    T newerJsonInstance = configClass.getDeclaredConstructor().newInstance();
                    JsonElement newerJsonElement = gson.toJsonTree(newerJsonInstance, configClass);
                    mergeJson(olderJsonElement, newerJsonElement);
                }

                // read merged json
                configInstance = gson.fromJson(olderJsonElement, configClass);

                this.saveToDisk();
            }

        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            log.error("Load config failed: " + e.getMessage());
        }
    }

    public JsonElement toJsonElement() {
        return gson.toJsonTree(this.configInstance, this.configClass);
    }

    @SuppressWarnings("unused")
    public void backupFromDisk() {
        if (!file.exists()) return;
        String originalFileName = file.getName();
        String backupFileName = originalFileName + ".bak";
        String backupFilePath = file.getParent() + File.separator + backupFileName;
        File backupFile = new File(backupFilePath);
        try {
            Files.copy(file.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Backup file failed: " + e.getMessage());
        }
    }

    private void mergeJson(JsonElement oldJson, JsonElement newJson) {
        if (!oldJson.isJsonObject() || !newJson.isJsonObject()) {
            throw new IllegalArgumentException("Both elements must be JSON objects.");
        }
        mergeFields(oldJson.getAsJsonObject(), newJson.getAsJsonObject());
    }

    private void mergeFields(JsonObject oldJson, JsonObject newJson) {
        Set<Map.Entry<String, JsonElement>> entrySet = newJson.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (oldJson.has(key) && oldJson.get(key).isJsonObject() && value.isJsonObject()) {
                mergeFields(oldJson.getAsJsonObject(key), value.getAsJsonObject());
            } else {
                // note: for JsonArray, we will not directly set array elements, but we will add new properties for every array element (language default empty-value). e.g. For List<ExamplePojo>, we will never change the size of this list, but we will add missing properties for every ExamplePojo with the language default empty-value.
                if (!oldJson.has(key)) {
                    oldJson.add(key, value);
                    log.warn("Add missing json property: file = {}, key = {}, value = {}", this.file.getName(), key, value);
                }
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void saveToDisk() {
        try {
            // Should we generate a default config instance ?
            if (!file.exists()) {
                this.file.getParentFile().mkdirs();
                this.configInstance = configClass.getDeclaredConstructor().newInstance();
            }

            // Save.
            JsonWriter jsonWriter = gson.newJsonWriter(new BufferedWriter(new FileWriter(this.file)));
            gson.toJson(this.configInstance, configClass, jsonWriter);
            jsonWriter.close();
        } catch (IOException | InstantiationException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public T instance() {
        return configInstance;
    }
}
