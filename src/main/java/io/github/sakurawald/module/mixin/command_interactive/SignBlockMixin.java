package io.github.sakurawald.module.mixin.command_interactive;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.initializer.scheduler.SpecializedCommand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(SignBlock.class)

public class SignBlockMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void $use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir) {
        // bypass if player is sneaking
        if (player.isShiftKeyDown()) return;

        // interact with sign
        if (player instanceof ServerPlayer serverPlayer) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof SignBlockEntity signBlockEntity) {
                SignText signText = signBlockEntity.getText(signBlockEntity.isFacingFrontText(player));
                String text = combineLines(signText).replace("@u", serverPlayer.getGameProfile().getName());
                if (text.contains("//")) {
                    cir.setReturnValue(InteractionResult.CONSUME);
                    List<String> commands = resolveCommands(text);
                    if (ConfigManager.configWrapper.instance().modules.command_interactive.log_use) {
                        Fuji.log.info("Player {} execute commands: {}", serverPlayer.getName().getString(), commands);
                    }
                    SpecializedCommand.executeCommands(serverPlayer, commands);
                }
            }
        }
    }

    @Unique
    public String combineLines(SignText signText) {
        return Arrays.stream(signText.getMessages(false)).map(Component::getString).reduce("", String::concat);
    }

    @Unique
    /* text must contains "//" */
    public List<String> resolveCommands(String text) {
        int left = text.indexOf("//");
        // strip comments
        text = text.substring(left + 2);

        // split commands
        String[] split = text.split("//");
        return Arrays.stream(split).map(String::trim).collect(Collectors.toCollection(ArrayList::new));
    }
}
