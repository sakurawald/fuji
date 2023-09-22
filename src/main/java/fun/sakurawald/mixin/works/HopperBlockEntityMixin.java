package fun.sakurawald.mixin.works;

import carpet.utils.WoolTool;
import fun.sakurawald.module.works.BlockPosCache;
import fun.sakurawald.module.works.work_type.ProductionWork;
import fun.sakurawald.module.works.work_type.Work;
import lombok.extern.slf4j.Slf4j;
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

import java.util.HashSet;

// the priority of carpet's is 1000
@Mixin(value = HopperBlockEntity.class, priority = 999)
@Slf4j
public abstract class HopperBlockEntityMixin {

    @Inject(method = "ejectItems", at = @At(value = "HEAD"), cancellable = true)
    private static void onInsert(Level world, BlockPos blockPos, BlockState blockState, Container inventory, CallbackInfoReturnable<Boolean> cir) {
        // If this hopper is a works hopper, then we exclude it from carpet hoppers
        HashSet<Work> works = BlockPosCache.getBlockpos2works().get(blockPos);
        if (works == null) return;

        DyeColor woolColor = WoolTool.getWoolColorAtPosition(world, blockPos.relative(blockState.getValue(HopperBlock.FACING)));
        if (woolColor != null) {
            for (int i = 0; i < inventory.getContainerSize(); ++i) {
                if (!inventory.getItem(i).isEmpty()) {
                    ItemStack itemstack = inventory.getItem(i);//.copy();
                    // count this itemstack for all works that contains this blockpos
                    works.forEach(work -> {
                        if (work instanceof ProductionWork pwork) {
                            pwork.addCounter(itemstack);
                        }
                    });
                    inventory.setItem(i, ItemStack.EMPTY);
                }
            }
            cir.setReturnValue(true);
        }

    }
}
