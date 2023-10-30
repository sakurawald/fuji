package io.github.sakurawald.mixin.better_info;

import carpet.commands.InfoCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InfoCommand.class)

public class InfoCommandMixin {

    @Inject(method = "infoBlock", at = @At(value = "INVOKE", target = "Lcarpet/commands/InfoCommand;printBlock(Ljava/util/List;Lnet/minecraft/commands/CommandSourceStack;Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private static void blockInfo(CommandSourceStack source, BlockPos pos, String grep, CallbackInfoReturnable<Integer> cir) {
        // is player ?
        ServerPlayer player = source.getPlayer();
        if (player == null) return;

        // is block entity ?
        BlockEntity blockEntity = player.level().getBlockEntity(pos);

        MutableComponent output = Component.empty().append("\n");
        if (blockEntity == null) {
            player.sendMessage(output.append(Component.literal("No block entity found at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ())));
            return;
        }

        // send nbt data
        CompoundTag compoundTag = blockEntity.saveWithFullMetadata();
        player.sendMessage(output.append(Component.translatable("commands.data.block.query", pos.getX(), pos.getY(), pos.getZ(), NbtUtils.toPrettyComponent(compoundTag))));
    }
}
