package io.github.sakurawald.mixin.resource_world.registry;

import io.github.sakurawald.module.resource_world.FilteredRegistry;
import io.github.sakurawald.module.resource_world.interfaces.DimensionOptionsMixinInterface;
import net.minecraft.core.Registry;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(WorldDimensions.class)
public class DimensionOptionsRegistryHolderMixin {
    /* Prevent resource worlds to write in level.dat */
    @ModifyArg(method = "method_45516", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/MapCodec;forGetter(Ljava/util/function/Function;)Lcom/mojang/serialization/codecs/RecordCodecBuilder;"))
    private static Function<Object, Registry<LevelStem>> fuji$swapRegistryGetter(Function<Object, Registry<LevelStem>> getter) {
        return (x) -> new FilteredRegistry<>(getter.apply(x), DimensionOptionsMixinInterface.SAVE_PROPERTIES_PREDICATE);
    }
}
