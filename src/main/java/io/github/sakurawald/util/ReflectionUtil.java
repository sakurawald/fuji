package io.github.sakurawald.util;

import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class ReflectionUtil {

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

}
