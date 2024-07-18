package io.github.sakurawald.module.initializer.test;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.common.manager.Managers;
import io.github.sakurawald.module.common.structure.BossBarTicket;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class TestInitializer extends ModuleInitializer {

    private static int $run(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        ServerPlayerEntity player = source.getPlayer();

        BossBar bossbar = BossBar.bossBar(Component.text("test progress"), 0f, BossBar.Color.BLUE,  BossBar.Overlay.NOTCHED_20);

        List<Audience> playerList = new ArrayList<>(Fuji.SERVER.getPlayerManager().getPlayerList());

        Managers.getBossBarManager().addTicket(new BossBarTicket(bossbar, 10 * 1000, playerList) {

            @Override
            public void onComplete() {
                log.warn("done with audiences {}", this.getAudiences());
            }

            @Override
            public void onAudienceDisconnected(Audience audience) {
                log.warn("audience {} disconnected", audience);
            }
        });

        return 1;
    }

    @Override
    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;
        LiteralArgumentBuilder<Object> arg1 = LiteralArgumentBuilder.literal("arg1");

        dispatcher.register(
                CommandManager.literal("test").requires(s -> s.hasPermissionLevel(4))
                        .then(CommandManager.literal("run").executes(TestInitializer::$run))
        );
    }
}
