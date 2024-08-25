package io.github.sakurawald.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.manager.interfaces.AbstractManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.auxiliary.LogUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.*;

public class ModuleManager extends AbstractManager {

    private final JsonElement RC_CONFIG = Configs.configHandler.toJsonElement();
    private final Map<Class<? extends ModuleInitializer>, ModuleInitializer> moduleRegistry = new HashMap<>();
    private final Map<List<String>, Boolean> module2enable = new HashMap<>();

    @Override
    public void onInitialize() {
        callAllModuleInitializer();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.reportModules());
    }

    private void callAllModuleInitializer() {
        Reflections reflections = new Reflections(this.getClass().getPackageName());
        reflections.getSubTypesOf(ModuleInitializer.class).forEach(this::getInitializer);
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

    private void reportModules() {
        ArrayList<String> enabledModuleList = new ArrayList<>();
        module2enable.forEach((module, enable) -> {
            if (enable) enabledModuleList.add(String.join(".", module));
        });

        enabledModuleList.sort(String::compareTo);
        LogUtil.info("Enabled {}/{} modules -> {}", enabledModuleList.size(), module2enable.size(), enabledModuleList);
    }

    @ApiStatus.AvailableSince("1.1.5")
    public boolean isModuleEnabled(List<String> dirNameList) {
        return module2enable.get(dirNameList);
    }

    /**
     * @return if a module is disabled, then this method will return null.
     * (If a module is enabled, but the module doesn't extend AbstractModule, then this me*
     * hod will also return null, but the module doesn't extend AbstractModule, then this method will also return null.)
     */
    @ApiStatus.AvailableSince("1.1.5")
    public <T extends ModuleInitializer> T getInitializer(@NotNull Class<T> clazz) {
        if (!moduleRegistry.containsKey(clazz)) {
            if (shouldWeEnableThisModule(RC_CONFIG, getDirNameList(ModuleInitializer.class, clazz.getName()))) {
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
    public Collection<ModuleInitializer> getInitializers() {
        return this.moduleRegistry.values();
    }

    public boolean shouldWeEnableThisModule(@NotNull JsonElement config, @NotNull List<String> dirNameList) {
        // cache
        if (module2enable.containsKey(dirNameList)) {
            return module2enable.get(dirNameList);
        }

        // soft fail if required mod is not installed.
        if (!isRequiredModsInstalled(dirNameList)) {
            LogUtil.warn("Refuse to load module {} (reason: the required dependency mod isn't installed)", dirNameList);
            module2enable.put(dirNameList, false);
            return false;
        }

        // check enable-supplier
        boolean enable = true;
        JsonObject parent = config.getAsJsonObject().get("modules").getAsJsonObject();
        for (String dirName : dirNameList) {
            parent = parent.getAsJsonObject(dirName);

            if (parent == null || !parent.has("enable")) {
                throw new RuntimeException("Missing `enable supplier` key for dir name list `%s`".formatted(dirNameList));
            }

            // only enable a sub-module if the parent module is enabled.
            if (!parent.getAsJsonPrimitive("enable").getAsBoolean()) {
                enable = false;
                break;
            }
        }

        // cache
        module2enable.put(dirNameList, enable);
        return enable;
    }

    public @NotNull List<String> getDirNameList(@NotNull Class<?> rootPackageClass, @NotNull String className) {
        String ret;
        int left = rootPackageClass.getPackageName().length() + 1;
        ret = className.substring(left);

        int right = ret.lastIndexOf(".");
        ret = ret.substring(0, right);

        return List.of(ret.split("\\."));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isRequiredModsInstalled(@NotNull List<String> dirNameList) {
        String firstDirName = dirNameList.getFirst();

        if (dirNameList.contains("carpet")) {
            return FabricLoader.getInstance().isModLoaded("carpet");
        }

        if (firstDirName.equals("profiler")) {
            return FabricLoader.getInstance().isModLoaded("spark");
        }

        return true;
    }
}
