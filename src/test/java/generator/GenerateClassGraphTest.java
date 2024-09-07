package generator;

import io.github.classgraph.ScanResult;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;

public class GenerateClassGraphTest {

    @SneakyThrows
    @Test
    void test() {
        String dir = "src/main/resources/" + ReflectionUtil.class.getPackageName().replace(".","/");
        new File(dir).mkdirs();

        try (ScanResult scanResult = ReflectionUtil.makeBaseClassGraph()
            .enableAllInfo()
            .scan()) {

            try (PrintWriter writer = new PrintWriter(new File(dir, ReflectionUtil.MODULE_INITIALIZER_GRAPH_FILE_NAME))) {
                scanResult.getSubclasses(ModuleInitializer.class).getNames().forEach(writer::println);
            }

            try (PrintWriter writer = new PrintWriter(new File(dir, ReflectionUtil.ARGUMENT_TYPE_ADAPTER_GRAPH_FILE_NAME))) {
                scanResult.getSubclasses(BaseArgumentTypeAdapter.class).getNames().forEach(writer::println);
            }

        }
    }
}
