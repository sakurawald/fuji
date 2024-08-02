package io.github.sakurawald.module.initializer.works.job;

import io.github.sakurawald.module.common.job.interfaces.CronJob;
import io.github.sakurawald.module.initializer.works.interfaces.Schedulable;
import io.github.sakurawald.module.initializer.works.structure.WorksCache;
import io.github.sakurawald.module.initializer.works.WorksInitializer;
import io.github.sakurawald.module.initializer.works.structure.work.interfaces.Work;
import lombok.NoArgsConstructor;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@NoArgsConstructor
public class WorksScheduleJob extends CronJob {

    public WorksScheduleJob(JobDataMap jobDataMap, Supplier<String> cronSupplier) {
        super(jobDataMap, cronSupplier);
    }

    @Override
    public void execute(@NotNull JobExecutionContext context) {
        // save current works data
        MinecraftServer server = (MinecraftServer) context.getJobDetail().getJobDataMap().get(MinecraftServer.class.getName());
        if (server.isRunning()) {
            WorksInitializer.worksHandler.saveToDisk();
        }

        // run schedule method
        Set<Work> works = new HashSet<>();
        WorksCache.getBlockpos2works().values().forEach(works::addAll);
        WorksCache.getEntity2works().values().forEach(works::addAll);
        works.forEach(work -> {
            if (work instanceof Schedulable sm) sm.onSchedule();
        });
    }
}
