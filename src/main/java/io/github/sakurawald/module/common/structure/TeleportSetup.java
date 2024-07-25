package io.github.sakurawald.module.common.structure;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.util.minecraft.IdentifierHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

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
        return IdentifierHelper.ofServerWorld(Identifier.of(this.dimension));
    }

    public static Optional<TeleportSetup> of(ServerWorld world) {
        List<TeleportSetup> list = Configs.configHandler.model().modules.rtp.setup.dimension;
        String dimension = IdentifierHelper.ofString(world);
        return list.stream().filter(o -> o.getDimension().equals(dimension)).findFirst();
    }
}

