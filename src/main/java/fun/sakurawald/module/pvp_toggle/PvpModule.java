package fun.sakurawald.module.pvp_toggle;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import fun.sakurawald.util.MessageUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Objects;

public class PvpModule {
    public static LiteralCommandNode<CommandSourceStack> registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        return dispatcher.register(
                Commands.literal("pvp")
                        .then(
                                Commands.literal("on")
                                        .executes(PvpModule::$on)
                        )
                        .then(
                                Commands.literal("off")
                                        .executes(PvpModule::$off)
                        )
                        .then(
                                Commands.literal("list")
                                        .executes(PvpModule::$list)
                        )
                        .then(
                                Commands.literal("status")
                                        .executes(PvpModule::$status)
                        )
        );
    }

    private static int $on(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        GameProfile player = Objects.requireNonNull(source.getPlayer()).getGameProfile();

        if (!PvpWhitelist.contains(player)) {
            PvpWhitelist.addPlayer(player);

            MessageUtil.feedback(source, "PvP for you is now on.", ChatFormatting.DARK_AQUA);
            return 1;
        }

        MessageUtil.feedback(source, "You already have PvP on!", ChatFormatting.DARK_AQUA);
        return 0;
    }

    private static int $off(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        GameProfile player = Objects.requireNonNull(source.getPlayer()).getGameProfile();

        if (PvpWhitelist.contains(player)) {
            PvpWhitelist.removePlayer(player);
            MessageUtil.feedback(source, "PvP for you is now off.", ChatFormatting.DARK_AQUA);
            return 1;
        }

        MessageUtil.feedback(source, "You already have PvP off!", ChatFormatting.DARK_AQUA);
        return 0;
    }


    private static int $status(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        GameProfile player = Objects.requireNonNull(source.getPlayer()).getGameProfile();
        MessageUtil.feedback(source, "PvP for you is " + (PvpWhitelist.contains(player) ? "on" : "off"), ChatFormatting.DARK_AQUA);
        return 1;
    }

    private static int $list(CommandContext<CommandSourceStack> ctx) {
        MessageUtil.feedback(ctx.getSource(), "Players with PvP on: " + String.join(", ", PvpWhitelist.getPlayers()), ChatFormatting.DARK_AQUA);
        return 1;
    }
}
