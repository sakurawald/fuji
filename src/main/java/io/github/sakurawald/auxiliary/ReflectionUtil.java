package io.github.sakurawald.auxiliary;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.common.manager.impl.module.ModuleManager;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class ReflectionUtil {

    private static ScanResult CLASS_INFO_SCAN_RESULT = null;
    private static ScanResult CLASS_ANNOTATION_SCAN_RESULT = null;

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

    private static ClassGraph makeBaseClassGraph() {
        return new ClassGraph();
    }

    // Use Class.forName() to call context class loader, so that fabric's knot class loader will be used.
    public static ScanResult getClassInfoScanResult() {
        if (CLASS_INFO_SCAN_RESULT == null) {
            CLASS_INFO_SCAN_RESULT = makeBaseClassGraph()
                    .enableClassInfo()
                    .acceptPackages(Fuji.class.getPackageName())
                    .scan();
        }

        return CLASS_INFO_SCAN_RESULT;
    }

    public static ScanResult getClassAnnotationInfoScanResult() {
        if (CLASS_ANNOTATION_SCAN_RESULT == null) {
            CLASS_ANNOTATION_SCAN_RESULT = makeBaseClassGraph()
                    .enableClassInfo()
                    .enableAnnotationInfo()
                    .acceptPackages(Fuji.class.getPackageName())
                    .scan();
        }

        return CLASS_ANNOTATION_SCAN_RESULT;
    }

    public static String getModulePath(Object object) {
        return String.join(".",ModuleManager.computeModulePath(object.getClass().getName()));
    }
}
