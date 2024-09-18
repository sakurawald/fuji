package io.github.sakurawald.core.structure;

import io.github.sakurawald.core.auxiliary.minecraft.RegistryHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

@Data
@AllArgsConstructor
public class TeleportSetup {
    private String dimension;
    private int centerX;
    private int centerZ;
    private boolean circle;
    private int minRange;
    private int maxRange;
    private int minY;
    private int maxY;
    private int maxTryTimes;

    // circle
    public ServerWorld ofWorld() {
        return RegistryHelper.ofServerWorld(Identifier.of(this.dimension));
    }

}

