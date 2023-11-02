package io.github.sakurawald.module.initializer.main_stats;

import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.config.gson.ConfigGSON;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.motd.MotdModule;
import io.github.sakurawald.util.ScheduleUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainStatsModule extends ModuleInitializer {

    private final List<Character> colors = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');
    private final MotdModule motd_module = ModuleManager.getInitializer(MotdModule.class);

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.updateMainStats(server);
            this.registerScheduleTask(server);
        });
    }

    public void updateMainStats(MinecraftServer server) {
        // calc main stats
        MainStats serverMainStats = MainStats.calculateServerMainStats();

        // update motd if motd module is enabled
        if (motd_module != null) {
            ConfigGSON.Modules.MOTD motd = ConfigManager.configWrapper.instance().modules.motd;
            ArrayList<String> descriptions = new ArrayList<>();
            motd.descriptions.forEach(description -> descriptions.add(serverMainStats.resolve(server, description)));
            motd_module.updateDescriptions(descriptions);
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
        ScheduleUtil.addJob(UpdateMainStatsJob.class, null, null, ScheduleUtil.CRON_EVERY_MINUTE, new JobDataMap() {
            {
                this.put(MinecraftServer.class.getName(), server);
                this.put(MainStatsModule.class.getName(), MainStatsModule.this);
            }
        });
    }

    public static class UpdateMainStatsJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            // save all online-player's stats
            MinecraftServer server = (MinecraftServer) context.getJobDetail().getJobDataMap().get(MinecraftServer.class.getName());
            server.getPlayerList().getPlayers().forEach((p) -> p.getStats().save());

            // update main stats
            MainStatsModule module = (MainStatsModule) context.getJobDetail().getJobDataMap().get(MainStatsModule.class.getName());
            module.updateMainStats(server);
        }
    }
}
