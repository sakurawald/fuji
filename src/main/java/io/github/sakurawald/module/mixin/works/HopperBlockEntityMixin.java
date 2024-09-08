package io.github.sakurawald.module.mixin.works;

import io.github.sakurawald.module.initializer.works.structure.WorksCache;
import io.github.sakurawald.module.initializer.works.structure.work.impl.ProductionWork;
import io.github.sakurawald.module.initializer.works.structure.work.abst.Work;
import io.github.sakurawald.core.auxiliary.LogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

// the priority of carpet's is 1000
@Mixin(value = HopperBlockEntity.class, priority = 999)

public abstract class HopperBlockEntityMixin extends LootableContainerBlockEntity {

    protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType, @NotNull BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/Inventory;setStack(ILnet/minecraft/item/ItemStack;)V", shift = At.Shift.AFTER))
    private static void $ifHopperHasEmptySlot(Inventory container, Inventory container2, @NotNull ItemStack itemStack, int i, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        count(container, container2, itemStack, direction, cir);
    }


    @Inject(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void $ifHopperHasMergableSlot(Inventory container, Inventory container2, @NotNull ItemStack itemStack, int i, Direction direction, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack2, boolean bl, boolean bl2, int j, int k) {
        // Note: here we must copy the itemstack before ItemStack#shrink.
        // If the count of itemStack is shark to 0, then it may become AIR, and then we can't count it any more.
        ItemStack copy = itemStack.copy();
        copy.setCount(k);

        count(container, container2, copy, direction, cir);
    }

    @Unique
    private static void count(@Nullable Inventory container, Inventory container2, @NotNull ItemStack itemStack, Direction direction, CallbackInfoReturnable<ItemStack> cir) {
        // note: if the container == null, then means it's the source-hopper
        if (container != null) return;
        if (itemStack.isEmpty()) return;

        // If this hopper is a work's hopper, then we exclude it from carpet hoppers
        Set<Work> works;
        if (container2 instanceof HopperBlockEntity hb) {
            works = WorksCache.getBlockpos2works().get(hb.getPos());
        } else if (container2 instanceof HopperMinecartEntity mh) {
            works = WorksCache.getEntity2works().get(mh.getId());
        } else {
            LogUtil.warn("addItem() found an unknown container: {}", container2);
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
