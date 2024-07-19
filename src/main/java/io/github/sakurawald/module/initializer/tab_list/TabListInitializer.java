package io.github.sakurawald.module.initializer.tab_list;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class TabListInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        ServerTickEvents.START_SERVER_TICK.register(TabListInitializer::render);
    }

    public static void render(MinecraftServer server) {
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
