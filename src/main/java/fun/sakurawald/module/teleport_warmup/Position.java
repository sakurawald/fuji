package fun.sakurawald.module.teleport_warmup;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.world.level.Level;

@Data
@AllArgsConstructor
public class Position {
    private Level level;
    private double x;
    private double y;
    private double z;

    private float yaw;
    private float pitch;
}
