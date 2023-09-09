package fun.sakurawald.module.zero_command_permission;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import fun.sakurawald.mixin.zero_command_permission.CommandNodeAccessor;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Predicate;

public class ZeroCommandPermissionModule {

    public static void alterCommandPermission(MinecraftServer server) {
        CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
        alterCommandNode(dispatcher, dispatcher.getRoot());
    }

    @SuppressWarnings("unchecked")
    private static void alterCommandNode(CommandDispatcher<ServerCommandSource> dispatcher, CommandNode<ServerCommandSource> node) {
        var commandPath = buildCommandNodePath(dispatcher.getPath(node).toArray(new String[]{}));
        for (CommandNode<ServerCommandSource> child : node.getChildren()) {
            alterCommandNode(dispatcher, child);
        }
        ((CommandNodeAccessor<ServerCommandSource>) node).setRequirement(createZeroPermission(commandPath, node.getRequirement()));
    }

    private static Predicate<ServerCommandSource> createZeroPermission(String commandPath, Predicate<ServerCommandSource> original) {
        return source -> {
            // ignore non-player command source
            if (source.getPlayer() == null) return original.test(source);

            try {
                /* By default, command /seed has no permission. So we can create a zero-permission "zero.seed"
                   and then grant this permission to anyone so that he can use /seed command.
                   And also set other's permission zero.seed false to dis-allow them to use /seed command.
                   If a command doesn't have a zero-permission, then it will use the original requirement-supplier.

                   Only valid command has its command path (command-alias also has its path, but it will redirect the execution to the real command-path)
                 */
                TriState triState = Permissions.getPermissionValue(source, "zero.%s".formatted(commandPath));
                return triState.orElseGet(() -> original.test(source));
            } catch (Throwable use_original_predicate_if_failed) {
                return original.test(source);
            }
        };
    }

    private static String buildCommandNodePath(String... parts) {
        return String.join(".", parts);
    }
}
