package fun.sakurawald.module.main_stats;

import fun.sakurawald.ModMain;
import fun.sakurawald.config.ConfigManager;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CustomStatisticsModule {
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public static void updateMOTD() {
        String motd = MainStats.calculateServerMainStats().resolve(ConfigManager.configWrapper.instance().modules.main_stats.dynamic_motd);
        ModMain.SERVER.setMotd(motd);
    }

    public static void registerScheduleTask(MinecraftServer server) {
        // async task
        executorService.scheduleAtFixedRate(() -> {
            // save all online-player 's stats
            server.getPlayerList().getPlayers().forEach((p) -> {
                p.getStats().save();
            });

            // update motd
            updateMOTD();
        }, 60, 60, TimeUnit.SECONDS);
    }

}
