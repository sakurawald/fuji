package io.github.sakurawald.module.initializer.command_permission;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.luckperms.api.util.Tristate;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;


public class CommandPermissionInitializer extends ModuleInitializer {

    public static @NotNull String computeCommandNodePath(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandNode<ServerCommandSource> node) {
        String[] array = dispatcher.getPath(node).toArray(new String[]{});
        return String.join(".", array);
    }

    public static @NotNull WrappedPredicate<ServerCommandSource> makeWrappedPredicate(String commandPath, @NotNull Predicate<ServerCommandSource> original) {
        return source -> {
            /* ignore the non-player command source */
            if (source.getPlayer() == null) return original.test(source);

            /* try to use the wrapped predicate */
            try {
                /* By default, command /seed has no permission. So we can create a wrapped-permission "fuji.seed"
                   and then grant this permission to anyone so that he can use /seed command.
                   And also set other's permission fuji.seed false to dis-allow them to use /seed command.
                   If a command doesn't have a wrapped-permission, then it will use the original requirement-supplier.

                   Only valid command has its command path (command-alias also has its path, but it will redirect the execution to the real command-path)
                 */
                Tristate triState = PermissionHelper.checkPermission(source.getPlayer().getUuid(), "fuji.permission.%s".formatted(commandPath));
                if (triState != Tristate.UNDEFINED) {
                    return triState.asBoolean();
                }

                return original.test(source);
            } catch (Throwable use_original_predicate_if_failed) {
                return original.test(source);
            }
        };
    }

}
