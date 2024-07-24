package io.github.sakurawald.util.minecraft;

import lombok.Setter;
import net.minecraft.server.MinecraftServer;

public class ServerHelper {

    @Setter
    private static MinecraftServer server;

    public static MinecraftServer getDefaultServer() {
        return server;
    }
}
