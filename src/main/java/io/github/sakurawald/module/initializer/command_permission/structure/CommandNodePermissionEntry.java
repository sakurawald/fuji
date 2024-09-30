package io.github.sakurawald.module.initializer.command_permission.structure;

import com.mojang.brigadier.tree.CommandNode;
import io.github.sakurawald.core.structure.CommandNodeEntry;
import lombok.Getter;
import net.minecraft.server.command.ServerCommandSource;

@Getter
public class CommandNodePermissionEntry extends CommandNodeEntry {

    final boolean wrapped;

    public CommandNodePermissionEntry(CommandNode<ServerCommandSource> commandNode) {
        super(commandNode);
        this.wrapped = commandNode.getRequirement() instanceof WrappedPredicate<ServerCommandSource>;
    }
}
