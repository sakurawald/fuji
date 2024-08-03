package io.github.sakurawald.module.initializer.command_meta.chain;

import io.github.sakurawald.command.argument.wrapper.GreedyString;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.module.common.service.command_executor.CommandExecutor;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.minecraft.CommandHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChainInitializer extends ModuleInitializer {
    private static final Pattern CHAIN_COMMAND_PARSER = Pattern.compile("(.+?)\\s+(chain .+)");

    @Command("chain")
    @CommandPermission(level = 4)
    private int chain(GreedyString rest) {

        String $rest = rest.getString();

        Matcher matcher = CHAIN_COMMAND_PARSER.matcher($rest);
        if (matcher.find()) {
            String first = matcher.group(1);
            String second = matcher.group(2);
            int value = CommandExecutor.executeCommandAsConsole(null, first);
            // break chain, if command `fail`.
            if (value >= 0) {
                CommandExecutor.executeCommandAsConsole(null, second);
            }
        } else {
            CommandExecutor.executeCommandAsConsole(null, $rest);
        }

        return CommandHelper.Return.SUCCESS;
    }
}
