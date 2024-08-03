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

public class CheckModuleDependencyTest {

    private static final String COMMON = "common";
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

    private String getDirNameList(String className) {
        List<String> moduleNameList = extractMatches(moduleNamePattern, className, 1);
        if (moduleNameList.isEmpty()) {
            return COMMON;
        }
        return moduleNameList.getFirst();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isModule(String dirNameList) {
        JsonElement root = gson.toJsonTree(new ConfigModel());
        return JsonUtil.existsNode((JsonObject) root, "modules.%s.enable".formatted(dirNameList));
    }

    private boolean hasCommonAncestor(String a, String b) {
        String[] A = a.split("\\.");
        String[] B = b.split("\\.");
        return A[0].equals(B[0]);
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
        String definition = getDirNameList(classRef.getDefinition());
        List<String> referenceList = new ArrayList<>();

        for (String ref : classRef.getReferenceList()) {
            String reference = getDirNameList(ref);
            // skip -> common reference
            if (reference.equals(COMMON)) continue;
            // skip -> self reference
            if (definition.equals(reference) || definition.startsWith(reference)) continue;
            if (reference.startsWith(definition) && !isModule(reference)) continue;
            if (hasCommonAncestor(definition, reference) && !isModule(definition) && !isModule(reference)) continue;
            if (hasCommonAncestor(definition, reference) && isModule(definition) && !isModule(reference)) continue;

            // skip -> reference internal module
            if (reference.startsWith("_")) continue;

            referenceList.add(reference);
        }

        if (definition.equals(COMMON) || definition.equals("tester")) return null;
        if (referenceList.isEmpty()) return null;
        return new Reference(definition, referenceList);
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
