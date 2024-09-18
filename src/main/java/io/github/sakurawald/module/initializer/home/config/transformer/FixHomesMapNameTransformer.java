package io.github.sakurawald.module.initializer.home.config.transformer;

import io.github.sakurawald.core.config.transformer.abst.ConfigurationTransformer;
import lombok.SneakyThrows;

public class FixHomesMapNameTransformer extends ConfigurationTransformer {

    @SneakyThrows
    @Override
    public void apply() {
        if (notExists("$.name2home")) {
            renameKey("$","homes","name2home");
            writeStorage();
        }
    }
}
