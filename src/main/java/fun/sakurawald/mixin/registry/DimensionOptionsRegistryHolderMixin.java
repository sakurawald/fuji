package fun.sakurawald.mixin.registry;

import fun.sakurawald.resource_world.FantasyDimensionOptions;
import fun.sakurawald.resource_world.FilteredRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(DimensionOptionsRegistryHolder.class)
public class DimensionOptionsRegistryHolderMixin {
    /* Prevent resource worlds to write in level.dat */
    @ModifyArg(method = "method_45516", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/MapCodec;forGetter(Ljava/util/function/Function;)Lcom/mojang/serialization/codecs/RecordCodecBuilder;"))
    private static Function<Object, Registry<DimensionOptions>> fantasy$swapRegistryGetter(Function<Object, Registry<DimensionOptions>> getter) {
        return (x) -> new FilteredRegistry<>(getter.apply(x), FantasyDimensionOptions.SAVE_PROPERTIES_PREDICATE);
    }
}
