package io.github.sakurawald.config.model;

import io.github.sakurawald.module.initializer.scheduler.ScheduleJob;

import java.util.ArrayList;
import java.util.List;

public class SchedulerModel {

    public List<ScheduleJob> scheduleJobs = new ArrayList<>() {
        {
            this.add(new ScheduleJob("example_job", false, 3, List.of("0 0 * ? * *"),
                    List.of(
                            List.of("send_broadcast nobody gets the gift"),
                            List.of("all players get the gift!", "foreach give %player:name% minecraft:diamond 16")
//                            List.of("a random player gets the gift", "give !random_player! minecraft:diamond 1")
                    )));
        }
    };
}
