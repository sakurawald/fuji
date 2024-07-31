package io.github.sakurawald.module.initializer.command_scheduler;

import io.github.sakurawald.module.common.structure.CommandExecutor;
import io.github.sakurawald.util.LogUtil;
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
        LogUtil.info("Trigger ScheduleJob {}", this.getName());

        if (left_trigger_times > 0) {
            left_trigger_times--;
            if (left_trigger_times == 0) {
                this.enable = false;
            }
            CommandSchedulerInitializer.getSchedulerHandler().saveToDisk();
        }

        List<String> commands = this.commands_list.get(new Random().nextInt(this.commands_list.size()));
        CommandExecutor.executeCommandsAsConsoleWithContext(null, commands);
    }
}
