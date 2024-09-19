package io.github.sakurawald.core.config.transformer.impl;

import com.google.gson.JsonObject;
import io.github.sakurawald.core.config.transformer.abst.ConfigurationTransformer;


public class FlattenModulesTransformer extends ConfigurationTransformer {

    @Override
    public void apply() {
        String modulesPath = "$.modules";
        JsonObject modules = (JsonObject) read(modulesPath);

        for (String topLevelModule : modules.keySet()) {
            String topLevel = "config." + topLevelModule;
            FlattenTreeTransformer flattenTreeTransformer = new FlattenTreeTransformer(modulesPath + "." + topLevelModule, topLevel, "enable");
            flattenTreeTransformer.configure(this.getPath());
            flattenTreeTransformer.apply();
        }
    }
}
