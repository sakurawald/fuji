package structure.dependency_checker;

import com.google.gson.JsonElement;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.core.config.handler.abst.ConfigHandler;
import io.github.sakurawald.core.config.model.ConfigModel;
import io.github.sakurawald.core.manager.impl.module.ModuleManager;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModuleDependencyChecker extends ClassDependencyChecker {

    public static final JsonElement rcConfig = ConfigHandler.getGson().toJsonTree(new ConfigModel());

    private Dependency groupSymbol(Dependency classRef) {
        String definition = String.join(".", ModuleManager.computeModulePath(rcConfig, classRef.getDefinition()));
        List<String> referenceList = new ArrayList<>();

        for (String ref : classRef.getReference()) {
            String reference = String.join(".", ModuleManager.computeModulePath(rcConfig, ref));
            // skip -> common reference
            if (reference.equals(ModuleManager.CORE_MODULE_ROOT)) continue;
            // skip -> self reference
            if (definition.equals(reference)) continue;
            // skip -> parent reference
            if (definition.startsWith(reference)) continue;

            referenceList.add(reference);
        }

        if (definition.equals(ModuleManager.CORE_MODULE_ROOT) || definition.equals("tester")) return null;
        if (referenceList.isEmpty()) return null;
        return new Dependency(definition, referenceList);
    }

    @Override
    public @Nullable Dependency makeDependency(Path file) {
        Dependency dependency = super.makeDependency(file);

        // filter: only collect project source file, and group them into modules.
        dependency.filterReference(Fuji.class.getPackageName());

        return groupSymbol(dependency);
    }
}
