package generator;

import com.google.gson.JsonObject;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.config.model.SchedulerModel;
import io.github.sakurawald.generator.JsonDocsGenerator;
import io.github.sakurawald.generator.MarkdownDocsGenerator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.file.Path;

public class DocsGeneratorTest {

    @SneakyThrows
    private void generate(String fileName, Object javaObject) {
        Path githubWikiPath = Path.of("fuji-fabric.wiki");
        JsonObject configJson = JsonDocsGenerator.getInstance().generate(javaObject);

        Path path = githubWikiPath.resolve("docs-gen-[%s].md".formatted(fileName));
        String text = MarkdownDocsGenerator.getInstance().generate(configJson);
        FileUtils.writeStringToFile(path.toFile(), text, Charset.defaultCharset());
    }

    @Test
    void generate() {
        generate("config.json", new ConfigModel());
        generate("scheduler.json", new SchedulerModel());
    }

}
