package io.github.sakurawald.module.initializer.command_attachment.structure;

import io.github.sakurawald.module.initializer.command_attachment.command.argument.wrapper.ExecuteAsType;
import io.github.sakurawald.module.initializer.command_attachment.command.argument.wrapper.InteractType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandAttachmentEntry {
    String command;
    InteractType interactType;
    ExecuteAsType executeAsType;
    int maxUseTimes;
    boolean destroyItem;
    int useTimes;
}
