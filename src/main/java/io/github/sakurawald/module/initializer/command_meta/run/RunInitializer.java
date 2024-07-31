package io.github.sakurawald.module.initializer.command_meta.run;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.adapter.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.module.common.structure.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import lombok.SneakyThrows;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.literal;

@Command("run")
@CommandPermission(level =  4)
public class RunInitializer extends ModuleInitializer {

    @Command("as console")
    private int runAsConsole(GreedyString rest) {
        CommandExecutor.executeCommandAsConsole(null, rest.getString());
        return CommandHelper.Return.SUCCESS;
    }

    @Command("as player")
    private int runAsPlayer(ServerPlayerEntity player, GreedyString rest) {
        CommandExecutor.executeCommandAsPlayer(player, rest.getString());
        return CommandHelper.Return.SUCCESS;
    }
}
