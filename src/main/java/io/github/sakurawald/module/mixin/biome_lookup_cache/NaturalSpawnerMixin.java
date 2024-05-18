package io.github.sakurawald.module.mixin.biome_lookup_cache;

import io.github.sakurawald.module.initializer.biome_lookup_cache.ChunkManager;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/*
 * Carpet Mod also includes some optimization for entity-spawn, please enable lagFreeSpawn in carpet
 * */
@Mixin(SpawnHelper.class)
public abstract class NaturalSpawnerMixin {
    @Redirect(
            method = {"mobsAt", "getRandomSpawnMobAt"},
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"
            )
    )
    private static RegistryEntry<Biome> $getRandomSpawnMobAt(ServerWorld level, BlockPos pos) {
        return ChunkManager.getRoughBiome(level, pos);
    }
}