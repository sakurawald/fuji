package io.github.sakurawald.module.initializer.tab_list.sort;

import com.mojang.authlib.GameProfile;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.tab_list.sort.job.UpdateEncodedPlayerTablistNameJob;
import io.github.sakurawald.module.initializer.tab_list.sort.structure.AlphaTable;
import io.github.sakurawald.util.minecraft.PermissionHelper;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

public class TabListSortInitializer extends ModuleInitializer {

    public static final Map<String, String> encoded2name = new HashMap<>();

    public static final String META_SEPARATOR = "@";

    public static @NotNull PlayerListS2CPacket entryFromEncodedPlayer(@NotNull Collection<ServerPlayerEntity> collection) {
        /*
            For a player, we need to remove `Action.UPDATE_LISTED` so avoid the entry to be listed in client-side's tab list.
            For an encoded-player, we need to remove `Action.ADD_PLAYER` to
              1. avoid the player to be listed in client-side's report players menu.
              2. avoid the player to be listed in command suggestion.
         */

        EnumSet<PlayerListS2CPacket.Action> enumSet = EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER, PlayerListS2CPacket.Action.UPDATE_GAME_MODE, PlayerListS2CPacket.Action.UPDATE_LISTED, PlayerListS2CPacket.Action.UPDATE_LATENCY, PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);
        return new PlayerListS2CPacket(enumSet, collection);
    }

    public static @NotNull ServerPlayerEntity makeServerPlayerEntity(@NotNull MinecraftServer server, @NotNull String playerName) {
        ServerWorld world = server.getWorlds().iterator().next();
        GameProfile gameProfile = new GameProfile(UUID.nameUUIDFromBytes(playerName.getBytes()), playerName);
        SyncedClientOptions syncedClientOptions = SyncedClientOptions.createDefault();
        ServerPlayerEntity player = new ServerPlayerEntity(server, world, gameProfile, syncedClientOptions);
        ClientConnection clientConnection = new ClientConnection(NetworkSide.CLIENTBOUND);
        ConnectedClientData connectedClientData = ConnectedClientData.createDefault(gameProfile, false);
        player.networkHandler = new ServerPlayNetworkHandler(server, clientConnection, player, connectedClientData);
        return player;
    }

    @Unique
    public static @NotNull ServerPlayerEntity makeServerPlayerEntity(@NotNull MinecraftServer server, @NotNull ServerPlayerEntity player) {
        String encodedName = encodeName(player);
        ServerPlayerEntity dummyPlayer = makeServerPlayerEntity(server, encodedName);
        dummyPlayer.getGameProfile().getProperties().putAll(player.getGameProfile().getProperties());
        return dummyPlayer;
    }

    public static @NotNull Map<String, Integer> getWeightMap(@NotNull List<ServerPlayerEntity> players) {
        Map<String, Integer> ret = new HashMap<>();
        for (ServerPlayerEntity player : players) {
            String name = player.getGameProfile().getName();
            Integer weight = getWeight(player);
            ret.put(name, weight);
        }
        return ret;
    }

    public static @NotNull Integer getWeight(@NotNull ServerPlayerEntity player) {
        Optional<Integer> weight = PermissionHelper.getMeta(player, "fuji.tab_list.sort.weight", Integer::valueOf);
        return weight.orElse(0);
    }

    @Override
    public void onInitialize() {
        new UpdateEncodedPlayerTablistNameJob(() -> Configs.configHandler.model().modules.tab_list.sort.sync_cron).schedule();
    }

    public static String decodeName(String playerName) {
        return encoded2name.get(playerName);
    }

    public static @NotNull String encodeName(@NotNull ServerPlayerEntity player) {
        int weight = getWeight(player);
        // reverse weight
        int maxIndex = AlphaTable.TABLE.length - 1;
        int index = (maxIndex) - weight;
        index = Math.min(maxIndex, index);

        // the "zzzz" string is used to force the dummy player listed in the last of `command suggestion`
        String prefix = "zzzz" + AlphaTable.TABLE[index];
        String playerName = player.getGameProfile().getName();
        String hashedName = UUID.nameUUIDFromBytes(playerName.getBytes()).toString().substring(0, 8);
        String encoded = prefix + META_SEPARATOR + hashedName;

        encoded2name.put(encoded, playerName);
        return encoded;
    }

    public static void syncEncodedPlayers(@NotNull MinecraftServer server) {
        /* make encoded player list */
        ArrayList<ServerPlayerEntity> encodedPlayers = new ArrayList<>();
        for (String encodedName : TabListSortInitializer.encoded2name.keySet()) {
            encodedPlayers.add(makeServerPlayerEntity(server, encodedName));
        }

        /* update tab list name for encoded players */
        PlayerListS2CPacket playerListS2CPacket = new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME), encodedPlayers);
        server.getPlayerManager().sendToAll(playerListS2CPacket);
    }

}
