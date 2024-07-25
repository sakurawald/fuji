package io.github.sakurawald.module.initializer.rtp;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
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

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

public class RtpInitializer extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("rtp").executes(this::$rtp));
    }

    private int $rtp(CommandContext<ServerCommandSource> ctx) {
        return CommandHelper.playerOnlyCommand(ctx, player -> {
            ServerWorld serverWorld = player.getServerWorld();

            Optional<TeleportSetup> first = TeleportSetup.of(serverWorld);
            if (first.isEmpty()) {
                MessageHelper.sendMessage(player, "rtp.dimension.disallow", IdentifierHelper.ofString(serverWorld));
                return CommandHelper.Return.FAIL;
            }

            RandomTeleport.request(player, first.get(), (position -> {
                MessageHelper.sendMessage(player, "rtp.success");
            }));

            return CommandHelper.Return.SUCCESS;
        });
    }
}
