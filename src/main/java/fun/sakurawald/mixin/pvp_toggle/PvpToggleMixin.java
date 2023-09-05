package fun.sakurawald.mixin.pvp_toggle;

import com.mojang.authlib.GameProfile;
import fun.sakurawald.module.pvp_toggle.PvpModule;
import fun.sakurawald.module.pvp_toggle.PvpWhitelist;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class PvpToggleMixin extends PlayerEntity {

    public PvpToggleMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow
    public abstract void sendMessage(Text message);

    @Shadow
    public abstract void readCustomDataFromNbt(NbtCompound nbt);

    @Inject(method = "shouldDamagePlayer", at = @At("HEAD"), cancellable = true)
    public void checkWhitelist(PlayerEntity sourcePlayer, CallbackInfoReturnable<Boolean> cir) {
        if (this == sourcePlayer) return;

        if (!PvpWhitelist.contains(sourcePlayer.getGameProfile())) {
            PvpModule.feedback(sourcePlayer.getCommandSource(), "PvP for you is now off!");
            cir.setReturnValue(false);
            return;
        }

        if (!PvpWhitelist.contains(this.getGameProfile())) {
            PvpModule.feedback(sourcePlayer.getCommandSource(), String.format("PvP for %s is now off!", this.getGameProfile().getName()));
            cir.setReturnValue(false);
        }

    }
}
