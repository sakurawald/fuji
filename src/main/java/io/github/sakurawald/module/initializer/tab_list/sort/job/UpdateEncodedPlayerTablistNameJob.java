package io.github.sakurawald.module.initializer.tab_list.sort.job;

import io.github.sakurawald.module.common.job.interfaces.CronJob;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.initializer.tab_list.sort.TabListSortInitializer;
import io.github.sakurawald.util.minecraft.ServerHelper;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.function.Supplier;

@NoArgsConstructor
public class UpdateEncodedPlayerTablistNameJob extends CronJob {

    public UpdateEncodedPlayerTablistNameJob(Supplier<String> cronSupplier) {
        super(cronSupplier);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        TabListSortInitializer.syncEncodedPlayers(ServerHelper.getDefaultServer());
    }
}
