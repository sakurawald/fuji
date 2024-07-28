package io.github.sakurawald.module.common.manager;

import io.github.sakurawald.module.common.manager.interfaces.AbstractBackupManager;
import lombok.Getter;

public class Managers {

    @Getter
    private static final BossBarManager bossBarManager = new BossBarManager();

    @Getter
    private static final ScheduleManager scheduleManager = new ScheduleManager();

    @Getter
    private static final AbstractBackupManager standardBackupManager = new StandardBackupManager();

    @Getter
    private static final AbstractBackupManager rescueBackupManager = new BackupRescueManager();

}
