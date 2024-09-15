package tests;

import auxiliary.TestUtility;
import io.github.classgraph.ScanResult;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class GenerateClassGraphTest {

    public static final Path COMPILE_TIME_SRC_MAIN_RESOURCES_PATH = Path.of("src/main/resources/");
    public static final Path COMPILE_TIME_GRAPH_PATH = COMPILE_TIME_SRC_MAIN_RESOURCES_PATH.resolve(ReflectionUtil.class.getPackageName().replace(".", "/"));
    public static final Path COMPILE_TIME_LANGUAGE_PATH = COMPILE_TIME_SRC_MAIN_RESOURCES_PATH.toAbsolutePath().resolve(Fuji.class.getPackageName().replace(".", "/")).resolve("lang");

    @SneakyThrows
    @Test
    void test() {

        // scan source
        try (ScanResult scanResult = TestUtility.makeBaseClassGraph()
            .enableAllInfo()
            .scan()) {

            Path path = COMPILE_TIME_GRAPH_PATH;
            Files.createDirectories(path);

            try (PrintWriter writer = new PrintWriter(path.resolve(ReflectionUtil.MODULE_INITIALIZER_GRAPH_FILE_NAME).toFile())) {
                scanResult.getSubclasses(ModuleInitializer.class).getNames().stream().sorted().forEach(writer::println);
            }

            try (PrintWriter writer = new PrintWriter(path.resolve(ReflectionUtil.ARGUMENT_TYPE_ADAPTER_GRAPH_FILE_NAME).toFile())) {
                scanResult.getSubclasses(BaseArgumentTypeAdapter.class).getNames().stream().sorted().forEach(writer::println);
            }
        }

        // scan resource
        try (PrintWriter writer = new PrintWriter(COMPILE_TIME_GRAPH_PATH.resolve(ReflectionUtil.LANGUAGE_GRAPH_FILE_NAME).toFile())) {
            Arrays.stream(Objects.requireNonNull(COMPILE_TIME_LANGUAGE_PATH.toFile().listFiles())).forEach(file -> writer.println(file.getName()));
        }
    }
}
