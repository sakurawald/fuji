package io.github.sakurawald.module.initializer.rtp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.structure.TeleportSetup;
import io.github.sakurawald.module.common.structure.random_teleport.RandomTeleport;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.IdentifierHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

public class RtpInitializer extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("rtp").executes(this::rtp));
    }

    private int rtp(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.playerOnlyCommand(ctx, player -> {

            ServerWorld serverWorld = player.getServerWorld();
            String dimension = IdentifierHelper.ofString(serverWorld);

            List<TeleportSetup> list = Configs.configHandler.model().modules.rtp.dimension.list;
            Optional<TeleportSetup> first = list.stream().filter(o -> o.getDimension().equals(dimension)).findFirst();
            if (first.isEmpty()) {
                MessageHelper.sendMessage(player,"rtp.dimension.disallow", dimension);
                return CommandHelper.Return.FAIL;
            }

//            RandomTeleport.randomTeleport();


            return CommandHelper.Return.SUCCESS;
        });
    }
}
