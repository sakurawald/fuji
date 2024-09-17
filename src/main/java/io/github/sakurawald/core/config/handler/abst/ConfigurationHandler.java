package io.github.sakurawald.core.config.handler.abst;

import com.google.gson.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.JsonUtil;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.job.SaveConfigHandlerJob;
import io.github.sakurawald.core.manager.Managers;
import lombok.Cleanup;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quartz.JobDataMap;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.EnumSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * I write some rules here to avoid forgetting.
 * 1. Only use static inner class in config model java object, this is because a historical design problem in java.
 * 2. Only register gson type adapter inside the static initialization block of a pojo entity.
 *
 */
public abstract class ConfigurationHandler<T> {

    private static final Pattern MAP_TYPE_MATCHER = Pattern.compile(".+2.+");

    @Getter
    protected static Gson gson = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .serializeNulls()
        .create();

    protected File file;
    protected @Nullable T model;
    protected boolean alreadyBackup;

    protected static ParseContext jsonPathParser = null;

    public static void registerTypeAdapter(Type type, Object typeAdapter) {
        gson = gson.newBuilder().registerTypeAdapter(type, typeAdapter).create();
    }

    static {

        // configure json path library
        Configuration.setDefaults(new com.jayway.jsonpath.Configuration.Defaults() {
            private final JsonProvider jsonProvider = new GsonJsonProvider();
            private final MappingProvider mappingProvider = new GsonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    public static ParseContext getJsonPathParser() {
        if (jsonPathParser == null) {
            jsonPathParser = JsonPath.using(Configuration.defaultConfiguration());
        }

        return jsonPathParser;
    }

    public ConfigurationHandler(File file) {
        this.file = file;
    }

    public static @Nullable JsonElement getJsonElement(@NotNull String resourcePath) {
        try {
            InputStream inputStream = Fuji.class.getResourceAsStream(resourcePath);
            assert inputStream != null;
            @Cleanup Reader reader = new BufferedReader(new InputStreamReader(inputStream));
            return JsonParser.parseReader(reader);
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }

        return null;
    }

    public abstract void loadFromDisk();

    public abstract void saveToDisk();


    public T model() {
        return this.model;
    }

    public JsonElement toJsonElement() {
        if (this.model == null) {
            throw new IllegalStateException("The model is null now, maybe it's too early to call this function ?");
        }

        return gson.toJsonTree(this.model);
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
            LogUtil.error("backup file failed: {}", e.getMessage());
        }
    }

    public void setAutoSaveJob(@NotNull String cron) {
        String jobName = this.file.getName();
        new SaveConfigHandlerJob(jobName, new JobDataMap() {
            {
                this.put(ConfigurationHandler.class.getName(), ConfigurationHandler.this);
            }
        }, () -> cron).schedule();
    }

    public void mergeJson(@NotNull JsonElement oldJson, @NotNull JsonElement newJson) {
        if (!oldJson.isJsonObject() || !newJson.isJsonObject()) {
            throw new IllegalArgumentException("Both elements must be JSON objects.");
        }
        mergeFields("", oldJson.getAsJsonObject(), newJson.getAsJsonObject());
    }

    private void mergeFields(String parentPath, @NotNull JsonObject currentJson, @NotNull JsonObject defaultJson) {
        Set<Map.Entry<String, JsonElement>> entrySet = defaultJson.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            // test -> missing keys
            if (currentJson.has(key)) {

                String currentPath = StringUtils.strip(parentPath + "." + key, ".");

                // test -> same type
                if (JsonUtil.sameType(currentJson.get(key), value)) {
                    // test -> both are JsonObject
                    if (currentJson.get(key).isJsonObject() && value.isJsonObject()) {
                        // skip the missing keys if its type is Map
                        if (MAP_TYPE_MATCHER.matcher(key).matches()) {
                            continue;
                        }

                        mergeFields(currentPath, currentJson.getAsJsonObject(key), value.getAsJsonObject());
                    }
                } else {
                    if (rescueMode(currentJson, currentPath, key, value)) break;
                }

            } else {
                // note: for JsonArray, we will not directly set array elements, but we will add new properties for every array element (language default empty-value). e.g. For List<ExamplePojo>, we will never change the size of this list, but we will add missing properties for every ExamplePojo with the language default empty-value.
                if (!currentJson.has(key)) {
                    currentJson.add(key, value);
                    LogUtil.warn("add missing json key-value pair: file = {}, key = {}, value = {}", this.file.getName(), key, value);
                }
            }
        }
    }

    private boolean rescueMode(@NotNull JsonObject currentJson, String currentPath, String key, JsonElement value) {
        LogUtil.warn("""

            # What happened ?
            There is an incompatibility issue in the configuration file `{}`.
              - Actual value of key `{}` does not match the expected type.

            Possible reason:
              1. In the new version of fuji, the key has changed its type.
              2. The configuration file was been accidentally modified.

            How can I solve this ?

              - Manually:
                1. Backup the folder `<your-server-root>/config/fuji`
                2. Use your `text-editor` to open the file `{}`
                3. Find the `key` in path `{}`
                4. Make sure again you have backup your folder in `step 1`
                5. Delete the `key`, and re-start the server. Fuji will re-generate the missing keys.

              - Automatically:
                If you want to `back up the folder` and `delete the key`, press "y" and enter. (y/n)

            """, file.getAbsoluteFile(), currentPath, file.getAbsoluteFile(), currentPath);

        /* ynop query */
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next().trim();
        if (input.equalsIgnoreCase("y")) {
            if (!this.alreadyBackup) {
                Managers.getRescueBackupManager().backup();
                LogUtil.warn("backup the `config/fuji` folder into `config/fuji/backup_rescue` folder successfully.");
                this.alreadyBackup = true;
            }

            currentJson.remove(key);
            currentJson.add(key, value);
            return true;
        } else {
            // exit the JVM with error code
            System.exit(-1);
        }
        return false;
    }

}
