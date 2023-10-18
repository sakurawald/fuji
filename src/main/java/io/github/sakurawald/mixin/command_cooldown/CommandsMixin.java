package io.github.sakurawald.mixin.command_cooldown;

import com.mojang.brigadier.ParseResults;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.command_cooldown.CommandCooldownModule;
import io.github.sakurawald.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Commands.class)
@Slf4j
public class CommandsMixin {

    @Unique
    private static final CommandCooldownModule module = ModuleManager.getOrNewInstance(CommandCooldownModule.class);

    // If you issue "///abcdefg", then commandLine = "//abcdefg"
    @Inject(method = "performCommand", at = @At("HEAD"), cancellable = true)
    public void $performCommand(ParseResults<CommandSourceStack> parseResults, String commandLine, CallbackInfoReturnable<Integer> cir) {
        ServerPlayer player = parseResults.getContext().getSource().getPlayer();
        if (player == null) return;

        long cooldown = module.calculateCommandCooldown(player, commandLine);
        if (cooldown > 0) {
            MessageUtil.sendActionBar(player, "command_cooldown.cooldown", cooldown / 1000);
            cir.setReturnValue(0);
        }
    }
}
