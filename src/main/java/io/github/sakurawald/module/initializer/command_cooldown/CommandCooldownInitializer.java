package io.github.sakurawald.module.initializer.command_cooldown;

import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.core.structure.Counter;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_cooldown.config.model.CommandCooldownConfigModel;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandCooldownInitializer extends ModuleInitializer {
    public static final BaseConfigurationHandler<CommandCooldownConfigModel> config = new ObjectConfigurationHandler<>(BaseConfigurationHandler.CONFIG_JSON, CommandCooldownConfigModel.class);

    private static final HashMap<ServerPlayerEntity, Counter<String>> player2counter = new HashMap<>();

    public static long computeLeftTime(ServerPlayerEntity player, @NotNull String commandLine) {
        Counter<String> counter = player2counter.computeIfAbsent(player, k -> new Counter<>());

        Optional<Map.Entry<String, Long>> first = config.getModel().regex2ms.entrySet()
            .stream()
            .filter(it -> commandLine.matches(it.getKey()))
            .findFirst();

        return first.map(entry -> counter.computeLeftTime(entry.getKey(), entry.getValue()))
            .orElse(-1L);
    }

}
