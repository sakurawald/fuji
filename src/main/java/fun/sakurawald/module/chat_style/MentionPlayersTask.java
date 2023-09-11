package fun.sakurawald.module.chat_style;

import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

public class MentionPlayersTask implements Runnable {

    private static final int LIMIT = 5;
    private static final Sound sound = Sound.sound(Key.key("block.note_block.bell"), Sound.Source.MUSIC, 100f, 1f);
    private final ArrayList<ServerPlayer> players;
    @Setter
    private ScheduledFuture<?> scheduledFuture;
    private int current = 0;

    public MentionPlayersTask(ArrayList<ServerPlayer> players) {
        this.players = players;
    }

    @Override
    public void run() {
        current++;
        if (current > LIMIT) {
            scheduledFuture.cancel(true);
            return;
        }

        for (ServerPlayer player : players) {
            player.playSound(sound);
        }
    }
}
