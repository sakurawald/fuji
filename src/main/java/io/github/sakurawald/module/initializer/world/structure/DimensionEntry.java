package io.github.sakurawald.module.initializer.world.structure;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimensionEntry {
    private boolean enable;
    private String dimension;
    private String dimensionType;
    private long seed;
}
