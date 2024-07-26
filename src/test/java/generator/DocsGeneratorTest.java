package generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.config.model.SchedulerModel;
import io.github.sakurawald.generator.JsonDocsGenerator;
import io.github.sakurawald.generator.MarkdownDocsGenerator;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Path;

public class DocsGeneratorTest {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final Path LOCALE_PATH = Path.of("fuji-fabric.wiki", "locale");
    private static final String EN_US = "en_us";

    private String getLanguageCode(String fileName) {
        fileName = fileName.replace(".json", "");
        return fileName.toLowerCase();
    }


    @SneakyThrows
    private void generate(JsonObject jsonObject, String configFileName, String languageCode) {
        Path outputPath = LOCALE_PATH.resolve(languageCode);
        String outputFileName = "[%s]-[%s].json".formatted(configFileName, languageCode);

        String json = GSON.toJson(jsonObject);
        FileUtils.writeStringToFile(outputPath.resolve(outputFileName).toFile(), json, Charset.defaultCharset());

        outputFileName = "[%s]-[%s].md".formatted(configFileName, languageCode);
        String text = MarkdownDocsGenerator.getInstance().generate(jsonObject);
        FileUtils.writeStringToFile(outputPath.resolve(outputFileName).toFile(), text, Charset.defaultCharset());
    }

    @SneakyThrows
    private void generate(Object javaObject, String configFileName, String languageCode) {
        JsonObject configJson = JsonDocsGenerator.getInstance().generate(javaObject);
        generate(configJson, configFileName, languageCode);
    }

    @SneakyThrows
    private void generate(Path jsonFile, String languageCode) {
        JsonReader jsonReader = new JsonReader(new FileReader(jsonFile.toFile()));
        JsonObject jsonObject = GSON.fromJson(jsonReader, JsonObject.class);
        System.out.println(jsonObject);
        generate(jsonObject, jsonFile.toFile().getName(), languageCode);
    }

    @Test
    void generate() {
        generate(new ConfigModel(), "config.json", EN_US);
        generate(new SchedulerModel(), "scheduler.json", EN_US);
        generate(LOCALE_PATH.resolve("zh_cn").resolve("config.json"), "zh_cn");
    }

}
