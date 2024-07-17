package io.github.sakurawald.module.mixin.command_interactive;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.common.structure.SpecializedCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(AbstractSignBlock.class)
public class SignBlockMixin {

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void $onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> cir) {
        // bypass if player is sneaking
        if (player.isSneaking()) return;

        // interact with sign
        if (player instanceof ServerPlayerEntity serverPlayer) {
            BlockEntity blockEntity = world.getBlockEntity(blockPos);
            if (blockEntity instanceof SignBlockEntity signBlockEntity) {
                SignText signText = signBlockEntity.getText(signBlockEntity.isPlayerFacingFront(player));

                String text = reduce(signText);
                if (text.contains("/")) {
                    cir.setReturnValue(ActionResult.CONSUME);
                    List<String> commands = splitCommands(text);

                    Fuji.LOGGER.info("Player {} execute commands: {}", serverPlayer.getName().getString(), commands);
                    SpecializedCommand.executeCommands(serverPlayer, commands);
                }
            }
        }
    }

    @Unique
    public String reduce(SignText signText) {
        return Arrays.stream(signText.getMessages(false)).map(Text::getString).reduce("", String::concat);
    }

    @Unique
    /* text must contains "//" */
    public List<String> splitCommands(String text) {
        int left = text.indexOf("/");

        // strip comments
        text = text.substring(left + 1);

        // split commands
        String[] split = text.split("/");
        return Arrays.stream(split).map(String::trim).collect(Collectors.toCollection(ArrayList::new));
    }
}
