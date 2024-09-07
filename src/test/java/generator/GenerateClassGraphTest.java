package generator;

import io.github.classgraph.ScanResult;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.command.argument.adapter.abst.BaseArgumentTypeAdapter;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

public class GenerateClassGraphTest {

    @SneakyThrows
    @Test
    void test() {
        String dir = ReflectionUtil.SRC_MAIN_RESOURCES + ReflectionUtil.class.getPackageName().replace(".","/");
        new File(dir).mkdirs();

        try (ScanResult scanResult = ReflectionUtil.makeBaseClassGraph()
            .enableAllInfo()
            .scan()) {

            try (PrintWriter writer = new PrintWriter(new File(dir, ReflectionUtil.MODULE_INITIALIZER_GRAPH_FILE_NAME))) {
                List<String> temp = scanResult.getSubclasses(ModuleInitializer.class).getNames();
                temp.sort(String::compareTo);
                temp.forEach(writer::println);
            }

            try (PrintWriter writer = new PrintWriter(new File(dir, ReflectionUtil.ARGUMENT_TYPE_ADAPTER_GRAPH_FILE_NAME))) {
                List<String> temp = scanResult.getSubclasses(BaseArgumentTypeAdapter.class).getNames();
                temp.sort(String::compareTo);
                temp.forEach(writer::println);
            }

        }
    }
}
