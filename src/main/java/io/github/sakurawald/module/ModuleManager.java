package io.github.sakurawald.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.reflections.Reflections;

import javax.naming.OperationNotSupportedException;
import java.util.*;

import static io.github.sakurawald.Fuji.LOGGER;

@Slf4j
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
                    } catch (OperationNotSupportedException e) {
                        // no-op
                    } catch (Exception e) {
                        LOGGER.error("Failed to reload module -> {}", e.getMessage());
                    }
                }
        );
    }

    public static void reportModules() {
        ArrayList<String> enabled = new ArrayList<>();
        module2enable.forEach((module, enable) -> {
            if (enable) enabled.add(module);
        });

        LOGGER.info("Enabled {}/{} modules -> {}", enabled.size(), module2enable.size(), enabled);
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
        JsonElement config = Configs.configHandler.toJsonElement();
        if (!initializers.containsKey(clazz)) {
            if (shouldEnableModule(config, getPackagePath(ModuleInitializer.class, clazz.getName()))) {
                try {
                    ModuleInitializer moduleInitializer = clazz.getDeclaredConstructor().newInstance();
                    moduleInitializer.initialize();
                    initializers.put(clazz, moduleInitializer);
                } catch (Exception e) {
                    LOGGER.error(e.toString());
                }
            }
        }
        return clazz.cast(initializers.get(clazz));
    }

    public static boolean shouldEnableModule(JsonElement config, List<String> packagePath) {
        String basePackagePath = packagePath.getFirst();
        if (!isRequiredModsInstalled(packagePath)) {
            Fuji.LOGGER.warn("Can't load module {} (reason: the required dependency mod isn't installed)", packagePath);
            module2enable.put(basePackagePath, false);
            return false;
        }

        // note: if missing the `enable supplier` for the package, then it's considered as `enable = true`
        boolean enable = true;
        JsonObject parent = config.getAsJsonObject().get("modules").getAsJsonObject();
        for (String packageName : packagePath) {
            if (!parent.has(packageName)) break;
            parent = parent.getAsJsonObject(packageName);
            if (!parent.has("enable")) break;
            if (!parent.getAsJsonPrimitive("enable").getAsBoolean()) {
                enable = false;
                break;
            }
        }

        // note: this only means that the module is enabled. (maybe partial enabled)
        module2enable.put(basePackagePath, enable);
        return enable;
    }

    public static List<String> getPackagePath(Class<?> rootPackageClass, String className) {
        String ret;
        int left = rootPackageClass.getPackageName().length() + 1;
        ret = className.substring(left);

        int right = ret.lastIndexOf(".");
        ret = ret.substring(0, right);

        return List.of(ret.split("\\."));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isRequiredModsInstalled(List<String> packagePath) {
        String basePackagePath = packagePath.getFirst();

        if (basePackagePath.equals("better_info") || basePackagePath.equals("fake_player_manager")) {
            return FabricLoader.getInstance().isModLoaded("carpet");
        }

        if (basePackagePath.equals("profiler")) {
            return FabricLoader.getInstance().isModLoaded("spark");
        }

        return true;
    }
}
