package io.github.sakurawald.module.mixin.fake_player_manager;

import carpet.commands.PlayerCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.fake_player_manager.FakePlayerManagerModule;
import io.github.sakurawald.util.MessageUtil;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
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
    private static final FakePlayerManagerModule module = ModuleManager.getInitializer(FakePlayerManagerModule.class);

    @Unique
    private static String transformFakePlayerName(String fakePlayerName) {
        return Configs.configHandler.model().modules.fake_player_manager.transform_name.replace("%name%", fakePlayerName);
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
    private static void $spawn_head(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Integer> cir) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return;

        if (!module.canSpawnFakePlayer(player)) {
            MessageUtil.sendMessage(player, "fake_player_manager.spawn.limit_exceed");
            cir.setReturnValue(0);
        }

        /* fix: fake-player auth network laggy */
        if (Configs.configHandler.model().modules.fake_player_manager.use_local_random_skins_for_fake_player) {
            String fakePlayerName = StringArgumentType.getString(context, "player");
            fakePlayerName = transformFakePlayerName(fakePlayerName);
            Fuji.SERVER.getUserCache().add(module.createOfflineGameProfile(fakePlayerName));
        }
    }

    @Inject(method = "spawn", at = @At("TAIL"), remap = false)
    private static void $spawn_tail(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Integer> cir) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        String fakePlayerName = StringArgumentType.getString(context, "player");
        fakePlayerName = transformFakePlayerName(fakePlayerName);
        module.addFakePlayer(player, fakePlayerName);
        module.renewFakePlayers(player);
    }

    @Inject(method = "cantManipulate", at = @At("HEAD"), remap = false, cancellable = true)
    private static void $cantManipulate(CommandContext<ServerCommandSource> context, CallbackInfoReturnable<Boolean> cir) {
        String fakePlayerName = StringArgumentType.getString(context, "player");
        if (!module.canManipulateFakePlayer(context, fakePlayerName)) {
            MessageUtil.sendMessage(context.getSource(), "fake_player_manager.manipulate.forbidden");
            cir.setReturnValue(true);
        }
    }
}
