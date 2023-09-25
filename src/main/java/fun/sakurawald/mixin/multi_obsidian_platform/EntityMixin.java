package fun.sakurawald.mixin.multi_obsidian_platform;

import fun.sakurawald.module.multi_obsidian_platform.MultiObsidianPlatformModule;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Unique
    BlockPos getTransformedEndSpawnPoint() {
        Entity entity = (Entity) (Object) this;
        return MultiObsidianPlatformModule.transform(entity.blockPosition());
    }

    @Unique
    Level getServerLevel() {
        Entity entity = (Entity) (Object) this;
        return entity.level();
    }

    @Redirect(method = "findDimensionEntryPoint", at = @At(value = "FIELD", target = "Lnet/minecraft/server/level/ServerLevel;END_SPAWN_POINT:Lnet/minecraft/core/BlockPos;"))
    BlockPos $findDimensionEntryPoint() {
        if (getServerLevel().dimension() != Level.OVERWORLD) return ServerLevel.END_SPAWN_POINT;
        return getTransformedEndSpawnPoint();
    }

    @Redirect(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;makeObsidianPlatform(Lnet/minecraft/server/level/ServerLevel;)V"))
    public void $changeDimension(ServerLevel serverLevel) {
        if (serverLevel.dimension() != Level.OVERWORLD) return;
        MultiObsidianPlatformModule.makeObsidianPlatform(serverLevel, getTransformedEndSpawnPoint());
    }
}
