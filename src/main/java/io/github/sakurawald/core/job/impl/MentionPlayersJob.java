package io.github.sakurawald.core.job.impl;

import io.github.sakurawald.core.job.abst.FixedIntervalJob;
import lombok.NoArgsConstructor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class MentionPlayersJob extends FixedIntervalJob {

    public MentionPlayersJob(JobDataMap jobDataMap, int intervalMs, int repeatCount) {
        super(null, null, jobDataMap, intervalMs, repeatCount);
    }

    public static void requestJob(MentionPlayer setup, List<ServerPlayerEntity> players) {
        int intervalMs = setup.interval_ms;
        int repeatCount = setup.repeat_count;
        Sound sound = Sound.sound(Key.key(setup.sound), Sound.Source.MUSIC, setup.volume, setup.pitch);

        new MentionPlayersJob(new JobDataMap() {
            {
                this.put(ArrayList.class.getName(), players);
                this.put(Sound.class.getName(), sound);
            }
        }, intervalMs, repeatCount).schedule();
    }

    public static void requestJob(MentionPlayer setup, ServerPlayerEntity serverPlayer) {
        requestJob(setup, new ArrayList<>(Collections.singletonList(serverPlayer)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(@NotNull JobExecutionContext context) {
        List<ServerPlayerEntity> players = (ArrayList<ServerPlayerEntity>) context.getJobDetail().getJobDataMap().get(ArrayList.class.getName());
        Sound sound = (Sound) context.getJobDetail().getJobDataMap().get(Sound.class.getName());
        for (ServerPlayerEntity player : players) {
            if (player == null) continue;
            player.playSound(sound);
        }
    }

    public static class MentionPlayer {
        public @NotNull String sound = "entity.experience_orb.pickup";
        public float volume = 100f;
        public float pitch = 1f;
        public int repeat_count = 3;
        public int interval_ms = 1000;
    }
}
