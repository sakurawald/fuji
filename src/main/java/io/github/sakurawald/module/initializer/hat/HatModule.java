package io.github.sakurawald.module.initializer.hat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;


public class HatModule extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("hat").executes(this::$hat));
    }

    @SuppressWarnings("SameReturnValue")
    private int $hat(CommandContext<CommandSourceStack> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            ItemStack mainHandItem = player.getMainHandItem();
            ItemStack headSlotItem = player.getItemBySlot(EquipmentSlot.HEAD);

            player.setItemSlot(EquipmentSlot.HEAD, mainHandItem);
            player.setItemInHand(InteractionHand.MAIN_HAND, headSlotItem);
            MessageUtil.sendMessage(player, "hat.success");
            return Command.SINGLE_SUCCESS;
        });
    }

}
