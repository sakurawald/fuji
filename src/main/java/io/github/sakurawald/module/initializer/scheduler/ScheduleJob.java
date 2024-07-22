package io.github.sakurawald.module.initializer.scheduler;

import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.common.structure.CommandExecuter;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Random;

@Data
@AllArgsConstructor
public class ScheduleJob {
    String name;
    boolean enable;
    int left_trigger_times;
    List<String> crons;
    List<List<String>> commands_list;

    public void trigger() {
        Fuji.LOGGER.info("Trigger ScheduleJob {}", this.getName());

        if (left_trigger_times > 0) {
            left_trigger_times--;
            if (left_trigger_times == 0) {
                this.enable = false;
            }
            SchedulerInitializer.schedulerHandler.saveToDisk();
        }

        List<String> commands = this.commands_list.get(new Random().nextInt(this.commands_list.size()));
        CommandExecuter.executeCommandsAsConsoleWithContext(null, commands);
    }
}
