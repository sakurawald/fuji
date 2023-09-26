package fun.sakurawald.mixin.multi_obsidian_platform;

import fun.sakurawald.module.multi_obsidian_platform.MultiObsidianPlatformModule;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
@Slf4j
public abstract class EntityMixin {
    @Unique
    BlockPos getTransformedEndSpawnPoint() {
        Entity entity = (Entity) (Object) this;
        return MultiObsidianPlatformModule.transform(entity.blockPosition());
    }

    @Unique
    Level getEntityCurrentLevel() {
        Entity entity = (Entity) (Object) this;
        return entity.level();
    }

    @Redirect(method = "findDimensionEntryPoint", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerLevel;END_SPAWN_POINT:Lnet/minecraft/core/BlockPos;"))
    BlockPos $findDimensionEntryPoint(ServerLevel toLevel) {
        // modify: resource_world:overworld -> minecraft:the_end (default obsidian platform)
        // feature: https://bugs.mojang.com/browse/MC-252361
        if (getEntityCurrentLevel().dimension() != Level.OVERWORLD) return new BlockPos(100, 50, 0);
        return getTransformedEndSpawnPoint();
    }

    /* This method will NOT be called when a PLAYER jump into overworld's ender-portal-frame */
    @Redirect(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;makeObsidianPlatform(Lnet/minecraft/server/level/ServerLevel;)V"))
    public void $changeDimension(ServerLevel toLevel) {
        // modify: resource_world:overworld -> minecraft:the_end (default obsidian platform)
        if (getEntityCurrentLevel().dimension() != Level.OVERWORLD) {
            ServerLevel.makeObsidianPlatform(toLevel);
            return;
        }
        MultiObsidianPlatformModule.makeObsidianPlatform(toLevel, getTransformedEndSpawnPoint());
    }
}
