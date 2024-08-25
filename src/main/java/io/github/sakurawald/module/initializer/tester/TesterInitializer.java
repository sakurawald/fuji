package io.github.sakurawald.module.initializer.tester;

import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.command.annotation.Command;
import io.github.sakurawald.command.annotation.CommandPermission;
import io.github.sakurawald.command.annotation.CommandSource;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;


@Command("tester")
@CommandPermission(level = 4)
public class TesterInitializer extends ModuleInitializer {

    @Command("run")
    private static int $run(@CommandSource CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = player.server;

        return -1;
    }

}
