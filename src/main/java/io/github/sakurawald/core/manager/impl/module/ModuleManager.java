package io.github.sakurawald.core.manager.impl.module;

import com.google.gson.JsonObject;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.manager.Managers;
import io.github.sakurawald.core.manager.abst.BaseManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.mixin.GlobalMixinConfigPlugin;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModuleManager extends BaseManager {

    public static final String ENABLE_SUPPLIER_KEY = "enable";
    public static final String CORE_MODULE_ROOT = "core";
    private static final Set<String> MODULES = new HashSet<>(ReflectionUtil.getGraph(ReflectionUtil.MODULE_GRAPH_FILE_NAME));

    private final Map<Class<? extends ModuleInitializer>, ModuleInitializer> moduleRegistry = new HashMap<>();
    private final Map<List<String>, Boolean> module2enable = new HashMap<>();

    @Override
    public void onInitialize() {
        invokeModuleInitializers();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.serverStartupReport());
    }

    @SuppressWarnings("unchecked")
    private void invokeModuleInitializers() {
        ReflectionUtil.getGraph(ReflectionUtil.MODULE_INITIALIZER_GRAPH_FILE_NAME)
            .stream()
            .filter(className -> Managers.getModuleManager().shouldWeEnableThis(className))
            .forEach(className -> {
                try {
                    Class<? extends ModuleInitializer> clazz = (Class<? extends ModuleInitializer>) Class.forName(className);
                    this.getInitializer(clazz);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }

    public void reloadModules() {
        moduleRegistry.values().forEach(initializer -> {
                try {
                    initializer.doReload();
                } catch (Exception e) {
                    LogUtil.error("failed to reload module.", e);
                }
            }
        );
    }

    private void serverStartupReport() {
        List<String> enabledModuleList = new ArrayList<>();
        module2enable.forEach((module, enable) -> {
            if (enable) enabledModuleList.add(String.join(".", module));
        });

        enabledModuleList.sort(String::compareTo);
        LogUtil.info("enabled {}/{} modules -> {}", enabledModuleList.size(), module2enable.size(), enabledModuleList);
    }

    @SuppressWarnings("unused")
    @ApiStatus.AvailableSince("1.1.5")
    public boolean isModuleEnabled(List<String> modulePath) {
        return module2enable.get(modulePath);
    }

    /**
     * @return if a module is disabled, then this method will return null.
     * (If a module is enabled, but the module doesn't extend AbstractModule, then this me*
     * hod will also return null, but the module doesn't extend AbstractModule, then this method will also return null.)
     */
    public <T extends ModuleInitializer> T getInitializer(@NotNull Class<T> clazz) {
        if (!moduleRegistry.containsKey(clazz)) {
            if (shouldWeEnableThis(clazz.getName())) {
                try {
                    ModuleInitializer moduleInitializer = clazz.getDeclaredConstructor().newInstance();
                    moduleInitializer.doInitialize();
                    moduleRegistry.put(clazz, moduleInitializer);
                } catch (Exception e) {
                    LogUtil.error("failed to initialize module %s.".formatted(clazz.getSimpleName()), e);
                }
            }
        }
        return clazz.cast(moduleRegistry.get(clazz));
    }

    /**
     * It's possible that there is no `initializer` for a `module` (That module only required `mixin`.).
     * If you want to check whether a module is enabled or disabled, use the method `isModuleEnabled`
     *
     * @return all initializers of `enabled module`.
     */
    public Collection<ModuleInitializer> getRegisteredInitializers() {
        return this.moduleRegistry.values();
    }

    public boolean shouldWeEnableThis(String className) {
        return shouldWeEnableThis(computeModulePath(className));
    }

    private boolean shouldWeEnableThis(@NotNull List<String> modulePath) {
        if (Configs.configHandler.getModel().core.debug.disable_all_modules) return false;
        if (modulePath.getFirst().equals(CORE_MODULE_ROOT)) return true;

        // cache
        if (module2enable.containsKey(modulePath)) {
            return module2enable.get(modulePath);
        }

        // soft fail if required mod is not installed.
        if (!isRequiredModsInstalled(modulePath)) {
            LogUtil.warn("refuse to load module {} (reason: the required dependency mod isn't installed)", modulePath);
            module2enable.put(modulePath, false);
            return false;
        }

        // check enable-supplier
        boolean enable = true;
        JsonObject parent = Configs.configHandler.convertModelToJsonTree().getAsJsonObject().get("modules").getAsJsonObject();
        for (String node : modulePath) {
            parent = parent.getAsJsonObject(node);

            if (parent == null || !parent.has(ModuleManager.ENABLE_SUPPLIER_KEY)) {
                throw new RuntimeException("Missing `enable supplier` key for dir name list `%s`".formatted(modulePath));
            }

            // only enable a sub-module if the parent module is enabled.
            if (!parent.getAsJsonPrimitive(ModuleManager.ENABLE_SUPPLIER_KEY).getAsBoolean()) {
                enable = false;
                break;
            }
        }

        // cache
        module2enable.put(modulePath, enable);
        return enable;
    }

    /**
     * @return the module path for given class name, if the class is not inside a module, then a special module path List.of("core") will be returned.
     */
    public static @NotNull List<String> computeModulePath(@NotNull String className) {

        /* remove leading directories */
        int left = -1;
        List<Class<?>> modulePackagePrefixes = List.of(ModuleInitializer.class, GlobalMixinConfigPlugin.class);
        for (Class<?> modulePackagePrefix : modulePackagePrefixes) {
            String prefix = modulePackagePrefix.getPackageName();
            if (className.startsWith(prefix)) {

                // skip self
                if (className.equals(modulePackagePrefix.getName())) continue;

                left = prefix.length() + 1;
                break;
            }
        }

        if (left == -1) {
            return List.of(CORE_MODULE_ROOT);
        }

        String str = className.substring(left);

        /* remove trailing directories */
        int right = str.lastIndexOf(".");
        str = str.substring(0, right);

        List<String> modulePath = new ArrayList<>(List.of(str.split("\\.")));

        if (modulePath.getFirst().equals(CORE_MODULE_ROOT)) {
            return List.of(CORE_MODULE_ROOT);
        }

        /* remove the trailing directories until the string is a module path string */
        String modulePathString = String.join(".", modulePath);
        while (!MODULES.contains(modulePathString)) {
            // remove last!
            if (modulePath.isEmpty()) {
                throw new RuntimeException("Can't find the module enable-supplier in `config.json` for class name %s. Did you forget to add the enable-supplier key in ConfigModel ?".formatted(className));
            }
            modulePath.removeLast();

            // compute it
            modulePathString = String.join(".", modulePath);
        }

        return modulePath;
    }

    private boolean isRequiredModsInstalled(@NotNull List<String> modulePath) {

        if (modulePath.contains("carpet")) {
            return FabricLoader.getInstance().isModLoaded("carpet");
        }

        String root = modulePath.getFirst();
        if (root.equals("profiler")) {
            return FabricLoader.getInstance().isModLoaded("spark");
        }

        return true;
    }
}
