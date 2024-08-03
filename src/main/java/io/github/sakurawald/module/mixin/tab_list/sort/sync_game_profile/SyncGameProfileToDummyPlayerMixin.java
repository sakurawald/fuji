package io.github.sakurawald.module.mixin.tab_list.sort.sync_game_profile;

import io.github.sakurawald.module.initializer.tab_list.sort.structure.TabListEntry;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(PlayerListS2CPacket.class)
public class SyncGameProfileToDummyPlayerMixin {

    @Inject(method = "<init>(Ljava/util/EnumSet;Ljava/util/Collection;)V", at = @At("TAIL"))
    void syncGameProfileChangeToDummyPlayer(EnumSet<PlayerListS2CPacket.Action> enumSet, Collection<ServerPlayerEntity> collection, CallbackInfo ci) {
        if (enumSet.contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
            syncGameProfileChangeToDummyPlayer(collection);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Action;Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At("TAIL"))
    void syncGameProfileChangeToDummyPlayer(PlayerListS2CPacket.Action action, ServerPlayerEntity serverPlayerEntity, CallbackInfo ci) {
        if (action.equals(PlayerListS2CPacket.Action.ADD_PLAYER)) {
            syncGameProfileChangeToDummyPlayer(List.of(serverPlayerEntity));
        }
    }

    @Unique
    private static Set<String> visited = new HashSet<>();

    @Unique
    void syncGameProfileChangeToDummyPlayer(Collection<ServerPlayerEntity> players) {

        // sync the properties from game profile
        for (ServerPlayerEntity player : players) {
            PlayerManager playerManager = ServerHelper.getDefaultServer().getPlayerManager();
            String name = player.getGameProfile().getName();

            // if it's real player
            if (!visited.contains(name)) {
                visited.add(name);

                if (TabListEntry.isDummyPlayer(player)) {
                    /* fix: `/fuji reload` will clear the skin of dummy player, so we need to listen the Action.ADD of dummy-player */
                    Optional<TabListEntry> optional = TabListEntry.getEntryFromDummyPlayer(player);
                    if (optional.isPresent()) {
                        player.getGameProfile().getProperties().clear();
                        player.getGameProfile().getProperties().putAll(optional.get().getRealPlayer().getGameProfile().getProperties());
                    }

                } else {
                    /* listen the Action.ADD of real-player, and sync the game profile to dummy-player */
                    ServerPlayerEntity dummyPlayer = TabListEntry.getEntryFromRealPlayer(player).getDummyPlayer();

                    // the properties will not override, so it requires clear.
                    dummyPlayer.getGameProfile().getProperties().clear();
                    dummyPlayer.getGameProfile().getProperties().putAll(player.getGameProfile().getProperties());

                    playerManager.sendToAll(new PlayerRemoveS2CPacket(Collections.singletonList(dummyPlayer.getUuid())));
                    playerManager.sendToAll(TabListEntry.makePacket(List.of(dummyPlayer)));
                }

                visited.remove(name);
            }
        }

    }

}
