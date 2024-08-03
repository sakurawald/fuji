package checker;

import io.github.sakurawald.meta.checker.module_dependency.ModuleDependencyChecker;
import io.github.sakurawald.meta.checker.module_dependency.structure.Reference;
import org.junit.jupiter.api.Test;

import java.util.List;

public class CheckModuleDependencyTest {

    @Test
    void test() {
        List<Reference> refs = new ModuleDependencyChecker().check();

        System.out.println("\n=== module dependency analysis ===");
        refs.forEach(System.out::println);
        System.out.println();

        if (!refs.isEmpty()) {
            throw new RuntimeException("module dependency is not pure !");
        }
    }
}
