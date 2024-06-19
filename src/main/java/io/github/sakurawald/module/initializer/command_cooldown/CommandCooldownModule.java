package io.github.sakurawald.module.initializer.command_cooldown;

import io.github.sakurawald.common.event.PreCommandExecuteEvent;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import java.util.HashMap;
import java.util.Map;

import io.github.sakurawald.util.MessageUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public class CommandCooldownModule extends ModuleInitializer {

    private final HashMap<ServerPlayerEntity, HashMap<String, Long>> map = new HashMap<>();

    @Override
    public void onInitialize() {
        PreCommandExecuteEvent.EVENT.register((parseResults, string) -> {
            ServerPlayerEntity player = parseResults.getContext().getSource().getPlayer();
            if (player == null) return ActionResult.PASS;

            long cooldown = this.calculateCommandCooldown(player, string);
            if (cooldown > 0) {
                MessageUtil.sendActionBar(player, "command_cooldown.cooldown", cooldown / 1000);
                return ActionResult.FAIL;
            }

            return ActionResult.PASS;
        });
    }

    public long calculateCommandCooldown(ServerPlayerEntity player, String commandLine) {

        // find the matched cooldown-entry
        HashMap<String, Long> commandRegex2LastExecutedTimeMS = map.computeIfAbsent(player, k -> new HashMap<>());
        long leftTime = 0;
        for (Map.Entry<String, Long> entry : Configs.configHandler.model().modules.command_cooldown.command_regex_2_cooldown_ms.entrySet()) {
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
