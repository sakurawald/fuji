package io.github.sakurawald.module.initializer.world.structure;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DimensionEntry {
    private boolean enable;
    private String dimension;
    @SerializedName(value = "dimension_type", alternate = "dimensionType")
    private String dimensionType;
    private long seed;
}
