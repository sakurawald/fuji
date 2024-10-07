package io.github.sakurawald.core.auxiliary.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import lombok.Setter;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.UserCache;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ServerHelper {

    @Setter
    private static MinecraftServer server;

    public static MinecraftServer getDefaultServer() {
        return server;
    }

    public static Collection<ServerWorld> getWorlds() {
        return getDefaultServer().worlds.values();
    }

    public static @Nullable CommandDispatcher<ServerCommandSource> getCommandDispatcher() {
        if (getDefaultServer() == null || getDefaultServer().getCommandManager() == null) {
            return null;
        }

        return getDefaultServer().getCommandManager().getDispatcher();
    }

    public static Optional<GameProfile> getGameProfileByName(String playerName) {
        UserCache userCache = getDefaultServer().getUserCache();
        if (userCache == null) return Optional.empty();

        return userCache.findByName(playerName);
    }

    public static PlayerManager getPlayerManager() {
        return getDefaultServer().getPlayerManager();
    }

    public static List<ServerPlayerEntity> getPlayers() {
        return getPlayerManager().getPlayerList();
    }

    public static @Nullable ServerPlayerEntity getPlayer(String name) {
        return getPlayerManager().getPlayer(name);
    }

    public static boolean isPlayerOnline(String name) {
        return getPlayers().stream().anyMatch(p -> p.getGameProfile().getName().equals(name));
    }

    public static void sendPacketToAll(Packet<?> packet) {
        getPlayerManager().sendToAll(packet);
    }

    @SuppressWarnings("unused")
    public static void sendPacketToAllExcept(Packet<?> packet, ServerPlayerEntity player) {
        getPlayerManager().getPlayerList().stream().filter(it -> it != player).forEach(p -> p.networkHandler.sendPacket(packet));
    }

    @SuppressWarnings("unused")
    public static void sendPacket(Packet<?> packet, ServerPlayerEntity player) {
        player.networkHandler.sendPacket(packet);
    }
}
