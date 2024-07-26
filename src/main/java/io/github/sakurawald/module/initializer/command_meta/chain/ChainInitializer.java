package io.github.sakurawald.module.initializer.command_meta.chain;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.common.structure.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.server.command.CommandManager.literal;

public class ChainInitializer extends ModuleInitializer {
    private static final Pattern CHAIN_COMMAND_PARSER = Pattern.compile("(.+?)\\s+(chain .+)");

    @Override
    public void registerCommand(@NotNull CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("chain").then(CommandHelper.Argument.rest().executes(this::chain)));
    }

    private int chain(@NotNull CommandContext<ServerCommandSource> ctx) {
        String rest = CommandHelper.Argument.rest(ctx);

        Matcher matcher = CHAIN_COMMAND_PARSER.matcher(rest);
        if (matcher.find()) {
            String first = matcher.group(1);
            String second = matcher.group(2);
            int value = CommandExecutor.executeCommandAsConsole(null, first);
            // break chain, if command `fail`.
            if (value >= 0) {
                CommandExecutor.executeCommandAsConsole(null, second);
            }
        } else {
            CommandExecutor.executeCommandAsConsole(null, rest);
        }

        return CommandHelper.Return.SUCCESS;
    }
}
