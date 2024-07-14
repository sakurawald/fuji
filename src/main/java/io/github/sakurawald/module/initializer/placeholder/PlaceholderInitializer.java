package io.github.sakurawald.module.initializer.placeholder;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import io.github.sakurawald.Fuji;
import io.github.sakurawald.module.initializer.ModuleInitializer;
import io.github.sakurawald.util.ScheduleUtil;
import lombok.extern.slf4j.Slf4j;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;


@Slf4j
public class PlaceholderInitializer extends ModuleInitializer {

    private static final String NO_PLAYER = "no player";

    @Override
    public void onInitialize() {
       /* register placeholders */
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_mined"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid(NO_PLAYER);
                    return PlaceholderResult.value(Text.literal(String.valueOf(PlayerSumUpPlaceholder.ofPlayer(ctx.player().getUuidAsString()).mined)));
                });
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "server_mined"),
                (ctx, arg) -> PlaceholderResult.value(Text.literal(String.valueOf(PlayerSumUpPlaceholder.ofServer().mined))));

        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_placed"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid(NO_PLAYER);
                    return PlaceholderResult.value(Text.literal(String.valueOf(PlayerSumUpPlaceholder.ofPlayer(ctx.player().getUuidAsString()).placed)));
                });
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "server_placed"),
                (ctx, arg) -> PlaceholderResult.value(Text.literal(String.valueOf(PlayerSumUpPlaceholder.ofServer().placed))));

        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_killed"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid(NO_PLAYER);
                    return PlaceholderResult.value(Text.literal(String.valueOf(PlayerSumUpPlaceholder.ofPlayer(ctx.player().getUuidAsString()).killed)));
                });
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "server_killed"),
                (ctx, arg) -> PlaceholderResult.value(Text.literal(String.valueOf(PlayerSumUpPlaceholder.ofServer().killed))));


        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_moved"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid(NO_PLAYER);
                    PlayerSumUpPlaceholder playerSumUpPlaceholder = PlayerSumUpPlaceholder.ofPlayer(ctx.player().getUuidAsString());
                    return PlaceholderResult.value(Text.literal(String.valueOf(playerSumUpPlaceholder.moved)));
                });
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "server_moved"),
                (ctx, arg) -> {
                    PlayerSumUpPlaceholder playerSumUpPlaceholder = PlayerSumUpPlaceholder.ofServer();
                    return PlaceholderResult.value(Text.literal(String.valueOf(playerSumUpPlaceholder.moved)));
                });

        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "player_playtime"),
                (ctx, arg) -> {
                    if (ctx.player() == null) PlaceholderResult.invalid(NO_PLAYER);
                    PlayerSumUpPlaceholder playerSumUpPlaceholder = PlayerSumUpPlaceholder.ofPlayer(ctx.player().getUuidAsString());
                    return PlaceholderResult.value(Text.literal(String.valueOf(playerSumUpPlaceholder.playtime)));
                });
        Placeholders.register(
                Identifier.of(Fuji.MOD_ID, "server_playtime"),
                (ctx, arg) -> {
                    PlayerSumUpPlaceholder playerSumUpPlaceholder = PlayerSumUpPlaceholder.ofServer();
                    return PlaceholderResult.value(Text.literal(String.valueOf(playerSumUpPlaceholder.playtime)));
                });

       /* events */
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PlayerSumUpPlaceholder.ofServer();
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
            PlayerSumUpPlaceholder.ofServer();
        }
    }
}
