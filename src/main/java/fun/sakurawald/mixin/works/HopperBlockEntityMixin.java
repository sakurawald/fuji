package fun.sakurawald.mixin.works;

import fun.sakurawald.module.works.WorksCache;
import fun.sakurawald.module.works.work_type.ProductionWork;
import fun.sakurawald.module.works.work_type.Work;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;

// the priority of carpet's is 1000
@Mixin(value = HopperBlockEntity.class, priority = 1001)
@Slf4j
public abstract class HopperBlockEntityMixin extends RandomizableContainerBlockEntity {

    protected HopperBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z", at = @At(value = "RETURN"))
    private static void addItem(Container container, ItemEntity itemEntity, CallbackInfoReturnable<Boolean> cir) {
        Boolean success = cir.getReturnValue();
        // check if this ItemEntity is finally added to the container
        if (!success) return;

        // If this hopper is a works hopper, then we exclude it from carpet hoppers
        HashSet<Work> works;
        if (container instanceof HopperBlockEntity hb) {
            works = WorksCache.getBlockpos2works().get(hb.getBlockPos());
        } else if (container instanceof MinecartHopper mh) {
            works = WorksCache.getEntity2works().get(mh.getId());
        } else {
            log.warn("addItem() found an unknown container: {}", container);
            return;
        }
        if (works == null) return;
        // count this itemstack for all works that contains this blockpos
        works.forEach(work -> {
            if (work instanceof ProductionWork pwork) {
                pwork.addCounter(itemEntity.getItem());
            }
        });
    }

}
