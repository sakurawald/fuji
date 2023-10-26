package io.github.sakurawald.module.scheduler;

import io.github.sakurawald.ServerMain;
import io.github.sakurawald.config.base.ConfigManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

@Data
@AllArgsConstructor
@Slf4j
public class ScheduleJob {
    String name;
    boolean enable;
    int left_trigger_times;
    List<String> crons;
    List<List<String>> commands_list;

    public void trigger() {
        log.info("Trigger ScheduleJob {}", this);

        if (left_trigger_times > 0) {
            left_trigger_times--;
            if (left_trigger_times == 0) {
                this.enable = false;
            }
            ConfigManager.schedulerWrapper.saveToDisk();
        }

        List<String> commands = this.commands_list.get(new Random().nextInt(this.commands_list.size()));
        SpecializedCommand.runSpecializedCommands(ServerMain.SERVER, commands);
    }
}
