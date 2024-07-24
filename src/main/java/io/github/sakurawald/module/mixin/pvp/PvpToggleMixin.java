package io.github.sakurawald.module.mixin.pvp;

import com.mojang.authlib.GameProfile;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.pvp.PvpInitializer;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class PvpToggleMixin extends PlayerEntity {
    @Unique
    private static final PvpInitializer module = ModuleManager.getInitializer(PvpInitializer.class);

    public PvpToggleMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "shouldDamagePlayer", at = @At("HEAD"), cancellable = true)
    public void $shouldDamagePlayer(PlayerEntity sourcePlayer, CallbackInfoReturnable<Boolean> cir) {
        if (this == sourcePlayer) return;

        ServerPlayerEntity player = sourcePlayer.getCommandSource().getPlayer();
        if (player == null) return;

        if (!module.contains(sourcePlayer.getGameProfile().getName())) {
            MessageHelper.sendMessage(player, "pvp.check.off.me");
            cir.setReturnValue(false);
            return;
        }

        if (!module.contains(this.getGameProfile().getName())) {
            MessageHelper.sendMessage(player, "pvp.check.off.others", this.getGameProfile().getName());
            cir.setReturnValue(false);
        }
    }
}
