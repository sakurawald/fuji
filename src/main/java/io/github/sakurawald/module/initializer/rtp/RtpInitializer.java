package io.github.sakurawald.module.initializer.rtp;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.IdentifierHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LanguageHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.Dimension;
import io.github.sakurawald.core.service.random_teleport.RandomTeleport;
import io.github.sakurawald.core.structure.TeleportSetup;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public class RtpInitializer extends ModuleInitializer {

    @CommandNode("rtp")
    private int $rtp(@CommandSource ServerPlayerEntity player, Optional<Dimension> dimension) {

        ServerWorld serverWorld = dimension.isPresent() ? dimension.get().getValue() : player.getServerWorld();

        Optional<TeleportSetup> first = TeleportSetup.of(serverWorld);
        if (first.isEmpty()) {
            LanguageHelper.sendMessageByKey(player, "rtp.dimension.disallow", IdentifierHelper.ofString(serverWorld));
            return CommandHelper.Return.FAIL;
        }

        LanguageHelper.sendActionBarByKey(player, "rtp.tip");
        RandomTeleport.request(player, first.get(), (position -> LanguageHelper.sendMessageByKey(player, "rtp.success")));
        return CommandHelper.Return.SUCCESS;
    }
}
