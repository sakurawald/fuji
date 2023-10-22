package io.github.sakurawald.config;

import io.github.sakurawald.module.scheduler.ScheduleJob;

import java.util.ArrayList;
import java.util.List;

public class SchedulerGSON {

    public List<ScheduleJob> scheduleJobs = new ArrayList<>() {
        {
            this.add(new ScheduleJob("example_job", false, List.of("0 0 * ? * *"),
                    List.of(
                            List.of("say Nobody gets the gift!"),
                            List.of("say All players get the gift!", "give [all_player] minecraft:diamond 1"),
                            List.of("say player [random_player] get the gift!", "give [random_player] minecraft:diamond 1")
                    )));
        }
    };
}
