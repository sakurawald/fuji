package io.github.sakurawald.config;

import io.github.sakurawald.module.scheduler.ScheduleJob;

import java.util.ArrayList;
import java.util.List;

public class SchedulerGSON {

    public List<ScheduleJob> scheduleJobs = new ArrayList<>() {
        {
            this.add(new ScheduleJob("example_job", true, "0 * * ? * *", List.of("say You can turn off this schedule job in scheduler.json")));
        }
    };

}
