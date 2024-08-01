package io.github.sakurawald.module.mixin.tab_list.sort;

import io.github.sakurawald.module.initializer.tab_list.sort.TabListSortInitializer;
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
public class SyncGameProfileMixin {

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
    private static Set<String> flags = new HashSet<>();

    @Unique
    void syncGameProfileChangeToDummyPlayer(Collection<ServerPlayerEntity> collection) {
        // sync the properties from game profile
        for (ServerPlayerEntity realPlayer : collection) {
            PlayerManager playerManager = ServerHelper.getDefaultServer().getPlayerManager();
            String name = realPlayer.getGameProfile().getName();

            // if it's real player
            if (!flags.contains(name) && playerManager.getPlayerList().contains(realPlayer)) {
                flags.add(name);
                ServerPlayerEntity dummyPlayer = TabListSortInitializer.makeServerPlayerEntity(ServerHelper.getDefaultServer(), realPlayer);

                playerManager.sendToAll(new PlayerRemoveS2CPacket(Collections.singletonList(dummyPlayer.getUuid())));
                playerManager.sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, dummyPlayer));
                flags.remove(name);
            }
        }

    }

}
