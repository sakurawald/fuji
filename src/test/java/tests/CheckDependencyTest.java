package tests;

import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.works.structure.work.abst.Work;
import io.github.sakurawald.module.mixin.GlobalMixinConfigPlugin;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.junit.jupiter.api.Test;
import structure.dependency_checker.ClassDependencyChecker;
import structure.dependency_checker.Dependency;
import structure.dependency_checker.ModuleDependencyChecker;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class CheckDependencyTest {
    private static final Path COMPILE_TIME_SOURCE_PATH = Path.of("src", "main", "java");

    private static final String PREFIX_IO_GITHUB_SAKURAWALD = "io.github.sakurawald";
    private static final String PREFIX_IO_GITHUB_SAKURAWALD_MODULE = "io.github.sakurawald.module";

    private static final Path COMPILE_TIME_MAIN_PACKAGE_PATH = COMPILE_TIME_SOURCE_PATH.resolve(PREFIX_IO_GITHUB_SAKURAWALD.replace(".", "/"));

    private static final String PREFIX_JAVA = "java.";
    private static final String PREFIX_ORG_JETBRAINS = "org.jetbrains";
    private static final String PREFIX_LOMBOK = "lombok.";

    private static final String PREFIX_NET_MINECRAFT = "net.minecraft";
    private static final String PREFIX_COM_MOJANG = "com.mojang";

    private static final String[] MOJANG_PACKAGES = new String[]{PREFIX_COM_MOJANG, PREFIX_NET_MINECRAFT};
    private static final String[] BASE_PACKAGES = new String[]{
        PREFIX_JAVA
        , PREFIX_ORG_JETBRAINS
        , PREFIX_LOMBOK
        , MinecraftServer.class.getName()
        , ServerPlayerEntity.class.getName()
    };

    @Test
    void testModuleDependency() {
        List<Dependency> dependencies = new ModuleDependencyChecker().makeDependencies(COMPILE_TIME_SOURCE_PATH);

        if (!dependencies.isEmpty()) {
            dependencies.forEach(System.out::println);
            throw new RuntimeException("one module references other modules.");
        }
    }

    @Test
    void testCoreDependency() {
        Stream<Dependency> dependencies = new ClassDependencyChecker().makeDependencies(
                COMPILE_TIME_MAIN_PACKAGE_PATH.resolve("core"))
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

    @Test
    void testCoreConfigDependency() {
        Stream<Dependency> dependencies = new ClassDependencyChecker().makeDependencies(
                COMPILE_TIME_MAIN_PACKAGE_PATH.resolve("core").resolve("config"))
            .stream()
            .filter(dep -> {
                dep.filterReference(MOJANG_PACKAGES);
                return !dep.getReference().isEmpty();
            });

        dependencies.forEach(dep -> {
            System.out.println(dep);
            throw new RuntimeException("the `core.config` package references mojang classes.");
        });
    }

    @Test
    void testCoreManagerDependency() {
        Stream<Dependency> dependencies = new ClassDependencyChecker().makeDependencies(
                COMPILE_TIME_MAIN_PACKAGE_PATH.resolve("core").resolve("manager"))
            .stream()
            .filter(dep -> {
                dep.filterReference(
                    MOJANG_PACKAGES);
                dep.excludeReference(BASE_PACKAGES);
                return !dep.getReference().isEmpty();
            });

        dependencies.forEach(dep -> {
            System.out.println(dep);
            throw new RuntimeException("the `core.manager` package references mojang classes.");
        });
    }

    @Test
    void testCoreJobDependency() {
        Stream<Dependency> dependencies = new ClassDependencyChecker().makeDependencies(
                COMPILE_TIME_MAIN_PACKAGE_PATH.resolve("core").resolve("job"))
            .stream()
            .filter(dep -> {
                dep.filterReference(
                    MOJANG_PACKAGES);
                dep.excludeReference(BASE_PACKAGES);
                return !dep.getReference().isEmpty();
            });

        dependencies.forEach(dep -> {
            System.out.println(dep);
            throw new RuntimeException("the `core.job` package references mojang classes.");
        });
    }
}
