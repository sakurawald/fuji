package io.github.sakurawald.module.initializer.newbie_welcome;

import io.github.sakurawald.module.common.structure.random_teleport.RandomTeleport;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.IdentifierHelper;
import io.github.sakurawald.util.minecraft.MessageHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


public class NewbieWelcomeInitializer extends ModuleInitializer {

    public void welcomeNewbiePlayer(ServerPlayerEntity player) {
        /* welcome message */
        MessageHelper.sendBroadcast("newbie_welcome.welcome_message", player.getGameProfile().getName());

        /* random teleport */
        RandomTeleport.randomTeleport(player, player.getServerWorld(), (position -> {
            Identifier identifier = Identifier.of(position.getLevel());
            player.setSpawnPoint(IdentifierHelper.ofRegistryKey(identifier), position.ofBlockPos(), 0, true, false);
        }));
    }

}
