package io.github.sakurawald.config.model;

import io.github.sakurawald.module.initializer.head.HeadInitializer;
import org.jetbrains.annotations.NotNull;

public class HeadModel {
    public HeadInitializer.@NotNull EconomyType economyType = HeadInitializer.EconomyType.ITEM;
    public @NotNull String costType = "minecraft:emerald_block";
    public int costAmount = 1;
}
