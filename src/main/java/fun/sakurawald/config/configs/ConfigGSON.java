package fun.sakurawald.config.configs;


@SuppressWarnings("ALL")
public class ConfigGSON {

    public Modules modules = new Modules();

    public class Modules {

        public ResourceWorld resource_world = new ResourceWorld();
        public NewbieWelcome newbie_welcome = new NewbieWelcome();
        public TeleportWarmup teleport_warmup = new TeleportWarmup();
        public CustomStats custom_stats = new CustomStats();
        public ChatHistory chat_history = new ChatHistory();

        public class ResourceWorld {
            public long seed = 0L;
        }

        public class CustomStats {
            public String dynamic_motd = "\u00a7l  \u00a72   Pure Survival 1.20.1 \u00a7c\u2764 \u00a76QQ Group 912363929\n    §b%server_playtime%\uD83D\uDD25 %server_mined%⛏ %server_placed%\uD83D\uDD33 %server_killed%\uD83D\uDDE1 %server_moved%\uD83C\uDF0D";
        }

        public class NewbieWelcome {
            public String welcome_message = "§dWelcome new player %player% to join us!";
            public RandomTeleport random_teleport = new RandomTeleport();

            public class RandomTeleport {
                public int max_try_times = 32;
                public int min_distance = 5000;
                public int max_distance = 40000;
            }
        }

        public class TeleportWarmup {
            public int warmup_second = 3;
            public double interrupt_distance = 1d;
            public String bossbar_name = "Teleport";
            public String in_progress_message = "§bYou have a teleportation in progress.";
            public String in_combat_message = "§bYou can't teleport while in combat.";
        }

        public class ChatHistory {
            public int max_history = 30;
        }


        public BetterFakePlayer better_fake_player = new BetterFakePlayer();
        public class BetterFakePlayer {
            public int max_fake_player_limit = 2;
        }
    }
}
