package io.github.sakurawald.module.initializer.tab_list;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import javax.naming.OperationNotSupportedException;

public class TabListInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(this::render);
    }

    @Override
    public void onReload() throws OperationNotSupportedException {
        syncDisplayName();
    }

    private void syncDisplayName() {
        MinecraftServer server = Fuji.SERVER;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));
        }
    }


    public void render(MinecraftServer server) {
        ConfigModel.Modules.TabList config = Configs.configHandler.model().modules.tab_list;
        if (server.getTicks() % config.update_tick != 0) return;

        String headerControl = config.style.header;
        String footerControl = config.style.footer;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            Component header = MessageUtil.ofComponent(player, false, headerControl);
            Component footer = MessageUtil.ofComponent(player, false, footerControl);
            player.sendPlayerListHeaderAndFooter(header,footer);
        }
    }
}
