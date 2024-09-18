package io.github.sakurawald.module.initializer.command_toolbox.warp.config.model;

import io.github.sakurawald.module.initializer.command_toolbox.warp.structure.WarpEntry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WarpModel {
    public @NotNull Map<String, WarpEntry> name2warp = new HashMap<>();
}
