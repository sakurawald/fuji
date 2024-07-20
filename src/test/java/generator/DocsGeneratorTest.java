package generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.sakurawald.config.annotation.Documentation;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.generator.JsonDocsGenerator;
import io.github.sakurawald.generator.MarkdownDocsGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
public class DocsGeneratorTest {

    @Test
    void generate() {

        // config.configJson
        Path githubWikiPath = Path.of("fuji-fabric.wiki");

        JsonObject configJson = JsonDocsGenerator.getInstance().generate(githubWikiPath.resolve("config.json"), new ConfigModel());
        MarkdownDocsGenerator.getInstance().generate(githubWikiPath.resolve("config.md"), configJson);

    }

}
