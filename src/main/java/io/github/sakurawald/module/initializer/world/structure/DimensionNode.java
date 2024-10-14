package io.github.sakurawald.module.initializer.world.structure;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;

@SuppressWarnings("unused")
@Data
@AllArgsConstructor
public class DimensionNode {
    boolean enable;
    String dimension;
    @SerializedName(value = "dimension_type", alternate = "dimensionType")
    String dimension_type;
    long seed;
}
