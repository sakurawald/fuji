package io.github.sakurawald.module.common.manager;

import io.github.sakurawald.module.ModuleManager;
import io.github.sakurawald.module.common.manager.impl.attachment.AttachmentManager;
import io.github.sakurawald.module.common.manager.impl.backup.BackupRescueManager;
import io.github.sakurawald.module.common.manager.impl.backup.StandardBackupManager;
import io.github.sakurawald.module.common.manager.impl.bossbar.BossBarManager;
import io.github.sakurawald.module.common.manager.impl.backup.AbstractBackupManager;
import io.github.sakurawald.module.common.manager.impl.scheduler.ScheduleManager;
import lombok.Getter;
import org.spongepowered.asm.mixin.injection.At;

public class Managers {

    @Getter
    private static final ModuleManager moduleManager = new ModuleManager();

    @Getter
    private static final BossBarManager bossBarManager = new BossBarManager();

    @Getter
    private static final ScheduleManager scheduleManager = new ScheduleManager();

    @Getter
    private static final AbstractBackupManager standardBackupManager = new StandardBackupManager();

    @Getter
    private static final AbstractBackupManager rescueBackupManager = new BackupRescueManager();

    @Getter
    private static final AttachmentManager attachmentManager = new AttachmentManager();
}
