package io.github.sakurawald.core.structure;

import io.github.sakurawald.core.auxiliary.minecraft.RegistryHelper;
import lombok.Data;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
@Data
public class TeleportSetup {
    final String dimension;
    final int centerX;
    final int centerZ;
    final boolean circle;
    final int minRange;
    final int maxRange;
    final int minY;
    final int maxY;
    final int maxTryTimes;

    // circle
    public ServerWorld ofWorld() {
        return RegistryHelper.ofServerWorld(Identifier.of(this.dimension));
    }

}

