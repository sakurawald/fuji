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
            FlattenTransformer flattenTransformer = new FlattenTransformer(modulesPath + "." + topLevelModule, topLevel);
            flattenTransformer.configure(this.getPath());
            flattenTransformer.apply();
        }
    }
}
