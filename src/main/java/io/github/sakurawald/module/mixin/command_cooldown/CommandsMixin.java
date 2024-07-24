package io.github.sakurawald.module.mixin.command_cooldown;

import com.mojang.brigadier.ParseResults;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.command_cooldown.CommandCooldownInitializer;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)

public class CommandsMixin {

    @Unique
    private static final CommandCooldownInitializer module = ModuleManager.getInitializer(CommandCooldownInitializer.class);

    // If you issue "///abcdefg", then commandLine = "//abcdefg"
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    public void $execute(ParseResults<ServerCommandSource> parseResults, String string, CallbackInfo ci) {
        ServerPlayerEntity player = parseResults.getContext().getSource().getPlayer();
        if (player == null) return;

        long cooldown = module.calculateCommandCooldown(player, string);
        if (cooldown > 0) {
            MessageHelper.sendActionBar(player, "command_cooldown.cooldown", cooldown / 1000);
            ci.cancel();
        }
    }
}
