package io.github.sakurawald.module.afk;

public interface ServerPlayerAccessor_afk {

    void sakurawald$setAfk(boolean flag);

    boolean sakurawald$isAfk();

    void sakurawald$setLastLastActionTime(long lastActionTime);

    long sakurawald$getLastLastActionTime();
}
