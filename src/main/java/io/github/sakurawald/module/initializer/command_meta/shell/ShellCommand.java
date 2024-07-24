package io.github.sakurawald.module.initializer.command_meta.shell;

import com.mojang.brigadier.CommandDispatcher;
import io.github.sakurawald.config.Configs;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.LogUtil;
import io.github.sakurawald.util.minecraft.CommandHelper;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.literal;

public class ShellCommand extends ModuleInitializer {
    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        // confirm
        if (!Configs.configHandler.model().modules.command_meta.shell.enable_warning.equals("CONFIRM")) {
            LogUtil.warn("refuse to enable `shell` module");
            return;
        }

        dispatcher.register(
                literal("shell")
                        .requires(ctx -> ctx.hasPermissionLevel(4))
                        .then(CommandHelper.Argument.rest()
                                .executes((ctx) -> {
                                    String rest = CommandHelper.Argument.rest(ctx);

                                    CompletableFuture.runAsync(() -> {
                                        try {
                                            LogUtil.info("shell exec: {}", rest);

                                            Process process = Runtime.getRuntime().exec(rest, null, null);
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
                                            LogUtil.cryLoudly("Failed to execute a shell command.", e);
                                        }
                                    });

                                    return CommandHelper.Return.SUCCESS;
                                })
                        ));
    }
}
