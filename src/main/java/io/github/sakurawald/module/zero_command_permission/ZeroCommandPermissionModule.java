package io.github.sakurawald.module.zero_command_permission;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import io.github.sakurawald.config.ConfigManager;
import io.github.sakurawald.mixin.zero_command_permission.CommandNodeAccessor;
import io.github.sakurawald.module.AbstractModule;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class ZeroCommandPermissionModule extends AbstractModule {

    @Override
    public Supplier<Boolean> enableModule() {
        return () -> ConfigManager.configWrapper.instance().modules.zero_command_permission.enable;
    }

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(this::alterCommandPermission
        );
    }

    public void alterCommandPermission(MinecraftServer server) {
        CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
        alterCommandNode(dispatcher, dispatcher.getRoot());
    }

    private String buildCommandNodePath(CommandDispatcher<CommandSourceStack> dispatcher, CommandNode<CommandSourceStack> node) {
        String[] array = dispatcher.getPath(node).toArray(new String[]{});
        return String.join(".", array);
    }

    @SuppressWarnings("unchecked")
    private void alterCommandNode(CommandDispatcher<CommandSourceStack> dispatcher, CommandNode<CommandSourceStack> node) {
        var commandPath = buildCommandNodePath(dispatcher, node);
        for (CommandNode<CommandSourceStack> child : node.getChildren()) {
            alterCommandNode(dispatcher, child);
        }
        ((CommandNodeAccessor<CommandSourceStack>) node).setRequirement(createZeroPermission(commandPath, node.getRequirement()));
    }

    private Predicate<CommandSourceStack> createZeroPermission(String commandPath, Predicate<CommandSourceStack> original) {
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

}
