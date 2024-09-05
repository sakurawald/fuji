package io.github.sakurawald.module.initializer.home.command.argument.wrapper;

import io.github.sakurawald.core.command.argument.wrapper.abst.StringValue;
import lombok.AllArgsConstructor;
import lombok.Data;

public class HomeName extends StringValue {
    public HomeName(String value) {
        super(value);
    }
}
