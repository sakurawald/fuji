package io.github.sakurawald.module.initializer.command_permission;

import io.github.sakurawald.core.annotation.Cite;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.event.impl.ServerLifecycleEvents;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_permission.gui.CommandPermissionGui;
import io.github.sakurawald.module.initializer.command_permission.structure.CommandNodePermission;
import io.github.sakurawald.module.initializer.command_permission.structure.WrappedPredicate;
import net.luckperms.api.util.Tristate;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;


@Cite("https://github.com/DrexHD/VanillaPermissions")
@CommandNode("command-permission")
@CommandRequirement(level = 4)
public class CommandPermissionInitializer extends ModuleInitializer {

    @CommandNode
    public static int gui(@CommandSource ServerPlayerEntity player) {
        List<CommandNodePermission> entities = CommandHelper.getCommandNodes().stream()
            .map(CommandNodePermission::new)
            .sorted(Comparator.comparing(CommandNodePermission::getPath))
            .toList();
        new CommandPermissionGui(player, entities, 0).open();
        return CommandHelper.Return.SUCCESS;
    }

    public static String computeCommandPermission(String commandPath) {
        return "fuji.permission.%s".formatted(commandPath);
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
                Tristate triState = PermissionHelper.checkPermission(source.getPlayer().getUuid(), computeCommandPermission(commandPath));
                if (triState != Tristate.UNDEFINED) {
                    return triState.asBoolean();
                }

                return original.test(source);
            } catch (Throwable use_original_predicate_if_failed) {
                return original.test(source);
            }
        };
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            // ensure the getRequirement() is triggered.
            CommandHelper.getCommandNodes().forEach(com.mojang.brigadier.tree.CommandNode::getRequirement);
        });
    }

}
