package io.github.sakurawald.core.structure;

import com.mojang.brigadier.tree.CommandNode;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import lombok.Data;
import net.minecraft.server.command.ServerCommandSource;

@Data
public class CommandNodeEntry {

    protected final String path;

    public CommandNodeEntry(CommandNode<ServerCommandSource> commandNode) {
        this.path = CommandHelper.computeCommandNodePath(commandNode);
    }

}
