package checker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.generator.structure.Reference;
import io.github.sakurawald.util.JsonUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CheckModuleDependencyTest {

    public static final String COMMON = "common";
    private static final Pattern importPattern = Pattern.compile("import\\s+(\\S+);");
    private static final Pattern staticImportPattern = Pattern.compile("import\\s+static\\s+(\\S+)\\.\\S+;");
    private static final Pattern moduleNamePattern = Pattern.compile("io\\.github\\.sakurawald\\.module\\.(?:initializer|mixin)\\.(\\S+)\\.\\S+;?");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private List<String> extractMatches(Pattern pattern, String text, int group) {
        Matcher matcher = pattern.matcher(text);

        List<String> ret = new ArrayList<>();
        while (matcher.find()) {
            ret.add(matcher.group(group));
        }

        return ret;
    }

    @SneakyThrows
    private List<String> getRefClassNameList(String path) {
        String text = FileUtils.readFileToString(Path.of(path).toFile(), Charset.defaultCharset());

        List<String> ret = new ArrayList<>();
        ret.addAll(extractMatches(importPattern, text, 1));
        ret.addAll(extractMatches(staticImportPattern, text, 1));
        return ret;
    }

    private List<String> filterClassName(List<String> className, String prefix) {
        return className.stream().filter(s -> s.startsWith(prefix)).toList();
    }

    private Path getCodebasePath() {
        return Path.of("src", "main", "java");
    }

    private Reference makeClassRef(Path file) {
        String className = file.toString().replace("/", ".").replace("src.main.java.", "").replace(".java", "");
        List<String> refClassNameList = getRefClassNameList(file.toString());

        // filter only project class ref
        refClassNameList = filterClassName(refClassNameList, Fuji.class.getPackage().getName());

        return new Reference(className, refClassNameList);
    }

    private String extractModuleName(String className) {
        List<String> moduleNameList = extractMatches(moduleNamePattern, className, 1);
        if (moduleNameList.isEmpty()) {
            return COMMON;
        }
        return moduleNameList.getFirst();
    }

    private boolean isRealModulePath(String moduleName) {
        JsonElement root = gson.toJsonTree(new ConfigModel());
        return JsonUtil.existsNode((JsonObject) root, "modules.%s.enable".formatted(moduleName));
    }

    private boolean isSibling(String a, String b) {
        String[] A = a.split("\\.");
        String[] B = b.split("\\.");

        if (A.length != B.length) return false;
        for (int i = 0; i < A.length - 1; i++) {
            if (!A[i].equals(B[i])) return false;
        }
        return true;
    }

    private Reference makeModuleRef(Reference classRef) {
        String definition = extractModuleName(classRef.getDefinition());
        List<String> reference = new ArrayList<>();
        for (String ref : classRef.getReference()) {
            String str = extractModuleName(ref);
            // skip -> common reference
            if (str.equals(COMMON)) continue;
            // skip -> self reference
            if (definition.equals(str) || definition.startsWith(str)) continue;
            if (str.startsWith(definition) && !isRealModulePath(str)) continue;
            if (isSibling(definition, str) && !isRealModulePath(definition) && !isRealModulePath(str)) continue;
            // skip -> reference internal module
            if (str.startsWith("_")) continue;

            reference.add(str);
        }

        if (definition.equals(COMMON)) return null;
        if (reference.isEmpty()) return null;
        return new Reference(definition, reference);
    }


    @SneakyThrows
    private List<Reference> walk(Path path) {
        /* per class reference */
        List<Reference> refs = new ArrayList<>();
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                Reference reference = makeModuleRef(makeClassRef(file));
                if (reference != null) {
                    refs.add(reference);
                }

                return FileVisitResult.CONTINUE;
            }
        });

        /* reduce */
        return Reference.reduce(refs);
    }

    @Test
    void test() {
        CheckModuleDependencyTest gen = new CheckModuleDependencyTest();

        List<Reference> refs = gen.walk(gen.getCodebasePath());

        System.out.println("\n=== module dependency analysis ===");
        refs.forEach(System.out::println);
        System.out.println();

        if (!refs.isEmpty()) {
            throw new RuntimeException("module dependency is not pure !");
        }
    }
}
