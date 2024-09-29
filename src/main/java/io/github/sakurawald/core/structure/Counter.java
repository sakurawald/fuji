package io.github.sakurawald.core.structure;

import java.util.HashMap;
import java.util.Map;

public class Counter<T> {

    final Map<T, Long> map = new HashMap<>();

    public long computeLeftTime(T key, Long cooldown) {
        long lastUpdateTimeMs = map.computeIfAbsent(key, k -> 0L);
        long currentTimeMs = System.currentTimeMillis();
        long cooldownMS = cooldown;

        long leftTime = cooldownMS - (currentTimeMs - lastUpdateTimeMs);
        if (leftTime < 0) {
            map.put(key, currentTimeMs);
        }

        return leftTime;
    }

}
