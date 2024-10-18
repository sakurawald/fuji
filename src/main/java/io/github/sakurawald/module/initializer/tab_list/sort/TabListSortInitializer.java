package io.github.sakurawald.module.initializer.tab_list.sort;

import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.tab_list.sort.structure.TabListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public class TabListSortInitializer extends ModuleInitializer {

    @Override
    protected void onReload() {

        List<ServerPlayerEntity> copy1 = TabListEntry.getInstances().stream().map(TabListEntry::getRealPlayer).toList();
        List<ServerPlayerEntity> copy2 = TabListEntry.getInstances().stream().map(TabListEntry::getDummyPlayer).toList();

        // re-make TabListEntry
        TabListEntry.getInstances().clear();
        copy1.forEach(TabListEntry::getEntryFromRealPlayer);

        // refresh
        PlayerManager playerManager = ServerHelper.getDefaultServer().getPlayerManager();
        playerManager.sendToAll(new PlayerRemoveS2CPacket(copy2.stream().map(Entity::getUuid).toList()));
        playerManager.sendToAll(TabListEntry.makePacket(TabListEntry.getDummyPlayerList()));
    }

}
