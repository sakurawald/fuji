package io.github.sakurawald.module.initializer.hat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.CommandUtil;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Hand;


public class HatInitializer extends ModuleInitializer {


    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("hat").executes(this::$hat));
    }

    @SuppressWarnings("SameReturnValue")
    private int $hat(CommandContext<ServerCommandSource> ctx) {
        return CommandUtil.playerOnlyCommand(ctx, player -> {
            ItemStack mainHandItem = player.getMainHandStack();
            ItemStack headSlotItem = player.getEquippedStack(EquipmentSlot.HEAD);

            player.equipStack(EquipmentSlot.HEAD, mainHandItem);
            player.setStackInHand(Hand.MAIN_HAND, headSlotItem);
            MessageUtil.sendMessage(player, "hat.success");
            return Command.SINGLE_SUCCESS;
        });
    }

}
