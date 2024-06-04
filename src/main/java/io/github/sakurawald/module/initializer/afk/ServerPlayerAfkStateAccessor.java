package io.github.sakurawald.module.initializer.afk;

public interface ServerPlayerAfkStateAccessor {

    void fuji$setAfk(boolean flag);

    boolean fuji$isAfk();

    void fuji$setLastLastActionTime(long lastActionTime);

    long fuji$getLastLastActionTime();
}
