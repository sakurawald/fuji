package fun.sakurawald.mixin.works;

import carpet.utils.WoolTool;
import fun.sakurawald.module.works.WorkHopper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// the priority of carpet's is 1000
@Mixin(value = HopperBlockEntity.class, priority = 999)
public abstract class HopperBlockEntityMixin {

    @Inject(method = "ejectItems", at = @At(value = "HEAD"), cancellable = true)
    private static void onInsert(Level world, BlockPos blockPos, BlockState blockState, Container inventory, CallbackInfoReturnable<Boolean> cir) {

        // If this hopper is a works hopper, then we exclude it from carpet hoppers
        WorkHopper workCounter = WorkHopper.workHopper.get(blockPos);
        if (workCounter != null) {
            DyeColor woolColor = WoolTool.getWoolColorAtPosition(world, blockPos.relative(blockState.getValue(HopperBlock.FACING)));
            if (woolColor != null) {
                for (int i = 0; i < inventory.getContainerSize(); ++i) {
                    if (!inventory.getItem(i).isEmpty()) {
                        ItemStack itemstack = inventory.getItem(i);//.copy();
                        workCounter.addItemStack(itemstack);
                        inventory.setItem(i, ItemStack.EMPTY);
                    }
                }
                cir.setReturnValue(true);
            }
        }
    }
}
