package io.github.sakurawald.module.initializer.tab_list;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.tab_list.job.RenderHeaderAndFooterJob;
import io.github.sakurawald.util.minecraft.MessageHelper;
import io.github.sakurawald.util.RandomUtil;
import io.github.sakurawald.util.minecraft.ServerHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import javax.naming.OperationNotSupportedException;

public class TabListInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> new RenderHeaderAndFooterJob().schedule());
    }

    @Override
    public void onReload() {
        syncDisplayName();
    }

    private void syncDisplayName() {
        MinecraftServer server = ServerHelper.getDefaultServer();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));
        }
    }

    public static void render(@NotNull MinecraftServer server) {
        ConfigModel.Modules.TabList config = Configs.configHandler.model().modules.tab_list;
        String headerControl = RandomUtil.drawList(config.style.header);
        String footerControl = RandomUtil.drawList(config.style.footer);
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            Component header = MessageHelper.ofComponent(player, false, headerControl);
            Component footer = MessageHelper.ofComponent(player, false, footerControl);
            player.sendPlayerListHeaderAndFooter(header, footer);
        }
    }

}
