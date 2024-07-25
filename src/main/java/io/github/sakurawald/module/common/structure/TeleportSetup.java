package io.github.sakurawald.module.common.structure;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeleportSetup {
    private String dimension;
    private double centerX;
    private double centerZ;
    private double minRange;
    private double maxRange;
    private double minY;
    private double maxY;

    // circle
    // ignore water
    // ignore lava
    // ignorePowderSnow
}
