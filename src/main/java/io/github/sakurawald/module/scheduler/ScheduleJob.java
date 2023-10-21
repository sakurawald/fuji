package io.github.sakurawald.module.scheduler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ScheduleJob {
    String name;
    boolean enable;
    String cron;
    List<String> commands;
}
