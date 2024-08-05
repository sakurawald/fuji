package io.github.sakurawald.module.initializer.command_toolbox.compass;

import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.command.argument.wrapper.Dimension;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.Optional;
import java.util.function.Function;

@Command("compass")
public class CompassInitializer extends ModuleInitializer {

    int acceptCompassItem(ServerPlayerEntity source, Function<ItemStack, Integer> function) {
        ItemStack itemStack = source.getMainHandStack();
        if (!itemStack.getItem().equals(Items.COMPASS)) {
            MessageHelper.sendMessage(source, "compass.no_compass");
            return CommandHelper.Return.FAIL;
        }

        return function.apply(itemStack);
    }

    void setTrackedTarget(ItemStack itemStack, ServerWorld world, BlockPos blockPos) {
        LodestoneTrackerComponent component = new LodestoneTrackerComponent(Optional.of(GlobalPos.create(world.getRegistryKey(), blockPos)), false);
        itemStack.set(DataComponentTypes.LODESTONE_TRACKER, component);
    }

    @Command("track pos")
    int track(@CommandSource ServerPlayerEntity player, Dimension dimension, BlockPos blockPos) {
        return acceptCompassItem(player,(itemStack) -> {
           this.setTrackedTarget(itemStack, dimension.getWorld(), blockPos);
           return CommandHelper.Return.SUCCESS;
        });
    }

    @Command("track player")
    int track(@CommandSource ServerPlayerEntity player, ServerPlayerEntity target) {
        return acceptCompassItem(player,(itemStack) -> {
            this.setTrackedTarget(itemStack,target.getServerWorld(),target.getBlockPos());
            return CommandHelper.Return.SUCCESS;
        });
    }

    @Command("reset")
    int reset(@CommandSource ServerPlayerEntity player) {
        return acceptCompassItem(player,(itemStack) -> {
            itemStack.set(DataComponentTypes.LODESTONE_TRACKER, null);
            return CommandHelper.Return.SUCCESS;
        });
    }
}
