package io.github.sakurawald.core.structure;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import lombok.Data;
import net.minecraft.server.command.ServerCommandSource;

@Data
public class CommandNode {

    protected final String path;

    public CommandNode(com.mojang.brigadier.tree.CommandNode<ServerCommandSource> commandNode) {
        this.path = CommandHelper.computeCommandNodePath(commandNode);
    }

}
