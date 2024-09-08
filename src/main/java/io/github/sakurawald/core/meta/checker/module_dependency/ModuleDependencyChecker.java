package io.github.sakurawald.core.meta.checker.module_dependency;

import com.google.gson.JsonElement;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.core.config.model.ConfigModel;
import io.github.sakurawald.core.meta.checker.module_dependency.structure.Reference;
import io.github.sakurawald.core.manager.impl.module.ModuleManager;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

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

public class ModuleDependencyChecker {

    private static final Pattern importPattern = Pattern.compile("import\\s+(\\S+);");
    private static final Pattern staticImportPattern = Pattern.compile("import\\s+static\\s+(\\S+)\\.\\S+;");
    private static final JsonElement rcConfig = ConfigHandler.getGson().toJsonTree(new ConfigModel());

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

    private Reference makeModuleRef(Reference classRef) {
        String definition = String.join(".", ModuleManager.computeModulePath(rcConfig, classRef.getDefinition()));
        List<String> referenceList = new ArrayList<>();

        for (String ref : classRef.getReferenceList()) {
            String reference = String.join(".", ModuleManager.computeModulePath(rcConfig, ref));
            // skip -> common reference
            if (reference.equals(ModuleManager.CORE_MODULE_ROOT)) continue;
            // skip -> self reference
            if (definition.equals(reference)) continue;
            // skip -> parent reference
            if (definition.startsWith(reference)) continue;

            referenceList.add(reference);
        }

        if (definition.equals(ModuleManager.CORE_MODULE_ROOT) || definition.equals("tester")) return null;
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

    public List<Reference> check() {
        return this.walk(this.getCodebasePath());
    }
}
