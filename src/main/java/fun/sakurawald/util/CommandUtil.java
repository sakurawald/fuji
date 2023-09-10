package fun.sakurawald.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.ServerCommandSource;

public class CommandUtil {
    public static String buildCommandNodePath(CommandDispatcher<ServerCommandSource> dispatcher, CommandNode<ServerCommandSource> node){
        String[] array = dispatcher.getPath(node).toArray(new String[]{});
        return String.join(".", array);
    }
}
