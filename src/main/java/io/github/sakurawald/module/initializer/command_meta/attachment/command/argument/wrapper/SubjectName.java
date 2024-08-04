package io.github.sakurawald.module.initializer.command_meta.attachment.command.argument.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.spongepowered.asm.mixin.injection.At;

@AllArgsConstructor
@Data
public class SubjectName {
    String name;
}
