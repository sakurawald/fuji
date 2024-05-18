package io.github.sakurawald.module.mixin.multi_obsidian_platform;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.multi_obsidian_platform.MultiObsidianPlatformModule;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)

public abstract class EntityMixin {

    @Unique
    private static final MultiObsidianPlatformModule module = ModuleManager.getInitializer(MultiObsidianPlatformModule.class);

    @Unique
    BlockPos getTransformedEndSpawnPoint() {
        Entity entity = (Entity) (Object) this;
        return module.transform(entity.getBlockPos());
    }

    @Unique
    World getEntityCurrentLevel() {
        Entity entity = (Entity) (Object) this;
        return entity.getWorld();
    }

    @Redirect(method = "getTeleportTarget", at = @At(value = "FIELD", target = "Lnet/minecraft/server/world/ServerWorld;END_SPAWN_POS:Lnet/minecraft/util/math/BlockPos;"), require = 1)
    BlockPos $findDimensionEntryPoint(ServerWorld toLevel) {
        // modify: resource_world:overworld -> minecraft:the_end (default obsidian platform)
        // feature: https://bugs.mojang.com/browse/MC-252361
        if (getEntityCurrentLevel().getRegistryKey() != World.OVERWORLD) return ServerWorld.END_SPAWN_POS;
        return getTransformedEndSpawnPoint();
    }

    /* This method will NOT be called when a PLAYER jump into overworld's ender-portal-frame */
    @Redirect(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;createEndSpawnPlatform(Lnet/minecraft/server/world/ServerWorld;)V"), require = 1)
    public void $changeDimension(ServerWorld toLevel) {
        // modify: resource_world:overworld -> minecraft:the_end (default obsidian platform)
        if (getEntityCurrentLevel().getRegistryKey() != World.OVERWORLD) {
            ServerWorld.createEndSpawnPlatform(toLevel);
            return;
        }
        module.makeObsidianPlatform(toLevel, getTransformedEndSpawnPoint());
    }
}
