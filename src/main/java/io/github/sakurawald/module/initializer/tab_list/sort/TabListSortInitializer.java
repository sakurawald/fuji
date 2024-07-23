package io.github.sakurawald.module.initializer.tab_list.sort;

import com.mojang.authlib.GameProfile;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.tab_list.structure.AlphaTable;
import io.github.sakurawald.util.PermissionUtil;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;

public class TabListSortInitializer extends ModuleInitializer {


    public static final String META_SEPARATOR = "@";

    public static PlayerListS2CPacket entryFromEncodedPlayer(Collection<ServerPlayerEntity> collection) {
        /*
            For a player, we need to remove `Action.UPDATE_LISTED` so avoid the entry to be listed in client-side's tab list.
            For an encoded-player, we need to remove `Action.ADD_PLAYER` to
              1. avoid the player to be listed in client-side's report players menu.
              2. avoid the player to be listed in command suggestion.
         */

        EnumSet<PlayerListS2CPacket.Action> enumSet = EnumSet.of(PlayerListS2CPacket.Action.ADD_PLAYER, PlayerListS2CPacket.Action.UPDATE_GAME_MODE, PlayerListS2CPacket.Action.UPDATE_LISTED, PlayerListS2CPacket.Action.UPDATE_LATENCY, PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME);
        return new PlayerListS2CPacket(enumSet, collection);
    }

    public static ServerPlayerEntity makeServerPlayerEntity(MinecraftServer server, String playerName) {
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
    public static ServerPlayerEntity makeServerPlayerEntity(MinecraftServer server, ServerPlayerEntity player) {
        String encodedName = encodeName(player);
        return makeServerPlayerEntity(server, encodedName);
    }

    public static Map<String, Integer> getWeightMap(List<ServerPlayerEntity> players) {
        Map<String, Integer> ret = new HashMap<>();
        for (ServerPlayerEntity player : players) {
            String name = player.getGameProfile().getName();
            Integer weight = getWeight(player);
            ret.put(name, weight);
        }
        return ret;
    }

    public static Integer getWeight(ServerPlayerEntity player) {
        Optional<Integer> weight = PermissionUtil.getMeta(player, "fuji.tab_list.sort.weight", Integer::valueOf);
        return weight.orElse(0);
    }

    @Override
    public void onInitialize() {
//        String cron = Configs.configHandler.model().modules.tab_list.update_cron;
//        ScheduleUtil.addJob(UpdateEncodedPlayerTablistNameJob.class, null, null, cron, null);
    }


    public static String decodeName(String name) {
        name = name.substring(name.indexOf(META_SEPARATOR) + META_SEPARATOR.length());
        return name;
    }

    public static String encodeName(ServerPlayerEntity player) {
        int weight = getWeight(player);
        // reverse weight
        int maxIndex = AlphaTable.TABLE.length - 1;
        int index = (maxIndex) - weight;
        index = Math.min(maxIndex, index);
        String prefix = AlphaTable.TABLE[index];
        String name = player.getGameProfile().getName();
        return prefix + META_SEPARATOR + name;
    }

    private static void sync(MinecraftServer server) {
       /* make encoded player list */
        ArrayList<ServerPlayerEntity> encodedPlayers = new ArrayList<>();
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        for (ServerPlayerEntity player : players) {
            encodedPlayers.add(TabListSortInitializer.makeServerPlayerEntity(server, player));
        }

        /* update tab list name for encoded players */
        PlayerListS2CPacket playerListS2CPacket = new PlayerListS2CPacket(EnumSet.of(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME), encodedPlayers);
        server.getPlayerManager().sendToAll(playerListS2CPacket);
    }

    public static class UpdateEncodedPlayerTablistNameJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            sync(Fuji.SERVER);
        }
    }
}
