package fun.sakurawald.module.back;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.teleport_warmup.Position;
import fun.sakurawald.util.MessageUtil;
import lombok.Getter;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;

public class BackModule {

    @Getter
    private static final HashMap<String, Position> player2lastPos = new HashMap<>();

    public static void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("back").executes(BackModule::$back));
    }

    private static int $back(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 0;

        Position lastPos = player2lastPos.get(player.getName().getString());
        if (lastPos == null) {
            MessageUtil.message(player, "No previous position.", true);
            return Command.SINGLE_SUCCESS;
        }

        player.teleportTo((ServerLevel) lastPos.getLevel(), lastPos.getX(), lastPos.getY(), lastPos.getZ(), lastPos.getYaw(), lastPos.getPitch());
        return Command.SINGLE_SUCCESS;
    }

    public static void updatePlayer(ServerPlayer player) {
        Position lastPos = player2lastPos.get(player.getGameProfile().getName());
        double ignoreDistance = ConfigManager.configWrapper.instance().modules.back.ignore_distance;
        if (lastPos == null
                || (player.level() != lastPos.getLevel())
                || (player.level() == lastPos.getLevel() && player.position().distanceToSqr(lastPos.getX(), lastPos.getY(), lastPos.getZ()) > ignoreDistance * ignoreDistance)
        ) {
            player2lastPos.put(player.getGameProfile().getName(),
                    new Position(player.level(), player.position().x, player.position().y, player.position().z, player.getYRot(), player.getXRot()));
        }
    }
}
