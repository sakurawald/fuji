package fun.sakurawald.mixin.command_cooldown;

import com.mojang.brigadier.ParseResults;
import fun.sakurawald.module.command_cooldown.CommandCooldownModule;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Commands.class)
public class CommandManagerMixin {
    // If you issue "///abcdefg", then commandLine = "//abcdefg"
    @Inject(method = "performCommand", at = @At("HEAD"), cancellable = true)
    public void $performCommand(ParseResults<CommandSourceStack> parseResults, String commandLine, CallbackInfoReturnable<Integer> cir) {
        ServerPlayer player = parseResults.getContext().getSource().getPlayer();
        if (player == null) return;

        long cooldown = CommandCooldownModule.calculateCommandCooldown(player, commandLine);
        if (cooldown > 0) {
            MessageUtil.message(player, Component.literal("wait %d s".formatted(cooldown / 1000)).withStyle(ChatFormatting.YELLOW), true);
            cir.setReturnValue(0);
        }
    }
}
