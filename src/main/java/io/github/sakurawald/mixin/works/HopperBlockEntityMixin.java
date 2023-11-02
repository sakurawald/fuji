package io.github.sakurawald.mixin.works;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.works.WorksCache;
import io.github.sakurawald.module.works.work_type.ProductionWork;
import io.github.sakurawald.module.works.work_type.Work;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashSet;

// the priority of carpet's is 1000
@Mixin(value = HopperBlockEntity.class, priority = 999)

public abstract class HopperBlockEntityMixin extends RandomizableContainerBlockEntity {

    protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "tryMoveInItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", shift = At.Shift.AFTER))
    private static void $ifHopperHasEmptySlot(Container container, Container container2, ItemStack itemStack, int i, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        count(container, container2, itemStack, direction, cir);
    }


    @Inject(method = "tryMoveInItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void $ifHopperHasMergableSlot(Container container, Container container2, ItemStack itemStack, int i, Direction direction, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack2, boolean bl, boolean bl2, int j, int k) {
        // Note: here we must copy the itemstack before ItemStack#shrink.
        // If the count of itemStack is shark to 0, then it may become AIR, and then we can't count it any more.
        ItemStack copy = itemStack.copy();
        copy.setCount(k);

        count(container, container2, copy, direction, cir);
    }

    @SuppressWarnings("unused")
    @Unique
    private static void count(Container container, Container container2, ItemStack itemStack, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        // note: if the container == null, then means it's the source-hopper
        if (container != null) return;
        if (itemStack.isEmpty()) return;

        // If this hopper is a work's hopper, then we exclude it from carpet hoppers
        HashSet<Work> works;
        if (container2 instanceof HopperBlockEntity hb) {
            works = WorksCache.getBlockpos2works().get(hb.getBlockPos());
        } else if (container2 instanceof MinecartHopper mh) {
            works = WorksCache.getEntity2works().get(mh.getId());
        } else {
            Fuji.log.warn("addItem() found an unknown container: {}", container2);
            return;
        }
        if (works == null) return;
        // count this itemstack for all works that contain this blockpos
        works.forEach(work -> {
            if (work instanceof ProductionWork pwork) {
                pwork.addCounter(itemStack);
            }
        });
    }

}
