package io.github.sakurawald.module.initializer.command_attachment.config.model;

import io.github.sakurawald.module.initializer.command_attachment.structure.CommandAttachmentEntry;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommandAttachmentModel {
    List<CommandAttachmentEntry> entries = new ArrayList<>();
}
