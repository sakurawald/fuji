package io.github.sakurawald.module.initializer.placeholder;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.HashMap;
import java.util.Map;


@Slf4j
public class PlaceholderInitializer extends ModuleInitializer {
    private final Map<String, Map<String, String>> rotate = new HashMap<>();

    private static final String NO_PLAYER = "no player";

    private void registerHealthBarPlaceholder() {
        Placeholders.register(Identifier.of("fuji:health_bar"), (ctx, args) -> {
            if (ctx.player() == null) {
                return PlaceholderResult.invalid();
            }

            ServerPlayerEntity player = ctx.player();

            int totalHearts = 10;
            int filledHearts = (int) (player.getHealth() / 2);
            int unfilledHearts = totalHearts - filledHearts;
            String str = "♥".repeat(filledHearts) + "♡".repeat(unfilledHearts);
            return PlaceholderResult.value(Text.literal(str));
        });
    }

    private void registerRotatePlaceholder() {
        Placeholders.register(Identifier.of("fuji:rotate"), (ctx, args) -> {
            String namespace = "default";
            if (ctx.hasPlayer()) {
                namespace = ctx.player().getGameProfile().getName();
            }

            rotate.putIfAbsent(namespace, new HashMap<>());
            Map<String, String> rotateMap = rotate.get(namespace);
            rotateMap.putIfAbsent(args, args);

            String frame = rotateMap.get(args);
            rotateMap.put(args, StringUtils.rotate(frame, -1));

            return PlaceholderResult.value(Text.literal(frame));
        });
    }

    @Override
    public void onInitialize() {
        /* register placeholders */
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_mined"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid(NO_PLAYER);
                    return PlaceholderResult.value(Text.literal(String.valueOf(SumUpPlaceholder.ofPlayer(ctx.player().getUuidAsString()).mined)));
                });
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "server_mined"),
                (ctx, arg) -> PlaceholderResult.value(Text.literal(String.valueOf(SumUpPlaceholder.ofServer().mined))));

        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_placed"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid(NO_PLAYER);
                    return PlaceholderResult.value(Text.literal(String.valueOf(SumUpPlaceholder.ofPlayer(ctx.player().getUuidAsString()).placed)));
                });
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "server_placed"),
                (ctx, arg) -> PlaceholderResult.value(Text.literal(String.valueOf(SumUpPlaceholder.ofServer().placed))));

        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_killed"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid(NO_PLAYER);
                    return PlaceholderResult.value(Text.literal(String.valueOf(SumUpPlaceholder.ofPlayer(ctx.player().getUuidAsString()).killed)));
                });
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "server_killed"),
                (ctx, arg) -> PlaceholderResult.value(Text.literal(String.valueOf(SumUpPlaceholder.ofServer().killed))));


        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_moved"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid(NO_PLAYER);
                    SumUpPlaceholder sumUpPlaceholder = SumUpPlaceholder.ofPlayer(ctx.player().getUuidAsString());
                    return PlaceholderResult.value(Text.literal(String.valueOf(sumUpPlaceholder.moved)));
                });
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "server_moved"),
                (ctx, arg) -> {
                    SumUpPlaceholder sumUpPlaceholder = SumUpPlaceholder.ofServer();
                    return PlaceholderResult.value(Text.literal(String.valueOf(sumUpPlaceholder.moved)));
                });

        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_playtime"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid(NO_PLAYER);
                    SumUpPlaceholder sumUpPlaceholder = SumUpPlaceholder.ofPlayer(ctx.player().getUuidAsString());
                    return PlaceholderResult.value(Text.literal(String.valueOf(sumUpPlaceholder.playtime)));
                });
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "server_playtime"),
                (ctx, arg) -> {
                    SumUpPlaceholder sumUpPlaceholder = SumUpPlaceholder.ofServer();
                    return PlaceholderResult.value(Text.literal(String.valueOf(sumUpPlaceholder.playtime)));
                });
        registerHealthBarPlaceholder();
        registerRotatePlaceholder();

        /* events */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            SumUpPlaceholder.ofServer();
            ScheduleUtil.addJob(UpdatePlaceholderJob.class, null, null, ScheduleUtil.CRON_EVERY_MINUTE, new JobDataMap() {
                {
                    this.put(MinecraftServer.class.getName(), server);
                    this.put(PlaceholderInitializer.class.getName(), PlaceholderInitializer.this);
                }
            });
        });
    }

    public static class UpdatePlaceholderJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            // save all online-player's stats into /stats/ folder
            MinecraftServer server = (MinecraftServer) context.getJobDetail().getJobDataMap().get(MinecraftServer.class.getName());
            server.getPlayerManager().getPlayerList().forEach((p) -> p.getStatHandler().save());

            // update
            SumUpPlaceholder.ofServer();
        }
    }
}
