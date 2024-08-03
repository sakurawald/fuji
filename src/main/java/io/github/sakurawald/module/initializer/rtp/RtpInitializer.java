package io.github.sakurawald.module.initializer.rtp;

import io.github.sakurawald.command.argument.wrapper.Dimension;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.common.structure.TeleportSetup;
import io.github.sakurawald.module.common.service.random_teleport.RandomTeleport;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import io.github.sakurawald.util.minecraft.IdentifierHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public class RtpInitializer extends ModuleInitializer {

    @Command("rtp")
    private int $rtp(@CommandSource ServerPlayerEntity player, Optional<Dimension> dimension) {

        ServerWorld serverWorld = dimension.isPresent() ? dimension.get().getWorld() : player.getServerWorld();

        Optional<TeleportSetup> first = TeleportSetup.of(serverWorld);
        if (first.isEmpty()) {
            MessageHelper.sendMessage(player, "rtp.dimension.disallow", IdentifierHelper.ofString(serverWorld));
            return CommandHelper.Return.FAIL;
        }

        MessageHelper.sendActionBar(player, "rtp.tip");
        RandomTeleport.request(player, first.get(), (position -> MessageHelper.sendMessage(player, "rtp.success")));
        return CommandHelper.Return.SUCCESS;
    }
}
