package io.github.sakurawald.module.initializer.test;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.module.initializer.placeholder.MainStats;
import io.github.sakurawald.util.LuckPermsUtil;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;


@Slf4j
public class TestInitializer extends ModuleInitializer {

    private static int clearChat(CommandContext<ServerCommandSource> ctx) {
        for (int i = 0; i < 50; i++) {
            ctx.getSource().sendMessage(Component.empty());
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int magic(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();


        MainStats mainStats = MainStats.calculatePlayerMainStats(player.getUuidAsString());


        LuckPermsUtil.saveMeta(player, "fuji.placed", String.valueOf(mainStats.placed));
        LuckPermsUtil.saveMeta(player, "fuji.mined", String.valueOf(mainStats.mined));
        LuckPermsUtil.saveMeta(player, "fuji.killed", String.valueOf(mainStats.killed));
        LuckPermsUtil.saveMeta(player, "fuji.moved", String.valueOf(mainStats.moved));
        LuckPermsUtil.saveMeta(player, "fuji.playtime", String.valueOf(mainStats.playtime));


        log.warn("mainStats = {}", mainStats);

//        log.warn("flyspeed = {}", Options.get(player, "fuji.flyspeed", Double::valueOf));
//        TriState test = Permissions.getPermissionValue(ctx.getSource(), "fuji.seed");
//        source.sendMessage(Text.literal("state is " + test.name()));
        return 1;
    }

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        dispatcher.register(
                CommandManager.literal("test").requires(s -> s.hasPermissionLevel(4))
                        .then(CommandManager.literal("clear-chat").executes(TestInitializer::clearChat))
                        .then(CommandManager.literal("magic").executes(TestInitializer::magic))
        );
    }
}
