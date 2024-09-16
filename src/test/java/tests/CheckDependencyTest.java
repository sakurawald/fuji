package tests;

import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.works.structure.work.abst.Work;
import io.github.sakurawald.module.mixin.GlobalMixinConfigPlugin;
import org.junit.jupiter.api.Test;
import structure.dependency_checker.ClassDependencyChecker;
import structure.dependency_checker.Dependency;
import structure.dependency_checker.ModuleDependencyChecker;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class CheckDependencyTest {
    private static final Path COMPILE_TIME_SOURCE_PATH = Path.of("src", "main", "java");

    public static final String PREFIX_COM_MOJANG = "com.mojang";
    public static final String PREFIX_LOMBOK = "lombok.";
    public static final String PREFIX_IO_GITHUB_SAKURAWALD = "io.github.sakurawald";
    public static final String PREFIX_IO_GITHUB_SAKURAWALD_CORE = "io.github.sakurawald.core";
    private static final String PREFIX_IO_GITHUB_SAKURAWALD_MODULE = "io.github.sakurawald.module";
    public static final String PREFIX_COM_MOJANG_BRIGADIER = "com.mojang.brigadier";
    public static final String PREFIX_JAVA = "java.";
    public static final String PREFIX_NET_KYORI = "net.kyori";
    public static final String PREFIX_NET_FABRICMC = "net.fabricmc";
    private static final String PREFIX_NET_MINECRAFT = "net.minecraft";
    private static final String PREFIX_ORG_JETBRAINS = "org.jetbrains";

    @Test
    void testModuleDependency() {
        List<Dependency> dependencies = new ModuleDependencyChecker().makeDependencies(COMPILE_TIME_SOURCE_PATH);

        if (!dependencies.isEmpty()) {
            dependencies.forEach(System.out::println);
            throw new RuntimeException("one module references other modules.");
        }
    }

    @Test
    void testCoreConfigModelDependency() {
        Stream<Dependency> dependencies = new ClassDependencyChecker().makeDependencies(
                COMPILE_TIME_SOURCE_PATH.resolve("io/github/sakurawald/core/config"))
            .stream()
            .filter(dep -> {
                dep.filterReference(PREFIX_COM_MOJANG);
                return !dep.getReference().isEmpty();
            });

        dependencies.forEach(dep -> {
            System.out.println(dep);
            throw new RuntimeException("one config model in `core` package references mojang classes.");
        });
    }

    @Test
    void testCoreDependency() {
        Stream<Dependency> dependencies = new ClassDependencyChecker().makeDependencies(
                COMPILE_TIME_SOURCE_PATH.resolve("io/github/sakurawald/core"))
            .stream()
            .filter(dep -> {
                dep.filterReference(
                    PREFIX_IO_GITHUB_SAKURAWALD_MODULE
                );
                dep.excludeReference(
                    ModuleInitializer.class.getName()
                    , GlobalMixinConfigPlugin.class.getName()
                    , Work.class.getName()
                );
                return !dep.getReference().isEmpty();
            });

        dependencies.forEach(dep -> {
            System.out.println(dep);
            throw new RuntimeException("the `core` package references the `module` package.");
        });
    }
}
