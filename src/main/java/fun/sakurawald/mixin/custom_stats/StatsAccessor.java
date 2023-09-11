package fun.sakurawald.mixin.custom_stats;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Stats.class)
public interface StatsAccessor {
    @Accessor
    static StatType<ResourceLocation> getCUSTOM() {
        throw new AssertionError();
    }
}
