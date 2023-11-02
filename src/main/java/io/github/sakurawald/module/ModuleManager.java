package io.github.sakurawald.module;

import com.google.gson.JsonElement;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import org.jetbrains.annotations.ApiStatus;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static io.github.sakurawald.Fuji.log;

public class ModuleManager {
    private static final Map<Class<? extends ModuleInitializer>, ModuleInitializer> initializers = new HashMap<>();
    private static final Map<String, Boolean> module2enable = new HashMap<>();

    @SuppressWarnings("SameParameterValue")
    private static Set<Class<? extends ModuleInitializer>> scanModules(String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getSubTypesOf(ModuleInitializer.class);
    }

    public static void initializeModules() {
        scanModules(ModuleManager.class.getPackageName()).forEach(ModuleManager::getInitializer);
    }

    public static void reloadModules() {
        initializers.values().forEach(initializer -> {
                    try {
                        initializer.onReload();
                    } catch (Exception e) {
                        log.error("Failed to reload module -> {}", e.getMessage());
                    }
                }
        );
    }

    public static void reportModules() {
        ArrayList<String> enabled = new ArrayList<>();
        module2enable.forEach((module, enable) -> {
            if (enable) enabled.add(module);
        });

        log.info("Enabled {}/{} modules -> {}", enabled.size(), module2enable.size(), enabled);
    }

    @ApiStatus.AvailableSince("1.1.5")
    public static boolean isModuleEnabled(String moduleName) {
        return module2enable.get(moduleName);
    }

    /**
     * @return if a module is disabled, then this method will return null.
     * (If a module is enabled, but the module doesn't extend AbstractModule, then this me*
     * hod will also return null, but the module doesn't extend AbstractModule, then this method will also return null.)
     */
    @ApiStatus.AvailableSince("1.1.5")
    public static <T extends ModuleInitializer> T getInitializer(Class<T> clazz) {
        JsonElement config = ConfigManager.configWrapper.toJsonElement();
        if (!initializers.containsKey(clazz)) {
            String basePackageName = calculateBasePackageName(ModuleInitializer.class, clazz.getName());
            if (enableModule(config, basePackageName)) {
                try {
                    ModuleInitializer moduleInitializer = clazz.getDeclaredConstructor().newInstance();
                    moduleInitializer.onInitialize();
                    initializers.put(clazz, moduleInitializer);
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
        }
        return clazz.cast(initializers.get(clazz));
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
