package io.github.sakurawald.module.initializer.afk.accessor;

public interface AfkStateAccessor {

    void fuji$setAfk(boolean flag);

    boolean fuji$isAfk();

    void fuji$setLastLastActionTime(long lastActionTime);

    long fuji$getLastLastActionTime();
}
