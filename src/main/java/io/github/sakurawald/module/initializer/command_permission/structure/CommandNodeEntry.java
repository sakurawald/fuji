package io.github.sakurawald.module.initializer.command_permission.structure;

import com.mojang.brigadier.tree.CommandNode;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import lombok.Data;
import net.minecraft.server.command.ServerCommandSource;

@Data
public class CommandNodeEntry {

    final String path;
    final boolean wrapped;

    public CommandNodeEntry(CommandNode<ServerCommandSource> commandNode) {
        this.path = CommandHelper.computeCommandNodePath(commandNode);
        this.wrapped = commandNode.getRequirement() instanceof WrappedPredicate<ServerCommandSource>;
    }
}
