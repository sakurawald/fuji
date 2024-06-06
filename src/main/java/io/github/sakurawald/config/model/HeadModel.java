package io.github.sakurawald.config.model;

import io.github.sakurawald.module.initializer.head.HeadModule;

public class HeadModel extends AbstractModel {
    public HeadModule.EconomyType economyType = HeadModule.EconomyType.ITEM;
    public String costType = "minecraft:emerald_block";
    public int costAmount = 1;
}
