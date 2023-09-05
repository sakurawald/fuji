package fun.sakurawald.mixin.custom_stats;

import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Stats.class)
public interface StatsAccessor {
    @Accessor
    static StatType<Identifier> getCUSTOM() {
        throw new AssertionError();
    }
}
