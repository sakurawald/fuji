package io.github.sakurawald.module.scheduler;

import io.github.sakurawald.ServerMain;
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
    List<String> crons;
    List<List<String>> commands_list;

    public void trigger() {
        log.info("Trigger ScheduleJob {}", this);
        List<String> commands = this.commands_list.get(new Random().nextInt(this.commands_list.size()));
        SpecializedCommand.runSpecializedCommands(ServerMain.SERVER, commands);
    }
}
