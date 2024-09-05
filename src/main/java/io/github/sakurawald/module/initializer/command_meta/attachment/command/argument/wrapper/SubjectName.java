package io.github.sakurawald.module.initializer.command_meta.attachment.command.argument.wrapper;

import io.github.sakurawald.core.command.argument.wrapper.abst.StringValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.spongepowered.asm.mixin.injection.At;

public class SubjectName extends StringValue {
    public SubjectName(String value) {
        super(value);
    }
}
