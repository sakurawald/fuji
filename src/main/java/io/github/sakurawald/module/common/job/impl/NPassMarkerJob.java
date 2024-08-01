package io.github.sakurawald.module.common.job.impl;

import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public abstract class NPassMarkerJob<T> implements Job {

    int pass;
    String cron;
    Map<T, Integer> counter;

    public NPassMarkerJob(int pass, String cron) {
        this.pass = pass;
        this.cron = cron;
        this.counter = new HashMap<>();
    }

    public abstract Collection<T> getEntityList();

    public abstract boolean shouldMark(T entity);

    public void onMarked(T entity) {
        // no-op
    }

    public abstract void onCompleted(T entity);

    @Override
    public final void execute(JobExecutionContext context) throws JobExecutionException {
        for (T entity : getEntityList()) {
            if (shouldMark(entity)) {
                counter.put(entity, counter.getOrDefault(entity, 0) + 1);
                onMarked(entity);

                if (counter.get(entity) >= pass) {
                    onCompleted(entity);
                    counter.remove(entity);
                }
            } else {
                counter.remove(entity);
            }

        }
    }
}
