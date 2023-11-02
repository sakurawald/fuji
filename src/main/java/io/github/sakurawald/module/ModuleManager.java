package io.github.sakurawald.module;

import com.google.gson.JsonElement;
import io.github.sakurawald.config.ConfigManager;
import lombok.Getter;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.github.sakurawald.Fuji.log;


public class ModuleManager {
    @Getter
    private static final Map<Class<? extends AbstractModule>, AbstractModule> instances = new HashMap<>();
    @Getter
    private static final Map<String, Boolean> module2enable = new HashMap<>();

    @SuppressWarnings("SameParameterValue")
    private static Set<Class<? extends AbstractModule>> scanModules(String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getSubTypesOf(AbstractModule.class);
    }

    public static void initializeModules() {
        scanModules(ModuleManager.class.getPackageName()).forEach(ModuleManager::getOrNewInstance);
    }

    public static void reloadModules() {
        instances.values().forEach(module -> {
                    try {
                        module.onReload();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
        );
    }

    public static void reportModules() {
        ArrayList<String> enabled = new ArrayList<>();
        module2enable.forEach((module, enable) -> {
            if (enable) enabled.add(module);
        });

        log.info("Enable {}/{} modules -> {}", enabled.size(), module2enable.size(), enabled);
    }

    /**
     * @return if a module is disabled, then this method will return null.
     * (If a module is enabled, but the module doesn't extend AbstractModule, then this me*
     * hod will also return nullbut the module doesn't extend AbstractModule, then this method will also return null.)
     */
    public static <T extends AbstractModule> T getOrNewInstance(Class<T> clazz) {
        JsonElement config = ConfigManager.configWrapper.toJsonElement();
        if (!instances.containsKey(clazz)) {
            String basePackageName = calculateBasePackageName(ModuleManager.class, clazz.getName());
            if (enableModule(config, basePackageName)) {
                try {
                    AbstractModule abstractModule = clazz.getDeclaredConstructor().newInstance();
                    abstractModule.onInitialize();
                    instances.put(clazz, abstractModule);
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        }
        return clazz.cast(instances.get(clazz));
    }

    public static boolean enableModule(JsonElement config, String basePackageName) {
        boolean enable;
        try {
            enable = config.getAsJsonObject().get("modules").getAsJsonObject().get(basePackageName).getAsJsonObject().get("enable").getAsBoolean();
        } catch (Exception e) {
            log.error("The enable-supplier key '{}' is missing -> force enable this module", "modules.%s.enable".formatted(basePackageName));
            enable = true;
        }

        module2enable.put(basePackageName, enable);
        return enable;
    }

    public static String calculateBasePackageName(Class<?> packageRootClass, String className) {
        String basePackageName;
        int left = packageRootClass.getPackageName().length() + 1;
        basePackageName = className.substring(left);
        int right = basePackageName.indexOf(".");
        basePackageName = basePackageName.substring(0, right);
        return basePackageName;
    }
}
