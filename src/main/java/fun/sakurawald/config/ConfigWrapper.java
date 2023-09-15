package fun.sakurawald.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import fun.sakurawald.ModMain;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class ConfigWrapper<T> {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();

    private final File file;
    private final Class<T> configClass;
    private T configInstance;

    public ConfigWrapper(File file, Class<T> configClass) {
        this.file = file;
        this.configClass = configClass;
    }

    public ConfigWrapper(String child, Class<T> configClass) {
        this.file = new File(ModMain.CONFIG_PATH.toString(), child);
        this.configClass = configClass;
    }

    public void loadFromDisk() {
        // Does the file exists ?
        try {

            if (!file.exists()) {
                saveToDisk();
            } else {
                Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)));
                configInstance = gson.fromJson(reader, configClass);
                reader.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
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
