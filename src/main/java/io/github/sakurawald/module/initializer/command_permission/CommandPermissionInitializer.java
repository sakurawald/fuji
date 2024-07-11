package io.github.sakurawald.module.initializer.command_permission;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.mixin.command_permission.CommandNodeAccessor;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.util.function.Predicate;


public class CommandPermissionInitializer extends ModuleInitializer {

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::alterCommandPermission
        );
    }

    public void alterCommandPermission(MinecraftServer server) {
        CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
        alterCommandNode(dispatcher, dispatcher.getRoot());
    }

    private String buildCommandNodePath(CommandDispatcher<ServerCommandSource> dispatcher, CommandNode<ServerCommandSource> node) {
        String[] array = dispatcher.getPath(node).toArray(new String[]{});
        return String.join(".", array);
    }

    @SuppressWarnings("unchecked")
    private void alterCommandNode(CommandDispatcher<ServerCommandSource> dispatcher, CommandNode<ServerCommandSource> node) {
        var commandPath = buildCommandNodePath(dispatcher, node);
        for (CommandNode<ServerCommandSource> child : node.getChildren()) {
            alterCommandNode(dispatcher, child);
        }
        ((CommandNodeAccessor<ServerCommandSource>) node).setRequirement(createWrappedPermission(commandPath, node.getRequirement()));
    }

    private Predicate<ServerCommandSource> createWrappedPermission(String commandPath, Predicate<ServerCommandSource> original) {
        return source -> {
            // ignore the non-player command source
            if (source.getPlayer() == null) return original.test(source);

            try {
                /* By default, command /seed has no permission. So we can create a wrapped-permission "fuji.seed"
                   and then grant this permission to anyone so that he can use /seed command.
                   And also set other's permission fuji.seed false to dis-allow them to use /seed command.
                   If a command doesn't have a wrapped-permission, then it will use the original requirement-supplier.

                   Only valid command has its command path (command-alias also has its path, but it will redirect the execution to the real command-path)
                 */
                TriState triState = Permissions.getPermissionValue(source.getPlayer(), "fuji.permission.%s".formatted(commandPath));
                if (triState != TriState.DEFAULT) {
                    return triState.get();
                }

                return original.test(source);
            } catch (Throwable use_original_predicate_if_failed) {
                return original.test(source);
            }
        };
    }

}
