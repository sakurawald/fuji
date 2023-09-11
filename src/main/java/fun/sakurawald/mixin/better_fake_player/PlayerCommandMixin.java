package fun.sakurawald.mixin.better_fake_player;

import carpet.commands.PlayerCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.ModMain;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.stream.Stream;

@SuppressWarnings("DataFlowIssue")
@Mixin(PlayerCommand.class)
public abstract class PlayerCommandMixin {

    @SuppressWarnings("MissingUnique")
    private static String getDecoratedString(final CommandContext<CommandSourceStack> context, final String name) {
        ServerPlayer player = context.getSource().getPlayer();
        String sourcePlayerName = player.getGameProfile().getName();
        String spawnPlayerName = StringArgumentType.getString(context, name);
        return sourcePlayerName + "_" + spawnPlayerName;
    }

    @Inject(method = "spawn", at = @At("HEAD"), remap = false, cancellable = true)
    private static void spawn(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir) {
        ServerPlayer player = context.getSource().getPlayer();
        int limit = ConfigManager.configWrapper.instance().modules.better_fake_player.max_fake_player_limit;
        Stream<String> current = Arrays.stream(ModMain.SERVER.getPlayerList().getPlayerNamesArray())
                .filter(sourcePlayerName -> sourcePlayerName.toLowerCase().startsWith(player.getGameProfile().getName().toLowerCase() + "_"));
        if (current.count() >= limit) {
            MessageUtil.message(player, "You have reach the fake-player limit (%d).".formatted(limit), false);
            cir.setReturnValue(0);
        }
    }

    @Redirect(
            method = "spawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/arguments/StringArgumentType;getString(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Ljava/lang/String;"
            ),
            remap = false
    )
    private static String spawn(final CommandContext<CommandSourceStack> context, final String name) {
        return getDecoratedString(context, name);
    }

    @Redirect(
            method = "cantSpawn",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/arguments/StringArgumentType;getString(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Ljava/lang/String;"
            ),
            require = 1,
            remap = false
    )
    private static String cantSpawn(final CommandContext<CommandSourceStack> context, final String name) {
        return getDecoratedString(context, name);
    }

    @Inject(method = "cantManipulate", at = @At("HEAD"), remap = false, cancellable = true)
    private static void cantManipulate(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer sourcePlayer = context.getSource().getPlayer();
        String targetPlayerName = StringArgumentType.getString(context, "player");
        if (!targetPlayerName.toLowerCase().startsWith(sourcePlayer.getGameProfile().getName().toLowerCase() + "_")) {
            MessageUtil.message(sourcePlayer, "You can't manipulate this player", false);
            cir.setReturnValue(true);
        }
    }
}
