package io.github.sakurawald.auxiliary.minecraft;

import lombok.Setter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class ServerHelper {

    @Setter
    private static MinecraftServer server;

    public static MinecraftServer getDefaultServer() {
        return server;
    }

    public static List<ServerPlayerEntity> getPlayers() {
        return getDefaultServer().getPlayerManager().getPlayerList();
    }
}
