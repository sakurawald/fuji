package io.github.sakurawald.module.mixin.resource_world;

import io.github.sakurawald.module.initializer.resource_world.SafeIterator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;
import java.util.Iterator;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    /* After issue /rw reset, then it's possible that all the worlds will be ticked 2 times.
       and do it again it's 3 times...
     */
    @Redirect(method = "tickWorlds", at = @At(value = "INVOKE", target = "Ljava/lang/Iterable;iterator()Ljava/util/Iterator;", ordinal = 0), require = 0)
    private Iterator<ServerWorld> fuji$copyBeforeTicking(Iterable<ServerWorld> instance) {
        return new SafeIterator<>((Collection<ServerWorld>) instance);
    }
}
