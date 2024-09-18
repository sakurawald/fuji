package io.github.sakurawald.core.config.transformer.impl;

import io.github.sakurawald.core.config.transformer.abst.ConfigurationTransformer;

public class RenameKeyTransformer extends ConfigurationTransformer {

    final String jsonPath;
    final String oldKeyName;
    final String newKeyName;

    public RenameKeyTransformer(String jsonPath, String oldKeyName, String newKeyName) {
        this.jsonPath = jsonPath;
        this.oldKeyName = oldKeyName;
        this.newKeyName = newKeyName;
    }

    @Override
    public void apply() {
        if (notExists("%s.%s".formatted(jsonPath, newKeyName))) {
            renameKey(jsonPath, oldKeyName, newKeyName);
            writeStorage();
        }
    }
}
