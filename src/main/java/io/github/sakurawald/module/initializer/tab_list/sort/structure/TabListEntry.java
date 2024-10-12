package io.github.sakurawald.module.initializer.tab_list.sort.structure;

import com.mojang.authlib.GameProfile;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@AllArgsConstructor
public class TabListEntry {

    private static final String META_SEPARATOR = "@";

    @Getter
    private static final Set<TabListEntry> instances = new HashSet<>();

    @Getter
    ServerPlayerEntity realPlayer;

    @Getter
    ServerPlayerEntity dummyPlayer;

    public static boolean isDummyPlayer(ServerPlayerEntity player) {
        return player.getGameProfile().getName().contains(META_SEPARATOR);
    }

    public static Collection<ServerPlayerEntity> getDummyPlayerList() {
        return instances.stream().map(TabListEntry::getDummyPlayer).toList();
    }

    private static @NotNull String encodeName(@NotNull ServerPlayerEntity player) {
        int weight = getWeight(player);
        // reverse weight
        int maxIndex = AlphaTable.TABLE.length - 1;
        int index = maxIndex - weight;
        index = Math.min(maxIndex, index);

        // the "zzzz" string is used to force the dummy player listed in the last of `command suggestion`
        String prefix = "zzzz" + AlphaTable.TABLE[index];
        String playerName = player.getGameProfile().getName();
        String hashedName = UUID.nameUUIDFromBytes(playerName.getBytes()).toString().substring(0, 8);
        return prefix + META_SEPARATOR + hashedName;
    }

    public static @NotNull PlayerListS2CPacket makePacket(@NotNull Collection<ServerPlayerEntity> collection) {
        EnumSet<PlayerListS2CPacket.Action> enumSet = EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER, PlayerListS2CPacket.Action.UPDATE_GAME_MODE, PlayerListS2CPacket.Action.UPDATE_LISTED, PlayerListS2CPacket.Action.UPDATE_LATENCY, PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);
        return new PlayerListS2CPacket(enumSet, collection);
    }

    public static Optional<TabListEntry> getEntryFromDummyPlayer(ServerPlayerEntity dummyPlayer) {
        for (TabListEntry next : instances) {
            if (next.getDummyPlayer().equals(dummyPlayer)) return Optional.of(next);
        }

        // send packet to remove it
        ServerHelper.getDefaultServer().getPlayerManager().sendToAll(new PlayerRemoveS2CPacket(List.of(dummyPlayer.getUuid())));
        return Optional.empty();
    }

    public static @NotNull TabListEntry getEntryFromRealPlayer(ServerPlayerEntity realPlayer) {
        Optional<TabListEntry> first = instances.stream().filter(p -> p.getRealPlayer().equals(realPlayer)).findFirst();
        if (first.isEmpty()) {
            // make player entity
            String dummyPlayerName = encodeName(realPlayer);
            ServerPlayerEntity dummyPlayer = makeServerPlayerEntity(dummyPlayerName);

            // save
            TabListEntry tabListEntry = new TabListEntry(realPlayer, dummyPlayer);
            instances.add(tabListEntry);

            return tabListEntry;
        }

        return first.get();
    }

    private static @NotNull ServerPlayerEntity makeServerPlayerEntity(@NotNull String playerName) {
        MinecraftServer server = ServerHelper.getDefaultServer();
        ServerWorld world = server.getWorlds().iterator().next();
        GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes(playerName.getBytes()), playerName);
        SyncedClientOptions syncedClientOptions = SyncedClientOptions.createDefault();
        ServerPlayerEntity player = new ServerPlayerEntity(server, world, gameProfile, syncedClientOptions);
        ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
        ConnectedClientData connectedClientData = ConnectedClientData.createDefault(gameProfile, false);
        player.networkHandler = new ServerPlayNetworkHandler(server, clientConnection, player, connectedClientData);
        return player;
    }

    private static @NotNull Integer getWeight(@NotNull ServerPlayerEntity player) {
        Optional<Integer> weight = PermissionHelper.getMeta(player.getUuid(), "fuji.tab_list.sort.weight", Integer::valueOf);
        return weight.orElse(0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TabListEntry that = (TabListEntry) o;
        return this.getRealPlayer().getGameProfile().getName().equals(that.realPlayer.getGameProfile().getName());
    }

    @Override
    public int hashCode() {
        return realPlayer.hashCode();
    }

    @Override
    public String toString() {
        return "[player = %s, dummyPlayer = %s]".formatted(realPlayer.getGameProfile().getName(), dummyPlayer.getGameProfile().getName());
    }

}
