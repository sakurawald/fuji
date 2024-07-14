package io.github.sakurawald.module.initializer.placeholder;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.motd.MotdInitializer;
import io.github.sakurawald.util.ScheduleUtil;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;


public class PlaceholderInitializer extends ModuleInitializer {

    private final MotdInitializer motd_module = ModuleManager.getInitializer(MotdInitializer.class);

    @Override
    public void onInitialize() {

        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "my_placeholder"),
                (ctx, arg) -> PlaceholderResult.value(Text.literal("Hello World!"))
        );

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
            ConfigModel.Modules.MOTD motd = Configs.configHandler.model().modules.motd;
            ArrayList<String> descriptions = new ArrayList<>();
            motd.descriptions.forEach(description -> descriptions.add(serverMainStats.resolve(server, description)));

            motd_module.updateDescriptions(descriptions);
        }
    }

    public void registerScheduleTask(MinecraftServer server) {
        // async task
        ScheduleUtil.addJob(UpdateMainStatsJob.class, null, null, ScheduleUtil.CRON_EVERY_MINUTE, new JobDataMap() {
            {
                this.put(MinecraftServer.class.getName(), server);
                this.put(PlaceholderInitializer.class.getName(), PlaceholderInitializer.this);
            }
        });
    }

    public static class UpdateMainStatsJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            // save all online-player's stats
            MinecraftServer server = (MinecraftServer) context.getJobDetail().getJobDataMap().get(MinecraftServer.class.getName());
            server.getPlayerManager().getPlayerList().forEach((p) -> p.getStatHandler().save());

            // update main stats
            PlaceholderInitializer module = (PlaceholderInitializer) context.getJobDetail().getJobDataMap().get(PlaceholderInitializer.class.getName());
            module.updateMainStats(server);
        }
    }
}
