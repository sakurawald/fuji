package fun.sakurawald.mixin.resource_world.registry;

import fun.sakurawald.module.resource_world.FilteredRegistry;
import fun.sakurawald.module.resource_world.interfaces.DimensionOptionsMixinInterface;
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
    private static Function<Object, Registry<DimensionOptions>> sakurawald$swapRegistryGetter(Function<Object, Registry<DimensionOptions>> getter) {
        return (x) -> new FilteredRegistry<>(getter.apply(x), DimensionOptionsMixinInterface.SAVE_PROPERTIES_PREDICATE);
    }
}
