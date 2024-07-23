package io.github.sakurawald.util;

import io.github.sakurawald.Fuji;
import lombok.experimental.UtilityClass;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;

@UtilityClass
public class PacketUtil {

    public static void sendToOtherPlayers(ServerPlayerEntity player, Packet<?> packet) {
        for (ServerPlayerEntity serverPlayerEntity : Fuji.SERVER.getPlayerManager().getPlayerList()) {
            if (serverPlayerEntity == player) continue;
            serverPlayerEntity.networkHandler.sendPacket(packet);
        }
    }
}
