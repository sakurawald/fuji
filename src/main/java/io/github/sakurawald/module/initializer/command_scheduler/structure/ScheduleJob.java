package io.github.sakurawald.module.initializer.command_scheduler.structure;

import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.minecraft.ServerHelper;
import io.github.sakurawald.core.command.executor.CommandExecutor;
import io.github.sakurawald.core.command.structure.ExtendedCommandSource;
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
        LogUtil.info("[command scheduler] trigger job -> {}", this.getName());

        if (left_trigger_times > 0) {
            left_trigger_times--;
            if (left_trigger_times == 0) {
                this.enable = false;
            }
        }

        List<String> commands = this.commands_list.get(new Random().nextInt(this.commands_list.size()));

        // fix: sync command execution
        ServerHelper.getDefaultServer().executeSync(() -> CommandExecutor.execute(ExtendedCommandSource.asConsole(ServerHelper.getDefaultServer().getCommandSource()), commands));
    }
}
