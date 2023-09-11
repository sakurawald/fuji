package fun.sakurawald.mixin.custom_stats;

import fun.sakurawald.module.custom_stats.CustomStatisticsModule;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "playerDestroy", at = @At("HEAD"))
    private void $playerDestroy(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity, ItemStack stack, CallbackInfo ci) {
        player.awardStat(CustomStatisticsModule.MINE_ALL);
    }

    @Inject(method = "setPlacedBy", at = @At("HEAD"))
    public void $setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        if (!(placer instanceof ServerPlayer player)) return;
        player.awardStat(CustomStatisticsModule.PLACED_ALL);
    }
}
