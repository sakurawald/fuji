package io.github.sakurawald.module.common.manager.impl.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.classgraph.ClassInfo;
import io.github.sakurawald.core.auxiliary.JsonUtil;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.common.manager.abst.BaseManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.mixin.GlobalMixinConfigPlugin;
import lombok.SneakyThrows;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModuleManager extends BaseManager {

    public static final String COMMON_MODULE_ROOT = "common";
    private static JsonElement RC_CONFIG = null;
    private final Map<Class<? extends ModuleInitializer>, ModuleInitializer> moduleRegistry = new HashMap<>();
    private final Map<List<String>, Boolean> module2enable = new HashMap<>();

    @Override
    public void onInitialize() {
        invokeModuleInitializers();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.serverStartupReport());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private void invokeModuleInitializers() {
        for (ClassInfo classInfo : ReflectionUtil.getClassInfoScanResult().getSubclasses(ModuleInitializer.class)) {
            // module dispatch
            String className = classInfo.getName();
            if (!Managers.getModuleManager().shouldWeEnableThis(className)) continue;

            Class<? extends ModuleInitializer> clazz = (Class<? extends ModuleInitializer>) Class.forName(className);
            this.getInitializer(clazz);
        }
    }

    public void reloadModules() {
        moduleRegistry.values().forEach(initializer -> {
                    try {
                        initializer.onReload();
                    } catch (Exception e) {
                        LogUtil.cryLoudly("Failed to reload module.", e);
                    }
                }
        );
    }

    private void serverStartupReport() {
        ArrayList<String> enabledModuleList = new ArrayList<>();
        module2enable.forEach((module, enable) -> {
            if (enable) enabledModuleList.add(String.join(".", module));
        });

        enabledModuleList.sort(String::compareTo);
        LogUtil.info("Enabled {}/{} modules -> {}", enabledModuleList.size(), module2enable.size(), enabledModuleList);
    }

    @ApiStatus.AvailableSince("1.1.5")
    public boolean isModuleEnabled(List<String> modulePath) {
        return module2enable.get(modulePath);
    }

    /**
     * @return if a module is disabled, then this method will return null.
     * (If a module is enabled, but the module doesn't extend AbstractModule, then this me*
     * hod will also return null, but the module doesn't extend AbstractModule, then this method will also return null.)
     */
    @ApiStatus.AvailableSince("1.1.5")
    public <T extends ModuleInitializer> T getInitializer(@NotNull Class<T> clazz) {
        if (!moduleRegistry.containsKey(clazz)) {
            if (shouldWeEnableThis(clazz.getName())) {
                try {
                    ModuleInitializer moduleInitializer = clazz.getDeclaredConstructor().newInstance();
                    moduleInitializer.doInitialize();
                    moduleRegistry.put(clazz, moduleInitializer);
                } catch (Exception e) {
                    LogUtil.cryLoudly("Failed to initialize module %s.".formatted(clazz.getSimpleName()), e);
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
        // special case 1
        if (Configs.configHandler.model().common.debug.disable_all_modules) return false;

        // special case 2
        if (modulePath.getFirst().equals(COMMON_MODULE_ROOT)) return true;

        // cache
        if (module2enable.containsKey(modulePath)) {
            return module2enable.get(modulePath);
        }

        // soft fail if required mod is not installed.
        if (!isRequiredModsInstalled(modulePath)) {
            LogUtil.warn("Refuse to load module {} (reason: the required dependency mod isn't installed)", modulePath);
            module2enable.put(modulePath, false);
            return false;
        }

        // check enable-supplier
        boolean enable = true;
        JsonObject parent = RC_CONFIG.getAsJsonObject().get("modules").getAsJsonObject();
        for (String node : modulePath) {
            parent = parent.getAsJsonObject(node);

            if (parent == null || !parent.has("enable")) {
                throw new RuntimeException("Missing `enable supplier` key for dir name list `%s`".formatted(modulePath));
            }

            // only enable a sub-module if the parent module is enabled.
            if (!parent.getAsJsonPrimitive("enable").getAsBoolean()) {
                enable = false;
                break;
            }
        }

        // cache
        module2enable.put(modulePath, enable);
        return enable;
    }

    public static @NotNull List<String> computeModulePath(@NotNull String className) {
        if (RC_CONFIG == null) {
            RC_CONFIG = Configs.configHandler.toJsonElement();
        }

        return computeModulePath(RC_CONFIG, className);
    }

    /**
     * @return the module path for given class name, if the class is not inside a module, then a special module path List.of("common") will be returned.
     */
    public static @NotNull List<String> computeModulePath(@NotNull JsonElement rcConfig, @NotNull String className) {

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
            return List.of(COMMON_MODULE_ROOT);
        }

        String str = className.substring(left);

        /* remove trailing directories */
        int right = str.lastIndexOf(".");
        str = str.substring(0, right);

        List<String> modulePath = new ArrayList<>(List.of(str.split("\\.")));

        if (modulePath.getFirst().equals(COMMON_MODULE_ROOT)) {
            return List.of(COMMON_MODULE_ROOT);
        }

        boolean flag;
        do {
            flag = false;

            String modulePathString = String.join(".", modulePath);
            String moduleEnableSupplierJsonPath = "modules.%s.enable".formatted(modulePathString);

            // remove the trailing directories if there is no enable-supplier for this directory.
            if (!JsonUtil.existsNode((JsonObject) rcConfig, moduleEnableSupplierJsonPath)) {
                if (modulePath.isEmpty()) {
                    throw new RuntimeException("Can't find the module enable-supplier in `config.json` for class name %s. Did you forget to add the enable-supplier key in ConfigModel ?".formatted(className));
                }

                modulePath.removeLast();
                flag = true;
            }

        } while (flag);

        return modulePath;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
