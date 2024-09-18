package io.github.sakurawald.core.config.handler.abst;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import io.github.sakurawald.core.auxiliary.JsonUtil;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.config.job.SaveConfigurationHandlerJob;
import lombok.Cleanup;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * I write some rules here to avoid forgetting.
 * 1. Only use static inner class in config model java object, this is because a historical design problem in java.
 * 2. The new gson type adapter should be registered before the call to loadFromDisk()
 * 3. The type system of java is static, given an object instance, you can use instance.getClass() to get the type of the instance, which means that you don't need to specify the typeOfT for gson library.
 */
public abstract class BaseConfigurationHandler<T> {

    private static final Pattern MAP_TYPE_MATCHER = Pattern.compile(".+2.+");

    @Getter
    protected static Gson gson = new GsonBuilder()
        // the default naming policy is IDENTIFY, we ensure that the naming style is consistency, whatever the internal name is.
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        // for human
        .setPrettyPrinting()
        // for mini-message language
        .disableHtmlEscaping()
        // null-value is value, we should serialize it.
        .serializeNulls()
        .create();

    protected @NotNull Path path;
    @Getter
    protected T model;
    protected boolean alreadyBackup;


    private static ParseContext jsonPathParser = null;

    public static ParseContext getJsonPathParser() {
        if (jsonPathParser == null) {
            configureJsonPathLibrary();
            jsonPathParser = JsonPath.using(Configuration.defaultConfiguration());
        }

        return jsonPathParser;
    }

    private static void configureJsonPathLibrary() {
        Configuration.setDefaults(new Configuration.Defaults() {

            @Override
            public JsonProvider jsonProvider() {
                return new GsonJsonProvider();
            }

            @Override
            public MappingProvider mappingProvider() {
                return new GsonMappingProvider();
            }

            @Override
            public Set<Option> options() {
                return EnumSet.noneOf(Option.class);
            }
        });
    }

    public static void registerTypeAdapter(Type type, Object typeAdapter) {
        gson = gson.newBuilder().registerTypeAdapter(type, typeAdapter).create();
    }

    public BaseConfigurationHandler(@NotNull Path path) {
        this.path = path;
    }

    public abstract T getDefaultModel();

    @SuppressWarnings("unchecked")
    public void readStorage() {
        try {
            if (Files.notExists(this.path)) {
                writeStorage();
            } else {
                // read data tree from disk
                @Cleanup Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.path.toFile())));
                JsonElement dataTree = JsonParser.parseReader(reader);

                // merge data tree with schema tree
                T defaultModel = getDefaultModel();
                JsonElement schemaTree = gson.toJsonTree(defaultModel);
                mergeJsonTree("", dataTree.getAsJsonObject(), schemaTree.getAsJsonObject());

                // use merged tree
                this.model = (T) gson.fromJson(dataTree, defaultModel.getClass());
            }

        } catch (Exception e) {
            LogUtil.error("failed to read configuration file {} from disk.", this.path, e);
        }
    }

    public void writeStorage() {
        try {
            // set default data tree
            if (this.model == null) {
                LogUtil.info("write default configuration: {}", this.path.toFile().getAbsolutePath());
                this.model = this.getDefaultModel();
            }

            // write data tree to disk
            Files.createDirectories(this.path.getParent());
            Files.writeString(this.path, gson.toJson(this.model));
        } catch (Exception e) {
            LogUtil.error("failed to write configuration file {} to disk.", this.path, e);
        }
    }

    public JsonElement convertModelToJsonTree() {
        if (this.model == null) {
            throw new IllegalStateException("The model instance is null now, maybe it's too early to call this function ?");
        }

        return gson.toJsonTree(this.model);
    }

    /**
     * This method exists for performance purpose.
     */
    public void setAutoSaveJob(@NotNull String cron) {
        String jobName = this.path.getFileName().toString();
        new SaveConfigurationHandlerJob(jobName, new JobDataMap() {
            {
                this.put(BaseConfigurationHandler.class.getName(), BaseConfigurationHandler.this);
            }
        }, () -> cron).schedule();
    }

    protected void mergeJsonTree(String parentPath, @NotNull JsonObject dataTree, @NotNull JsonObject schemaTree) {
        /* navigating using schema tree */
        Set<Map.Entry<String, JsonElement>> entrySet = schemaTree.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            // test -> missing keys
            if (dataTree.has(key)) {
                String currentPath = StringUtils.strip(parentPath + "." + key, ".");

                // test -> same type
                if (JsonUtil.sameType(dataTree.get(key), value)) {
                    // test -> both are JsonObject
                    if (dataTree.get(key).isJsonObject() && value.isJsonObject()) {
                        // skip the missing keys if its type is Map
                        if (MAP_TYPE_MATCHER.matcher(key).matches()) {
                            continue;
                        }

                        mergeJsonTree(currentPath, dataTree.getAsJsonObject(key), value.getAsJsonObject());
                    }
                } else {
                    // enter rescue loop if the kv-pair is not the same type
                    rescueLoop(dataTree, currentPath, key, value);
                }

            } else {
                /* note: for JsonArray, we will not directly set array elements, but we will add new properties for every array element (language default empty-value).
                 e.g. For List<ExamplePojo>, we will never change the size of this list, but we will add missing properties for every ExamplePojo with the language default empty-value.
                 */
                LogUtil.info("add missing json key-value pair: file = {}, key = {}, value = {}", this.path.toFile().getName(), key, value);
                dataTree.add(key, value);
            }
        }
    }

    private void rescueLoop(@NotNull JsonObject dataTree, String currentPath, String key, JsonElement value) {
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

            """, this.path.toFile().getAbsoluteFile(), currentPath, this.path.toFile().getAbsoluteFile(), currentPath);

        /* ynop query */
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next().trim();
        if (input.equalsIgnoreCase("y")) {
            LogUtil.info("override the key {} with default value!", key);

//            if (!this.alreadyBackup) {
//                Managers.getRescueBackupManager().backup();
//                LogUtil.warn("backup the `config/fuji` folder into `config/fuji/backup_rescue` folder successfully.");
//                this.alreadyBackup = true;
//            }

            dataTree.remove(key);
            dataTree.add(key, value);

        } else {
            // exit the JVM with error code
            System.exit(-1);
        }
    }
}
