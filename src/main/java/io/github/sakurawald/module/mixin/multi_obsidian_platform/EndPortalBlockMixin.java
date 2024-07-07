package io.github.sakurawald.module.mixin.multi_obsidian_platform;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.multi_obsidian_platform.MultiObsidianPlatformInitializer;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {

    @Unique
    private static final MultiObsidianPlatformInitializer module = ModuleManager.getInitializer(MultiObsidianPlatformInitializer.class);

    @Unique
    BlockPos getTransformedEndSpawnPoint(Entity entity) {
        return module.transform(entity.getBlockPos());
    }

    @Unique
    World getEntityCurrentLevel(Entity entity) {
        return entity.getWorld();
    }

    @Redirect(method = "createTeleportTarget", at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ServerWorld;END_SPAWN_POS:Lnet/minecraft/util/math/BlockPos;")
    )
        /* This method will NOT be called when an entity (including player, item and other entities) jump into overworld's ender-portal-frame */
    BlockPos $createTeleportTarget(@Local(argsOnly = true) Entity entity) {
        if (getEntityCurrentLevel(entity).getRegistryKey() != World.OVERWORLD) {
            // modify: resource_world:overworld -> minecraft:the_end (default obsidian platform)
            // feature: https://bugs.mojang.com/browse/MC-252361
            return ServerWorld.END_SPAWN_POS;
        }
        return getTransformedEndSpawnPoint(entity);
    }

}
