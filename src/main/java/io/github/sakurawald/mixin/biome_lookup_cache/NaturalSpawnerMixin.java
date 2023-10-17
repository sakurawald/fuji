package io.github.sakurawald.mixin.biome_lookup_cache;

import io.github.sakurawald.module.biome_lookup_cache.ChunkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/*
 * Carpet Mod also includes some optimization for entity-spawn, please enable lagFreeSpawn in carpet
 * */
@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerMixin {
    @Redirect(
            method = {"mobsAt", "getRandomSpawnMobAt"},
            require = 0,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;"
            )
    )
    private static Holder<Biome> $getRandomSpawnMobAt(ServerLevel level, BlockPos pos) {
        return ChunkManager.getRoughBiome(level, pos);
    }
}