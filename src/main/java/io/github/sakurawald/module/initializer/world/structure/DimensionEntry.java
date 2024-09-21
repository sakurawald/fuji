package io.github.sakurawald.module.initializer.world.structure;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@SuppressWarnings("unused")
@Data
@AllArgsConstructor
public class DimensionEntry {
    public boolean enable;
    public String dimension;
    @SerializedName(value = "dimension_type", alternate = "dimensionType")
    public String dimension_type;
    public long seed;
}
