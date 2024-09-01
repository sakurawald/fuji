package io.github.sakurawald.config.handler.interfaces;

import assets.fuji.Cat;
import com.google.gson.*;
import io.github.sakurawald.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.config.job.ConfigHandlerAutoSaveJob;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.works.structure.work.interfaces.Work;
import io.github.sakurawald.auxiliary.JsonUtil;
import io.github.sakurawald.auxiliary.LogUtil;
import lombok.Cleanup;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quartz.JobDataMap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class ConfigHandler<T> {
    private static final Pattern MAP_TYPE_MATCHER = Pattern.compile(".+2.+");

    @Getter
    protected static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .registerTypeAdapter(Work.class, new Work.WorkTypeAdapter())
            .create();

    protected File file;
    protected @Nullable T model;
    protected boolean alreadyBackup;

    public ConfigHandler(File file) {
        this.file = file;
    }

    public static @Nullable JsonElement getJsonElement(@NotNull String resourcePath) {
        try {
            InputStream inputStream = Cat.class.getResourceAsStream(resourcePath);
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
            LogUtil.error("Backup file failed: {}", e.getMessage());
        }
    }

    public void setAutoSaveJob(@NotNull String cron) {
        String jobName = this.file.getName();
        new ConfigHandlerAutoSaveJob(jobName, new JobDataMap() {
            {
                this.put(ConfigHandler.class.getName(), ConfigHandler.this);
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
                    LogUtil.warn("Add missing json property: file = {}, key = {}, value = {}", this.file.getName(), key, value);
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
                LogUtil.warn("Backup the `config/fuji` folder into `config/fuji/backup_rescue` folder successfully.");
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
