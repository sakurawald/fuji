package io.github.sakurawald.module.initializer.command_toolbox.warp.config.transformer;

import io.github.sakurawald.core.config.transformer.abst.ConfigurationTransformer;
import lombok.SneakyThrows;

public class FixWarpsMapNameTransformer extends ConfigurationTransformer {

    @SneakyThrows
    @Override
    public void apply() {
        if (notExists("$.name2warp")) {
            renameKey("$","warps","name2warp");
            writeStorage();
        }
    }
}
