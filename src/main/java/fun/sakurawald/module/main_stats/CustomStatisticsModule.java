package fun.sakurawald.module.main_stats;

import fun.sakurawald.ModMain;
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
    private static final int MOTD_LINE_LENGTH = 69;
    private static final String NEWLINE = "\n";

    public static void updateMOTD() {
        String motd = MainStats.calculateServerMainStats().resolve(ConfigManager.configWrapper.instance().modules.main_stats.dynamic_motd);
        int i = motd.indexOf(NEWLINE);
        if (i == -1) {
            motd = centerText(motd, MOTD_LINE_LENGTH);
        } else {
            String line1 = centerText(motd.substring(0, i), MOTD_LINE_LENGTH);
            String line2 = centerText(motd.substring(i + NEWLINE.length()), MOTD_LINE_LENGTH);
            motd = line1 + "\n" + line2;
        }
        ModMain.SERVER.setMotd(motd);
    }

    @SuppressWarnings("SameParameterValue")
    private static String centerText(String text, int lineLength) {
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
            server.getPlayerList().getPlayers().forEach((p) -> {
                p.getStats().save();
            });

            // update motd
            updateMOTD();
        }, 60, 60, TimeUnit.SECONDS);
    }

}
