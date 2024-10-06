package io.github.sakurawald.module.initializer.tab_list;

import io.github.sakurawald.core.auxiliary.RandomUtil;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.event.impl.ServerLifecycleEvents;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.tab_list.config.model.TabListConfigModel;
import io.github.sakurawald.module.initializer.tab_list.job.RenderHeaderAndFooterJob;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class TabListInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<TabListConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, TabListConfigModel.class);

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> new RenderHeaderAndFooterJob().schedule());
    }

    @Override
    public void onReload() {
        updateDisplayName();
    }

    private void updateDisplayName() {
        MinecraftServer server = ServerHelper.getDefaultServer();
        for (ServerPlayerEntity player : ServerHelper.getPlayers()) {
            server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, player));
        }
    }

    public static void render() {
        String headerControl = RandomUtil.drawList(config.model().style.header);
        String footerControl = RandomUtil.drawList(config.model().style.footer);
        for (ServerPlayerEntity player : ServerHelper.getPlayers()) {
            @NotNull Text header = LocaleHelper.getTextByValue(player, headerControl);
            @NotNull Text footer = LocaleHelper.getTextByValue(player, footerControl);
            player.networkHandler.sendPacket(new PlayerListHeaderS2CPacket(header, footer));
        }

    }

}
