package io.github.sakurawald.module.mixin.command_warmup;

import com.mojang.brigadier.ParseResults;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.command_cooldown.CommandCooldownInitializer;
import io.github.sakurawald.module.initializer.command_warmup.CommandWarmupInitializer;
import io.github.sakurawald.util.MessageUtil;
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
    private static final CommandWarmupInitializer module = ModuleManager.getInitializer(CommandWarmupInitializer.class);

    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    public void $execute(ParseResults<ServerCommandSource> parseResults, String string, CallbackInfo ci) {
        ServerPlayerEntity player = parseResults.getContext().getSource().getPlayer();
        if (player == null) return;

        long cooldown = module.getMs(string);
        if (cooldown > 0) {
            MessageUtil.sendActionBar(player, "command_cooldown.cooldown", cooldown / 1000);

            ci.cancel();
        }
    }
}
