package io.github.sakurawald.core.command.structure;

import io.github.sakurawald.core.command.annotation.CommandRequirement;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class CommandRequirementDescriptor {

    final int level;
    final @Nullable String string;

    public static @Nullable CommandRequirementDescriptor of(@Nullable CommandRequirement annotation) {
        if (annotation == null) {
            return null;
        }

        return new CommandRequirementDescriptor(annotation.level(), annotation.string());
    }
}
