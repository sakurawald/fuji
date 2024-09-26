package io.github.sakurawald.module.initializer.command_toolbox.warp.structure;

import io.github.sakurawald.core.structure.SpatialPose;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WarpEntry {
    SpatialPose position;
}
