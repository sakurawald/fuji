package io.github.sakurawald.module.initializer.predicate;

import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.PermissionHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

@CommandRequirement(level = 4)
public class PredicateInitializer extends ModuleInitializer {

    @CommandNode("test-string-perm")
    private static int testStringPerm(@CommandSource ServerCommandSource source, ServerPlayerEntity player, GreedyString stringPermission) {
        boolean value = PermissionHelper.hasPermission(player.getUuid(), stringPermission.getValue());
        return CommandHelper.Return.outputBoolean(source, value);
    }

    @CommandNode("test-level-perm")
    private static int testLevelPerm(@CommandSource ServerCommandSource source, ServerPlayerEntity player, int levelPermission) {
        boolean value = player.hasPermissionLevel(levelPermission);
        return CommandHelper.Return.outputBoolean(source, value);
    }

}
