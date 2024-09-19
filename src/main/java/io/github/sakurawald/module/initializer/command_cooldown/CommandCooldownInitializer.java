package io.github.sakurawald.module.initializer.command_cooldown;

import io.github.sakurawald.core.config.Configs;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_cooldown.config.model.CommandCooldownConfigModel;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CommandCooldownInitializer extends ModuleInitializer {
    public final ObjectConfigurationHandler<CommandCooldownConfigModel> config = new ObjectConfigurationHandler<>(getModuleConfigFileName(), CommandCooldownConfigModel.class);

    private final HashMap<ServerPlayerEntity, HashMap<String, Long>> map = new HashMap<>();

    public long calculateCommandCooldown(ServerPlayerEntity player, @NotNull String commandLine) {

        // find the matched cooldown-entry
        HashMap<String, Long> commandRegex2LastExecutedTimeMS = map.computeIfAbsent(player, k -> new HashMap<>());
        long leftTime = 0;
        for (Map.Entry<String, Long> entry : config.getModel().regex2ms.entrySet()) {
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
