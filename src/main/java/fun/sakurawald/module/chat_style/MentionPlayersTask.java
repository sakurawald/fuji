package fun.sakurawald.module.chat_style;

import fun.sakurawald.ServerMain;
import fun.sakurawald.config.ConfigGSON;
import fun.sakurawald.config.ConfigManager;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("PatternValidation")
public class MentionPlayersTask implements Runnable {

    private final ArrayList<ServerPlayer> players;
    private final Sound sound;
    private final int limit;
    private final int interval;


    @Setter
    private ScheduledFuture<?> scheduledFuture;
    private int current = 0;

    public MentionPlayersTask(ServerPlayer serverPlayer) {
        this(new ArrayList<>(Collections.singletonList(serverPlayer)));
    }

    public MentionPlayersTask(ArrayList<ServerPlayer> players) {
        this.players = players;
        ConfigGSON.Modules.ChatStyle.MentionPlayer mentionPlayer = ConfigManager.configWrapper.instance().modules.chat_style.mention_player;
        this.sound = Sound.sound(Key.key(mentionPlayer.sound), Sound.Source.MUSIC, mentionPlayer.volume, mentionPlayer.pitch);
        this.interval = mentionPlayer.interval;
        this.limit = mentionPlayer.limit;
    }

    public void startTask() {
        ScheduledFuture<?> scheduledFuture = ServerMain.getSCHEDULED_EXECUTOR_SERVICE().scheduleAtFixedRate(this, 0, this.interval, TimeUnit.MILLISECONDS);
        this.setScheduledFuture(scheduledFuture);
    }

    @Override
    public void run() {
        if (++current > limit) {
            scheduledFuture.cancel(true);
            return;
        }

        for (ServerPlayer player : players) {
            if (player == null) continue;
            player.playSound(sound);
        }
    }
}
