package io.github.sakurawald.module.initializer.command_toolbox.warp.command.argument.wrapper;

import io.github.sakurawald.core.command.argument.wrapper.abst.StringValue;
import lombok.AllArgsConstructor;
import lombok.Data;

public class WarpName extends StringValue {
    public WarpName(String value) {
        super(value);
    }
}
