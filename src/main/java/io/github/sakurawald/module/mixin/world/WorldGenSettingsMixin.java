package io.github.sakurawald.module.mixin.world;

import com.google.common.collect.Maps;
import io.github.sakurawald.module.initializer.world.interfaces.IDimensionOptions;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionOptionsRegistryHolder;
import net.minecraft.world.level.WorldGenSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(WorldGenSettings.class)
public class WorldGenSettingsMixin {

    @ModifyArg(method = "encode(Lcom/mojang/serialization/DynamicOps;Lnet/minecraft/world/gen/GeneratorOptions;Lnet/minecraft/world/dimension/DimensionOptionsRegistryHolder;)Lcom/mojang/serialization/DataResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/WorldGenSettings;<init>(Lnet/minecraft/world/gen/GeneratorOptions;Lnet/minecraft/world/dimension/DimensionOptionsRegistryHolder;)V"), index = 1)
    private static DimensionOptionsRegistryHolder $wrapWorldGenSettings(DimensionOptionsRegistryHolder original) {
        Map<RegistryKey<DimensionOptions>, DimensionOptions> dimensions = original.comp_1014();
        var saveDimensions = Maps.filterEntries(dimensions, entry -> IDimensionOptions.SAVE_PROPERTIES_PREDICATE.test(entry.getValue()));
        return new DimensionOptionsRegistryHolder(saveDimensions);
    }
}
