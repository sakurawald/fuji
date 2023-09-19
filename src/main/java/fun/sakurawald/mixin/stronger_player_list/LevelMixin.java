package fun.sakurawald.mixin.stronger_player_list;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Level.class)
public abstract class LevelMixin {


    @Shadow
    public abstract ResourceKey<DimensionType> dimensionTypeId();
}
