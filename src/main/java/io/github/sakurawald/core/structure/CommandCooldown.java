package io.github.sakurawald.core.structure;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class CommandCooldown extends Cooldown<String> {

    private static final String GLOBAL_NAME = "GLOBAL";
    final @Nullable String name;
    final long cooldownMs;
    final int maxUsage;
    final boolean persistent;
    final boolean global;
    final Map<String, Integer> usage = new HashMap<>();

    @Override
    public long computeCooldown(String key, Long cooldown) {
        return super.computeCooldown(this.global ? GLOBAL_NAME : key, cooldown);
    }

    @Override
    public long tryUse(String key, Long cooldown) {
        return super.tryUse(this.global ? GLOBAL_NAME : key, cooldown);
    }
}
