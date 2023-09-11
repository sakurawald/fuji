package fun.sakurawald.module.command_cooldown;

import fun.sakurawald.config.ConfigManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

public class CommandCooldownModule {
    private static final HashMap<ServerPlayer, HashMap<String, Long>> map = new HashMap<>();

    public static long isCommandCooldown(ServerPlayer player, String commandLine) {
        map.putIfAbsent(player, new HashMap<>());
        HashMap<String, Long> commandRegex2LastExecutedTimeMS = map.get(player);

        // find the matched cooldown-entry
        long leftTime = 0;
        for (Map.Entry<String, Long> entry : ConfigManager.configWrapper.instance().modules.command_cooldown.command_regex_2_cooldown_ms.entrySet()) {
            if (!commandLine.matches(entry.getKey())) continue;

            commandRegex2LastExecutedTimeMS.putIfAbsent(entry.getKey(), 0L);
            long commandLineLastExecutedTimeMS = commandRegex2LastExecutedTimeMS.get(entry.getKey());
            long currentTimeMS = System.currentTimeMillis();
            long cooldownMS = entry.getValue();

            leftTime = Math.max(0, cooldownMS - (currentTimeMS - commandLineLastExecutedTimeMS));
            if (leftTime == 0) {
                commandRegex2LastExecutedTimeMS.put(entry.getKey(), currentTimeMS);
            }
        }

        return leftTime;
    }
}
