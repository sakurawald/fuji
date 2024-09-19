package io.github.sakurawald.module.initializer.command_meta.shell;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.core.auxiliary.LogUtil;
import io.github.sakurawald.core.auxiliary.ReflectionUtil;
import io.github.sakurawald.core.auxiliary.minecraft.CommandHelper;
import io.github.sakurawald.core.auxiliary.minecraft.LocaleHelper;
import io.github.sakurawald.core.command.annotation.CommandNode;
import io.github.sakurawald.core.command.annotation.CommandRequirement;
import io.github.sakurawald.core.command.annotation.CommandSource;
import io.github.sakurawald.core.command.argument.wrapper.impl.GreedyString;
import io.github.sakurawald.core.command.exception.AbortOperationException;
import io.github.sakurawald.core.config.handler.abst.BaseConfigurationHandler;
import io.github.sakurawald.core.config.handler.impl.ObjectConfigurationHandler;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.command_meta.shell.config.ShellConfigModel;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class ShellInitializer extends ModuleInitializer {

    public static final BaseConfigurationHandler<ShellConfigModel> config = new ObjectConfigurationHandler<>(ReflectionUtil.getModuleControlFileName(ShellConfigModel.class), ShellConfigModel.class);

    private void checkSecurity(CommandContext<ServerCommandSource> ctx) {
        var config = this.config.getModel();

        if (!config.enable_warning.equals("CONFIRM")) {
            throw new AbortOperationException("Refuse to execute shell command: please read the official wiki.");
        }

        if (config.security.only_allow_console && ctx.getSource().getPlayer() != null) {
            LocaleHelper.sendMessageByKey(ctx.getSource(), "command.console_only");
            throw new AbortOperationException();
        }

        if (ctx.getSource().getName() != null && !config.security.allowed_player_names.contains(ctx.getSource().getName())) {
            throw new AbortOperationException("You are not in the allowed player name list.");
        }

    }

    @SuppressWarnings("deprecation")
    @CommandNode("shell")
    @CommandRequirement(level = 4)
    private int shell(@CommandSource CommandContext<ServerCommandSource> ctx, GreedyString rest) {
        checkSecurity(ctx);

        String $rest = rest.getValue();
        CompletableFuture.runAsync(() -> {
            try {
                LogUtil.info("shell exec: {}", $rest);

                Process process = Runtime.getRuntime().exec($rest, null, null);
                InputStream inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                reader.close();
                process.waitFor();

                // output
                LogUtil.info(output.toString());
                ctx.getSource().sendMessage(Text.literal(output.toString()));
            } catch (IOException | InterruptedException e) {
                LogUtil.error("failed to execute a shell command.", e);
            }
        });

        return CommandHelper.Return.SUCCESS;
    }
}
