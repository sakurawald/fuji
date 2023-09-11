package fun.sakurawald.mixin.pvp_toggle;

import com.mojang.authlib.GameProfile;
import fun.sakurawald.module.pvp_toggle.PvpWhitelist;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public abstract class PvpToggleMixin extends Player {

    public PvpToggleMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow
    public abstract void readAdditionalSaveData(CompoundTag nbt);

    @Inject(method = "canHarmPlayer", at = @At("HEAD"), cancellable = true)
    public void $canHarmPlayer(Player sourcePlayer, CallbackInfoReturnable<Boolean> cir) {
        if (this == sourcePlayer) return;

        if (!PvpWhitelist.contains(sourcePlayer.getGameProfile())) {
            MessageUtil.feedback(sourcePlayer.createCommandSourceStack(), "PvP for you is now off!", ChatFormatting.DARK_AQUA);
            cir.setReturnValue(false);
            return;
        }

        if (!PvpWhitelist.contains(this.getGameProfile())) {
            MessageUtil.feedback(sourcePlayer.createCommandSourceStack(), String.format("PvP for %s is now off!", this.getGameProfile().getName()), ChatFormatting.DARK_AQUA);
            cir.setReturnValue(false);
        }

    }
}
