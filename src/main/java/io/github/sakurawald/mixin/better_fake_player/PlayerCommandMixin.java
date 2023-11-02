package io.github.sakurawald.mixin.better_fake_player;

import carpet.commands.PlayerCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.better_fake_player.BetterFakePlayerModule;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DataFlowIssue")
@Mixin(PlayerCommand.class)
public abstract class PlayerCommandMixin {

    @Unique
    private static final BetterFakePlayerModule module = ModuleManager.getOrNewInstance(BetterFakePlayerModule.class);

    @Unique
    private static String transformFakePlayerName(String fakePlayerName) {
        return ConfigManager.configWrapper.instance().modules.better_fake_player.transform_name.replace("%name%", fakePlayerName);
    }

    @Redirect(method = "cantSpawn", at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/brigadier/arguments/StringArgumentType;getString(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Ljava/lang/String;"
    ), remap = false)
    private static String $canSpawn(final CommandContext<?> context, final String name) {
        return transformFakePlayerName(StringArgumentType.getString(context, name));
    }

    @Redirect(method = "spawn", at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/brigadier/arguments/StringArgumentType;getString(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Ljava/lang/String;"
    ), remap = false)
    private static String $spawn(final CommandContext<?> context, final String name) {
        return transformFakePlayerName(StringArgumentType.getString(context, name));
    }

    @Inject(method = "spawn", at = @At("HEAD"), remap = false, cancellable = true)
    private static void $spawn_head(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) return;

        if (!module.canSpawnFakePlayer(player)) {
            MessageUtil.sendMessage(player, "better_fake_player.spawn.limit_exceed");
            cir.setReturnValue(0);
        }

        /* fix: fake-player auth network laggy */
        if (ConfigManager.configWrapper.instance().modules.better_fake_player.use_local_random_skins_for_fake_player) {
            String fakePlayerName = StringArgumentType.getString(context, "player");
            fakePlayerName = transformFakePlayerName(fakePlayerName);
            Fuji.SERVER.getProfileCache().add(module.createOfflineGameProfile(fakePlayerName));
        }
    }

    @Inject(method = "spawn", at = @At("TAIL"), remap = false)
    private static void $spawn_tail(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir) {
        ServerPlayer player = context.getSource().getPlayer();
        String fakePlayerName = StringArgumentType.getString(context, "player");
        fakePlayerName = transformFakePlayerName(fakePlayerName);
        module.addFakePlayer(player, fakePlayerName);
        module.renewFakePlayers(player);
    }

    @Inject(method = "cantManipulate", at = @At("HEAD"), remap = false, cancellable = true)
    private static void $cantManipulate(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Boolean> cir) {
        String fakePlayerName = StringArgumentType.getString(context, "player");
        if (!module.canManipulateFakePlayer(context, fakePlayerName)) {
            MessageUtil.sendMessage(context.getSource(), "better_fake_player.manipulate.forbidden");
            cir.setReturnValue(true);
        }
    }
}
