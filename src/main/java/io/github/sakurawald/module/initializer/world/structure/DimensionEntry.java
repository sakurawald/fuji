package io.github.sakurawald.module.initializer.world.structure;

import lombok.AllArgsConstructor;
import lombok.Data;

@SuppressWarnings("unused")
@Data
@AllArgsConstructor
public class DimensionEntry {
    public boolean enable;
    public String dimension;
    public String dimension_type;
    public long seed;
}
