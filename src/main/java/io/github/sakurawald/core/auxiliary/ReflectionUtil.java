package io.github.sakurawald.core.auxiliary;

import io.github.classgraph.ClassGraph;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.manager.impl.module.ModuleManager;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@UtilityClass
public class ReflectionUtil {

    public static final String MODULE_INITIALIZER_GRAPH_FILE_NAME = "module-initializer-graph.txt";
    public static final String ARGUMENT_TYPE_ADAPTER_GRAPH_FILE_NAME = "argument-type-adapter-graph.txt";
    public static final String SRC_MAIN_RESOURCES = "src/main/resources/";

    public static Set<Method> getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        Set<Method> methods = new HashSet<>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(annotation)) {
                methods.add(declaredMethod);
            }

        }
        return methods;
    }

    public static ClassGraph makeBaseClassGraph() {
        return new ClassGraph()
            .acceptPackages(Fuji.class.getPackageName());
    }

    @SneakyThrows
    public static List<String> getGraph(String graphName) {
        InputStream inputStream = ReflectionUtil.class.getResourceAsStream(graphName);

        assert inputStream != null;
        @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

    public static String getModulePath(Object object) {
        return String.join(".", ModuleManager.computeModulePath(object.getClass().getName()));
    }
}
