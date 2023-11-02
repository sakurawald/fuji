package io.github.sakurawald.config.base;

import assets.fuji.Cat;
import com.google.gson.*;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.works.work_type.Work;
import io.github.sakurawald.util.ScheduleUtil;
import lombok.Cleanup;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;


public abstract class ConfigWrapper<T> {
    protected static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .registerTypeAdapter(Work.class, new Work.WorkTypeAdapter())
            .create();

    protected File file;
    protected T configInstance;

    protected boolean merged = false;

    public ConfigWrapper(File file) {
        this.file = file;
    }

    public static JsonElement getJsonElement(String resourcePath) {
        try {
            InputStream inputStream = Cat.class.getResourceAsStream(resourcePath);
            assert inputStream != null;
            @Cleanup Reader reader = new BufferedReader(new InputStreamReader(inputStream));
            return JsonParser.parseReader(reader);
        } catch (Exception e) {
            Fuji.log.error(e.getMessage());
        }

        return null;
    }

    public abstract void loadFromDisk();

    public abstract void saveToDisk();


    public T instance() {
        return this.configInstance;
    }

    public JsonElement toJsonElement() {
        return gson.toJsonTree(this.configInstance);
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
            Fuji.log.error("Backup file failed: " + e.getMessage());
        }
    }

    public void autoSave(String cron) {
        String jobName = this.file.getName();
        String jobGroup = ConfigWrapperAutoSaveJob.class.getName();
        ScheduleUtil.removeJobs(jobName, jobGroup);
        ScheduleUtil.addJob(ConfigWrapperAutoSaveJob.class, jobName, jobGroup, cron, new JobDataMap() {
            {
                this.put(ConfigWrapper.class.getName(), ConfigWrapper.this);
            }
        });
    }

    public void mergeJson(JsonElement oldJson, JsonElement newJson) {
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
                    Fuji.log.warn("Add missing json property: file = {}, key = {}, value = {}", this.file.getName(), key, value);
                }
            }
        }
    }

    public static class ConfigWrapperAutoSaveJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            Fuji.log.debug("AutoSave ConfigWrapper {}", context.getJobDetail().getKey().getName());
            ConfigWrapper<?> configWrapper = (ConfigWrapper<?>) context.getJobDetail().getJobDataMap().get(ConfigWrapper.class.getName());
            configWrapper.saveToDisk();
        }
    }

}
