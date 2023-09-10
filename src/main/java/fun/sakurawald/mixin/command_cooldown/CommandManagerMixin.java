package fun.sakurawald.mixin.command_cooldown;

import com.mojang.brigadier.ParseResults;
import fun.sakurawald.ModMain;
import fun.sakurawald.module.command_cooldown.CommandCooldownModule;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    // If you issue "///abcdefg", then commandLine = "//abcdefg"
    @Inject(method = "execute", at = @At("HEAD"), remap = false, cancellable = true)
    public void execute(ParseResults<ServerCommandSource> parseResults, String commandLine, CallbackInfoReturnable<Integer> cir) {
        ServerPlayerEntity player = parseResults.getContext().getSource().getPlayer();
        if (player == null) return;

        long cooldown = CommandCooldownModule.isCommandCooldown(player, commandLine);
        if (cooldown > 0) {
            MessageUtil.message(player, Text.literal("This command is being cooled down (%d sec)".formatted(cooldown / 1000)).formatted(Formatting.YELLOW), true);
            cir.setReturnValue(0);
        }
    }
}
