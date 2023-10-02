package fun.sakurawald.module.main_stats;

import fun.sakurawald.ServerMain;
import fun.sakurawald.config.ConfigGSON;
import fun.sakurawald.config.ConfigManager;
import fun.sakurawald.module.AbstractModule;
import fun.sakurawald.module.ModuleManager;
import fun.sakurawald.module.motd.MotdModule;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MainStatsModule extends AbstractModule {

    private static final List<Character> colors = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');
    private static final MotdModule motdModule = ModuleManager.getOrNewInstance(MotdModule.class);

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
        motdModule.updateDescriptions(descriptions);
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

            // update motd
            updateMainStats();
        }, 10, 60, TimeUnit.SECONDS);
    }

}
