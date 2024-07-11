package io.github.sakurawald.config.handler;

import assets.fuji.Cat;
import com.google.gson.*;
import io.github.sakurawald.module.initializer.works.work_type.Work;
import io.github.sakurawald.util.ScheduleUtil;
import lombok.Cleanup;
import lombok.Getter;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;

import static io.github.sakurawald.Fuji.LOGGER;


public abstract class ConfigHandler<T> {

    @Getter
    protected static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .registerTypeAdapter(Work.class, new Work.WorkTypeAdapter())
            .create();

    protected File file;
    protected T model;

    protected boolean merged = false;

    public ConfigHandler(File file) {
        this.file = file;
    }

    public static JsonElement getJsonElement(String resourcePath) {
        try {
            InputStream inputStream = Cat.class.getResourceAsStream(resourcePath);
            assert inputStream != null;
            @Cleanup Reader reader = new BufferedReader(new InputStreamReader(inputStream));
            return JsonParser.parseReader(reader);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

        return null;
    }

    public abstract void loadFromDisk();

    public abstract void saveToDisk();


    public T model() {
        return this.model;
    }

    public JsonElement toJsonElement() {
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
            LOGGER.error("Backup file failed: {}", e.getMessage());
        }
    }

    public void autoSave(String cron) {
        String jobName = this.file.getName();
        String jobGroup = ConfigHandlerAutoSaveJob.class.getName();
        ScheduleUtil.removeJobs(jobGroup, jobName);
        ScheduleUtil.addJob(ConfigHandlerAutoSaveJob.class, jobName, jobGroup, cron, new JobDataMap() {
            {
                this.put(ConfigHandler.class.getName(), ConfigHandler.this);
            }
        });
    }

    public void mergeJson(JsonElement oldJson, JsonElement newJson) {
        if (!oldJson.isJsonObject() || !newJson.isJsonObject()) {
            throw new IllegalArgumentException("Both elements must be JSON objects.");
        }
        mergeFields(oldJson.getAsJsonObject(), newJson.getAsJsonObject());
    }

    private void mergeFields(JsonObject currentJson, JsonObject defaultJson) {
        Set<Map.Entry<String, JsonElement>> entrySet = defaultJson.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (currentJson.has(key) && currentJson.get(key).isJsonObject() && value.isJsonObject()) {
                mergeFields(currentJson.getAsJsonObject(key), value.getAsJsonObject());
            } else {
                // note: for JsonArray, we will not directly set array elements, but we will add new properties for every array element (language default empty-value). e.g. For List<ExamplePojo>, we will never change the size of this list, but we will add missing properties for every ExamplePojo with the language default empty-value.
                if (!currentJson.has(key)) {
                    currentJson.add(key, value);
                    LOGGER.warn("Add missing json property: file = {}, key = {}, value = {}", this.file.getName(), key, value);
                }
            }
        }
    }

    public static class ConfigHandlerAutoSaveJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            LOGGER.debug("AutoSave ConfigWrapper {}", context.getJobDetail().getKey().getName());
            ConfigHandler<?> configHandler = (ConfigHandler<?>) context.getJobDetail().getJobDataMap().get(ConfigHandler.class.getName());
            configHandler.saveToDisk();
        }
    }

}
