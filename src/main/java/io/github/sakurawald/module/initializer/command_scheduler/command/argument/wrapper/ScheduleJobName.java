package io.github.sakurawald.module.initializer.command_scheduler.command.argument.wrapper;

import io.github.sakurawald.core.command.argument.wrapper.abst.StringValue;
import lombok.AllArgsConstructor;
import lombok.Data;

public class ScheduleJobName extends StringValue {
    public ScheduleJobName(String value) {
        super(value);
    }
}
