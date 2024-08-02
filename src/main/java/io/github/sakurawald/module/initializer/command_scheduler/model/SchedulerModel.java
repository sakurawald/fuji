package io.github.sakurawald.module.initializer.command_scheduler.model;

import io.github.sakurawald.module.initializer.command_scheduler.ScheduleJob;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SchedulerModel {

    public @NotNull List<ScheduleJob> scheduleJobs = new ArrayList<>() {
        {
            this.add(new ScheduleJob("example_job", false, 3, List.of("0 0 * ? * *"),
                    List.of(
                            List.of("sendbroadcast nobody gets the gift"),
                            List.of("sendbroadcast all players get the gift!", "give @a minecraft:diamond 16")
                            // todo: double placeholder parse

//                            List.of("a random player gets the gift", "give !random_player! minecraft:diamond 1")
                    )));
        }
    };
}
