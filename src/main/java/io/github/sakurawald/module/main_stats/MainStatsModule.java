package io.github.sakurawald.module.main_stats;

import io.github.sakurawald.ServerMain;
import io.github.sakurawald.config.ConfigGSON;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.module.AbstractModule;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.motd.DynamicMotdModule;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
public class MainStatsModule extends AbstractModule {

    private final List<Character> colors = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');
    private final DynamicMotdModule dynamic_motd_module = ModuleManager.getOrNewInstance(DynamicMotdModule.class);

    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.main_stats.enable;
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.updateMainStats();
            this.registerScheduleTask(server);
        });
    }

    public void updateMainStats() {
        MainStats serverMainStats = MainStats.calculateServerMainStats();

        ConfigGSON.Modules.DynamicMOTD dynamic_motd = ConfigManager.configWrapper.instance().modules.dynamic_motd;
        ArrayList<String> descriptions = new ArrayList<>();
        dynamic_motd.descriptions.forEach(description -> descriptions.add(serverMainStats.resolve(description)));

        if (dynamic_motd_module != null) {
            dynamic_motd_module.updateDescriptions(descriptions);
        }
    }

    @SuppressWarnings({"SameParameterValue", "unused"})
    private String centerText(String text, int lineLength, int lengthDelta) {
        /* calc length */
        char[] chars = text.toCharArray();
        double length = 0;
        boolean bold = false;
        char code;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '§') {
                // skip §
                if (i + 1 != chars.length) {
                    // skip code
                    code = chars[i + 1];
                    if (code == 'l') bold = true;
                    else if (colors.contains(code) && code == 'r') bold = false;
                }
            } else {
                length += (chars[i] == ' ' ? 1 : (bold ? 1.1555555555555556 : 1));
            }
        }

        /* add length delta */
        length += lengthDelta;

        /* build spaces */
        double spaces = (lineLength - length) / 2;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < spaces; i++) {
            builder.append(' ');
        }
        builder.append(text);
        return builder.toString();
    }

    public void registerScheduleTask(MinecraftServer server) {
        // async task
        ServerMain.getSCHEDULED_EXECUTOR_SERVICE().scheduleAtFixedRate(() -> {
            // save all online-player 's stats
            server.getPlayerList().getPlayers().forEach((p) -> p.getStats().save());

            // update dynamic_motd
            updateMainStats();
        }, 10, 60, TimeUnit.SECONDS);
    }

}
