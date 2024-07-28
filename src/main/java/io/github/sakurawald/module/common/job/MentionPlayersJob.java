package io.github.sakurawald.module.common.job;

import io.github.sakurawald.config.Configs;
import io.github.sakurawald.config.model.ConfigModel;
import io.github.sakurawald.module.common.manager.Managers;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("PatternValidation")
public class MentionPlayersJob implements Job {

    public static void scheduleJob(ArrayList<ServerPlayerEntity> players) {
        ConfigModel.Modules.Chat.MentionPlayer mentionPlayer = Configs.configHandler.model().modules.chat.mention_player;
        int intervalMs = mentionPlayer.interval_ms;
        int repeatCount = mentionPlayer.repeat_count;
        Sound sound = Sound.sound(Key.key(mentionPlayer.sound), Sound.Source.MUSIC, mentionPlayer.volume, mentionPlayer.pitch);
        Managers.getScheduleManager().scheduleJob(MentionPlayersJob.class, null, null, intervalMs, repeatCount, new JobDataMap() {
            {
                this.put(ArrayList.class.getName(), players);
                this.put(Sound.class.getName(), sound);
            }
        });
    }

    public static void scheduleJob(ServerPlayerEntity serverPlayer) {
        scheduleJob(new ArrayList<>(Collections.singletonList(serverPlayer)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(@NotNull JobExecutionContext context) {
        ArrayList<ServerPlayerEntity> players = (ArrayList<ServerPlayerEntity>) context.getJobDetail().getJobDataMap().get(ArrayList.class.getName());
        Sound sound = (Sound) context.getJobDetail().getJobDataMap().get(Sound.class.getName());
        for (ServerPlayerEntity player : players) {
            if (player == null) continue;
            player.playSound(sound);
        }
    }
}
