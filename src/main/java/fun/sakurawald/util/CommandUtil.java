package fun.sakurawald.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandSourceStack;

public class CommandUtil {
    public static String buildCommandNodePath(CommandDispatcher<CommandSourceStack> dispatcher, CommandNode<CommandSourceStack> node) {
        String[] array = dispatcher.getPath(node).toArray(new String[]{});
        return String.join(".", array);
    }
}
