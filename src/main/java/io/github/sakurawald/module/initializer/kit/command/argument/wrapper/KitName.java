package io.github.sakurawald.module.initializer.kit.command.argument.wrapper;

import io.github.sakurawald.core.command.argument.wrapper.abst.StringValue;
import lombok.AllArgsConstructor;
import lombok.Data;

public class KitName extends StringValue {
    public KitName(String value) {
        super(value);
    }
}
