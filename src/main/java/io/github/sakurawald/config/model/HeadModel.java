package io.github.sakurawald.config.model;

import io.github.sakurawald.module.initializer.head.HeadInitializer;

public class HeadModel {
    public HeadInitializer.EconomyType economyType = HeadInitializer.EconomyType.ITEM;
    public String costType = "minecraft:emerald_block";
    public int costAmount = 1;
}
