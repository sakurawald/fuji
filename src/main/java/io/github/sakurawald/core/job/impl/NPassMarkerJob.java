package io.github.sakurawald.core.job.impl;

import io.github.sakurawald.core.job.abst.CronJob;
import lombok.NoArgsConstructor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@NoArgsConstructor
public abstract class NPassMarkerJob<T> extends CronJob {

    int pass;
    Map<T, Integer> counter;

    public NPassMarkerJob(int pass, Supplier<String> cronSupplier) {
        super(cronSupplier);
        this.pass = pass;
        this.counter = new HashMap<>();
    }

    public abstract Collection<T> getEntityList();

    public abstract boolean shouldMark(T entity);

    @SuppressWarnings({"EmptyMethod", "unused"})
    public void onMarked(T entity) {
        // no-op
    }

    public abstract void onCompleted(T entity);

    @Override
    public final void execute(JobExecutionContext context) {
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
