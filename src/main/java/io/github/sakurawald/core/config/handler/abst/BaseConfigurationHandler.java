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
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.job.SaveConfigurationHandlerJob;
import io.github.sakurawald.core.config.transformer.abst.ConfigurationTransformer;
import io.github.sakurawald.core.manager.Managers;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * I write some rules here to avoid forgetting.
 * 1. Only use static inner class in config model java object, this is because a historical design problem in java.
 * 2. The new gson type adapter should be registered before the call to loadFromDisk()
 * 3. The type system of java is static, given an object instance, you can use instance.getClass() to get the type of the instance, which means that you don't need to specify the typeOfT for gson library.
 * <p>
 * The configuration handler in module initializer should be static:
 * 1. The major point to make configuration handler a member of class, is that it's easier to control the lifecycle of objects, however, considering the fact that the configuration handler is a mapping between file system and memory, it should be static and unique.
 * 2. Create the instance of configuration handler should have no side effect, until the call to readStorage() and writeStorage()
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

    protected T model;


    /* flags */
    private boolean alreadyBackupFlag = false;
    private boolean writeStorageWithDataTreeFlag = false;
    private boolean exitJvmFlag = false;
    protected boolean detectUnknownKeysFlag = false;

    /* transformer */
    private final List<ConfigurationTransformer> transformers = new ArrayList<>();


    public BaseConfigurationHandler<T> addTransformer(ConfigurationTransformer transformer) {
        this.transformers.add(transformer);
        return this;
    }

    /* json path */
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
                return new GsonJsonProvider(gson);
            }

            @Override
            public MappingProvider mappingProvider() {
                return new GsonMappingProvider(gson);
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

    protected abstract T getDefaultModel();

    private void renameKeysToLowerUnderscoreStyle(JsonObject dataTree) {
        for (String key : dataTree.keySet().stream().toList()) {

            if (MAP_TYPE_MATCHER.matcher(key).matches()) {
                continue;
            }

            /* go down */
            if (dataTree.get(key).isJsonObject()) {
                renameKeysToLowerUnderscoreStyle(dataTree.get(key).getAsJsonObject());
            }

            if (dataTree.get(key).isJsonArray()) {
                dataTree.getAsJsonArray(key).forEach(e -> {
                    if (e.isJsonObject()) renameKeysToLowerUnderscoreStyle(e.getAsJsonObject());
                });
            }

            /* go up */
            String underscoreName = ReflectionUtil.translateToLowerCaseWithUnderscore(key);
            if (!key.equals(underscoreName)) {
                JsonElement value = dataTree.get(key);
                LogUtil.debug("read lower-camel key `{}` as lower-underscore key `{}`", key, underscoreName);
                dataTree.remove(key);
                dataTree.add(underscoreName, value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void readStorage() {
        try {
            /* apply transformers first*/
            this.transformers.forEach(it -> {
                it.configure(this.path);
                it.apply();
            });

            if (Files.notExists(this.path)) {
                writeStorage();
            } else {
                // read data tree from disk
                @Cleanup Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.path.toFile())));
                JsonElement dataTree = JsonParser.parseReader(reader);

                // treat all keys as lower-underscore format
                renameKeysToLowerUnderscoreStyle(dataTree.getAsJsonObject());

                // merge data tree with schema tree
                T defaultModel = getDefaultModel();
                JsonElement schemaTree = gson.toJsonTree(defaultModel);
                mergeJsonTree("", dataTree.getAsJsonObject(), schemaTree.getAsJsonObject());

                // handle flags
                if (this.writeStorageWithDataTreeFlag) {
                    this.writeStorageWithDataTreeFlag = false;
                    Files.writeString(this.path, gson.toJson(dataTree));
                }

                if (this.exitJvmFlag) {
                    LogUtil.info("sorry, i have to shutdown the server because there is an error existing in the configuration file `{}`", this.path);
                    System.exit(-1);
                }

                if (this.detectUnknownKeysFlag) {
                    this.detectUnknownKeys("", dataTree.getAsJsonObject(), schemaTree.getAsJsonObject());
                }

                // use merged tree
                this.model = (T) gson.fromJson(dataTree, defaultModel.getClass());

                /* write storage back, to:
                 * 1. keep the sync between memory and disk.
                 * 2. trigger the field naming conversion in gson.
                 * */

                this.writeStorage();
            }

        } catch (IOException e) {
            LogUtil.error("failed to read configuration file {} from disk.", this.path, e);
        }
    }

    public void writeStorage() {
        try {
            // set default data tree
            if (this.model == null) {
                // getDefaultModel() is allowed to throw exception.
                this.model = this.getDefaultModel();
                LogUtil.info("write default configuration: {}", this.path.toFile().getAbsolutePath());
            }

            // write data tree to disk
            Files.createDirectories(this.path.getParent());
            Files.writeString(this.path, gson.toJson(this.model));
        } catch (IOException e) {
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
    public void scheduleSaveConfigurationHandlerJob(@NotNull String cron) {
        String jobName = this.path.getFileName().toString();
        new SaveConfigurationHandlerJob(jobName, new JobDataMap() {
            {
                this.put(BaseConfigurationHandler.class.getName(), BaseConfigurationHandler.this);
            }
        }, () -> cron).schedule();

        // write storage on server stopping.
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
            LogUtil.debug("write storage on server stopping: {}", this.path);
            this.writeStorage();
        });
    }

    protected void mergeJsonTree(String parentPath, @NotNull JsonObject dataTree, @NotNull JsonObject schemaTree) {
        /* navigating using schema tree */
        Set<Map.Entry<String, JsonElement>> entrySet = schemaTree.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            /* is the key missing form the data tree ? */
            if (dataTree.has(key)) {
                String currentPath = StringUtils.strip(parentPath + "." + key, ".");

                // is the type of value equals ?
                if (JsonUtil.sameType(dataTree.get(key), value)) {

                    // are they not atoms ?
                    if (dataTree.get(key).isJsonObject() && value.isJsonObject()) {

                        /*
                        Gson will store the Map type as JsonObject, we can't tell if the type is a Map or a Java Object by the value.
                        We have to tell them by the name of the key: if its name matches the MAP_TYPE_MATCHER, then we indicate it is a Map, and skip it.
                         */
                        if (MAP_TYPE_MATCHER.matcher(key).matches()) {
                            continue;
                        }

                        // flatten the tree
                        mergeJsonTree(currentPath, dataTree.getAsJsonObject(key), value.getAsJsonObject());
                    }

                } else {
                    handleTreeMismatch(dataTree, currentPath, key, value);
                }

            } else {
                /* for JsonArray type, we will not walk into it, which means that the size of JsonArray will not be changed.
                However, each time the gson serialize the java object with its type specifier, gson will add the missing keys in the json using the field initialization-form defined in java.

                Consider the fact that, we only store user-generated-data using array and map.
                 */
                LogUtil.warn("add missing json key-value pair: file = {}, key = {}, value = {}", this.path.toFile().getName(), key, value);
                dataTree.add(key, value);
                this.writeStorageWithDataTreeFlag = true;
            }
        }
    }

    private void detectUnknownKeys(String parentPath, @NotNull JsonObject dataTree, @NotNull JsonObject schemaTree) {
        // navigating using data tree
        Set<Map.Entry<String, JsonElement>> entrySet = dataTree.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            String currentPath = StringUtils.strip(parentPath + "." + key, ".");

            if (MAP_TYPE_MATCHER.matcher(key).matches()) {
                continue;
            }

            if (!schemaTree.has(key)) {
                LogUtil.warn("unknown configuration key `{}` in configuration file `{}`", currentPath, this.path);
                continue;
            }

            // the verification of type equality is done by mergeJsonTree()
            if (value.isJsonObject()) {
                detectUnknownKeys(currentPath, value.getAsJsonObject(), schemaTree.get(key).getAsJsonObject());
            }
        }

    }


    @SneakyThrows
    private void handleTreeMismatch(@NotNull JsonObject dataTree, String currentPath, String key, JsonElement value) {
        while (true) {
            LogUtil.error("""

                # What happened ?
                There is an incompatibility issue in the configuration file `{}`.
                  - Actual type of key `{}` does not match the expected type.

                # Possible reasons:
                  1. In the new version of fuji, the value of the key has changed its type.
                  2. The configuration file was been modified incorrectly.

                # What can I do ?
                  - Press "y" and enter: backup the `config/fuji/` directory and `override the key with default value`.
                  - Press "n" and enter: ignore this issue.
                  - Press "q" and enter: abort the server start-up process.

                """, this.path.toFile().getAbsoluteFile(), currentPath, this.path.toFile().getAbsoluteFile(), currentPath);

            /* ynop query */
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next().trim();
            if (input.equalsIgnoreCase("y")) {
                if (!this.alreadyBackupFlag) {
                    Managers.getRescueBackupManager().backup();
                    LogUtil.info("backup the `config/fuji` directory into `config/fuji/backup_rescue` directory successfully.");
                    this.alreadyBackupFlag = true;
                }

                // override it with default value
                LogUtil.warn("override the key `{}` with default value: {}", key, value);
                dataTree.remove(key);
                dataTree.add(key, value);

                // data tree is modified in the memory, also we should sync the modification into the storage.
                writeStorageWithDataTreeFlag = true;
                break;
            } else if (input.equalsIgnoreCase("n")) {
                this.exitJvmFlag = true;
                break;
            } else if (input.equalsIgnoreCase("q")) {
                System.exit(-1);
                break;
            }

            LogUtil.error("Invalid input.");
        }
    }

    public T getModel() {
        // load storage if necessary.
        if (this.model == null) {
            this.readStorage();
        }

        return this.model;
    }

}
