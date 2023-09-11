package fun.sakurawald.mixin.better_fake_player;

import carpet.commands.PlayerCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.ModMain;
import fun.sakurawald.module.better_fake_player.BetterFakePlayerModule;
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


    @Inject(method = "spawn", at = @At("HEAD"), remap = false, cancellable = true)
    private static void $spawn(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Integer> cir) {
        ServerPlayer player = context.getSource().getPlayer();
        int limit = BetterFakePlayerModule.getCurrentAmountLimit();
        Stream<String> current = Arrays.stream(ModMain.SERVER.getPlayerList().getPlayerNamesArray())
                .filter(sourcePlayerName -> sourcePlayerName.toLowerCase().startsWith(player.getGameProfile().getName().toLowerCase() + "_"));
        if (current.count() >= limit) {
            MessageUtil.message(player, "You have reach the fake-player limit (current is %d).".formatted(limit), false);
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
    private static String $spawn(final CommandContext<CommandSourceStack> context, final String name) {
        return BetterFakePlayerModule.getDecoratedString(context, name);
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
    private static String $cantSpawn(final CommandContext<CommandSourceStack> context, final String name) {
        return BetterFakePlayerModule.getDecoratedString(context, name);
    }

    @Inject(method = "cantManipulate", at = @At("HEAD"), remap = false, cancellable = true)
    private static void $cantManipulate(CommandContext<CommandSourceStack> context, CallbackInfoReturnable<Boolean> cir) {
        ServerPlayer sourcePlayer = context.getSource().getPlayer();
        String targetPlayerName = StringArgumentType.getString(context, "player");
        if (!targetPlayerName.toLowerCase().startsWith(sourcePlayer.getGameProfile().getName().toLowerCase() + "_")) {
            MessageUtil.message(sourcePlayer, "You can't manipulate this player", false);
            cir.setReturnValue(true);
        }
    }
}
