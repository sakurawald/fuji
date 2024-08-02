package io.github.sakurawald.module.initializer.head.model;

import io.github.sakurawald.module.initializer.head.structure.EconomyType;
import org.jetbrains.annotations.NotNull;

public class HeadModel {
    public EconomyType economyType = EconomyType.ITEM;
    public @NotNull String costType = "minecraft:emerald_block";
    public int costAmount = 1;
}
