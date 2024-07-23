package io.github.sakurawald.module.initializer.tab_list;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.MessageUtil;
import io.github.sakurawald.util.RandomUtil;
import io.github.sakurawald.util.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.kyori.adventure.text.Component;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.naming.OperationNotSupportedException;

@Slf4j
public class TabListInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        String cron = Configs.configHandler.model().modules.tab_list.update_cron;
        ServerLifecycleEvents.SERVER_STARTED.register((server -> ScheduleUtil.addJob(RenderHeaderAndFooterJob.class, null, null, cron, null)));
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

    public static class RenderHeaderAndFooterJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            render(Fuji.SERVER);
        }
    }

    private static void render(MinecraftServer server) {
        ConfigModel.Modules.TabList config = Configs.configHandler.model().modules.tab_list;
        String headerControl = RandomUtil.drawList(config.style.header);
        String footerControl = RandomUtil.drawList(config.style.footer);
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            Component header = MessageUtil.ofComponent(player, false, headerControl);
            Component footer = MessageUtil.ofComponent(player, false, footerControl);
            player.sendPlayerListHeaderAndFooter(header, footer);
        }
    }

}
