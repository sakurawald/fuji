package io.github.sakurawald.module;

import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class ModuleManager {
    private static final Map<Class<? extends AbstractModule>, AbstractModule> instances = new HashMap<>();

    @SuppressWarnings("SameParameterValue")
    private static Set<Class<? extends AbstractModule>> scanModules(String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getSubTypesOf(AbstractModule.class);
    }

    public static void initializeModules() {
        scanModules(ModuleManager.class.getPackageName()).forEach(ModuleManager::getOrNewInstance);
    }

    public static void reloadModules() {
        instances.values().forEach(AbstractModule::onReload);
    }

    /**
     * @return if a module is disabled, then this method will return null
     */
    public static <T extends AbstractModule> T getOrNewInstance(Class<T> clazz) {
        if (!instances.containsKey(clazz)) {
            String moduleName = clazz.getSimpleName();
            try {
                AbstractModule abstractModule = clazz.getDeclaredConstructor().newInstance();
                if (abstractModule.enableModule().get()) {
                    log.info("Initialize module -> {}", moduleName);
                    // initialize module here.
                    abstractModule.onInitialize();
                    instances.put(clazz, abstractModule);
                }
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return clazz.cast(instances.get(clazz));
    }
}
