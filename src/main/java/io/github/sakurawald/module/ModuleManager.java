package io.github.sakurawald.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.LogUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import javax.naming.OperationNotSupportedException;
import java.util.*;

public class ModuleManager {
    private static final Map<Class<? extends ModuleInitializer>, ModuleInitializer> initializers = new HashMap<>();
    private static final Map<List<String>, Boolean> module2enable = new HashMap<>();

    @SuppressWarnings("SameParameterValue")
    private static Set<Class<? extends ModuleInitializer>> scanModules(String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getSubTypesOf(ModuleInitializer.class);
    }

    public static void initialize() {
        initializeModules();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> ModuleManager.reportModules());
    }

    private static void initializeModules() {
        scanModules(ModuleManager.class.getPackageName()).forEach(ModuleManager::getInitializer);
    }

    public static void reloadModules() {
        initializers.values().forEach(initializer -> {
                    try {
                        initializer.onReload();
                    } catch (OperationNotSupportedException e) {
                        // no-op
                    } catch (Exception e) {
                        LogUtil.cryLoudly("Failed to reload module.", e);
                    }
                }
        );
    }

    private static void reportModules() {
        ArrayList<String> enabled = new ArrayList<>();
        module2enable.forEach((module, enable) -> {
            if (enable) enabled.add(String.join(".", module));
        });

        enabled.sort(String::compareTo);
        LogUtil.info("Enabled {}/{} modules -> {}", enabled.size(), module2enable.size(), enabled);
    }

    @ApiStatus.AvailableSince("1.1.5")
    public static boolean isModuleEnabled(List<String> packagePath) {
        return module2enable.get(packagePath);
    }

    /**
     * @return if a module is disabled, then this method will return null.
     * (If a module is enabled, but the module doesn't extend AbstractModule, then this me*
     * hod will also return null, but the module doesn't extend AbstractModule, then this method will also return null.)
     */
    @ApiStatus.AvailableSince("1.1.5")
    public static <T extends ModuleInitializer> T getInitializer(@NotNull Class<T> clazz) {
        JsonElement config = Configs.configHandler.toJsonElement();
        if (!initializers.containsKey(clazz)) {
            if (shouldEnableModule(config, getPackagePath(ModuleInitializer.class, clazz.getName()))) {
                try {
                    ModuleInitializer moduleInitializer = clazz.getDeclaredConstructor().newInstance();
                    moduleInitializer.initialize();
                    initializers.put(clazz, moduleInitializer);
                } catch (Exception e) {
                    LogUtil.cryLoudly("Failed to initialize module %s.".formatted(clazz.getSimpleName()), e);
                }
            }
        }
        return clazz.cast(initializers.get(clazz));
    }

    public static boolean shouldEnableModule(@NotNull JsonElement config, @NotNull List<String> packagePath) {
        if (module2enable.containsKey(packagePath)) {
            return module2enable.get(packagePath);
        }

        if (!isRequiredModsInstalled(packagePath)) {
            LogUtil.warn("Can't load module {} (reason: the required dependency mod isn't installed)", packagePath);
            module2enable.put(packagePath, false);
            return false;
        }

        // note: if missing the `enable supplier` for the package, then it's considered as `enable = true`
        boolean enable = true;
        JsonObject parent = config.getAsJsonObject().get("modules").getAsJsonObject();
        for (String packageName : packagePath) {
            parent = parent.getAsJsonObject(packageName);
            if (parent == null || !parent.has("enable")) {
                throw new RuntimeException("Missing `enable supplier` key for package path %s".formatted(packagePath));
            }
            if (!parent.getAsJsonPrimitive("enable").getAsBoolean()) {
                enable = false;
                break;
            }
        }

        // note: this only means that the module is enabled. (maybe partial enabled)
        module2enable.put(packagePath, enable);
        return enable;
    }

    public static @NotNull List<String> getPackagePath(@NotNull Class<?> rootPackageClass, @NotNull String className) {
        String ret;
        int left = rootPackageClass.getPackageName().length() + 1;
        ret = className.substring(left);

        int right = ret.lastIndexOf(".");
        ret = ret.substring(0, right);

        return List.of(ret.split("\\."));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isRequiredModsInstalled(@NotNull List<String> packagePath) {
        String basePackagePath = packagePath.getFirst();

        if (basePackagePath.equals("carpet")) {
            return FabricLoader.getInstance().isModLoaded("carpet");
        }

        if (basePackagePath.equals("profiler")) {
            return FabricLoader.getInstance().isModLoaded("spark");
        }

        return true;
    }
}
