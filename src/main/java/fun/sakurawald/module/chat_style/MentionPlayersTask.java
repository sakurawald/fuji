package fun.sakurawald.module.chat_style;

import lombok.Setter;
import net.kyori.adventure.sound.Sound;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

public class MentionPlayersTask implements Runnable {

    private final ArrayList<ServerPlayer> players;
    private final Sound sound;
    private final int limit;

    @Setter
    private ScheduledFuture<?> scheduledFuture;
    private int current = 0;

    public MentionPlayersTask(ArrayList<ServerPlayer> players, Sound sound, int limit) {
        this.players = players;
        this.sound = sound;
        this.limit = limit;
    }

    @Override
    public void run() {
        current++;
        if (current > limit) {
            scheduledFuture.cancel(true);
            return;
        }

        for (ServerPlayer player : players) {
            player.playSound(sound);
        }
    }
}
