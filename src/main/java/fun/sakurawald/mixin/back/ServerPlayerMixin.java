package fun.sakurawald.mixin.back;

import fun.sakurawald.module.ModuleManager;
import fun.sakurawald.module.back.BackModule;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
@Slf4j
public abstract class ServerPlayerMixin {

    @Unique
    private static final BackModule module = ModuleManager.getOrNewInstance(BackModule.class);

    @Inject(method = "die", at = @At("HEAD"))
    public void die(DamageSource damageSource, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        module.updatePlayer(player);
    }

}
