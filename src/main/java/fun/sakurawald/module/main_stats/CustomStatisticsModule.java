package fun.sakurawald.module.main_stats;

import fun.sakurawald.ServerMain;
import fun.sakurawald.config.ConfigGSON;
import fun.sakurawald.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CustomStatisticsModule {
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    private static final List<Character> colors = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f');

    public static void updateMOTD() {
        ConfigGSON.Modules.MainStats main_stats = ConfigManager.configWrapper.instance().modules.main_stats;
        String motd = MainStats.calculateServerMainStats().resolve(main_stats.dynamic_motd);
        ServerMain.SERVER.setMotd(motd);
    }

    @SuppressWarnings({"SameParameterValue", "unused"})
    private static String centerText(String text, int lineLength, int lengthDelta) {
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

    public static void registerScheduleTask(MinecraftServer server) {
        // async task
        executorService.scheduleAtFixedRate(() -> {
            // save all online-player 's stats
            server.getPlayerList().getPlayers().forEach((p) -> p.getStats().save());

            // update motd
            updateMOTD();
        }, 60, 60, TimeUnit.SECONDS);
    }

}
