package io.github.sakurawald.module.initializer.command_cooldown;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class CommandCooldownInitializer extends ModuleInitializer {

    private final HashMap<ServerPlayerEntity, HashMap<String, Long>> map = new HashMap<>();


    public long calculateCommandCooldown(ServerPlayerEntity player, @NotNull String commandLine) {

        // find the matched cooldown-entry
        HashMap<String, Long> commandRegex2LastExecutedTimeMS = map.computeIfAbsent(player, k -> new HashMap<>());
        long leftTime = 0;
        for (Map.Entry<String, Long> entry : Configs.configHandler.model().modules.command_cooldown.regex2ms.entrySet()) {
            if (!commandLine.matches(entry.getKey())) continue;

            long commandLineLastExecutedTimeMS = commandRegex2LastExecutedTimeMS.computeIfAbsent(entry.getKey(), k -> 0L);
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
