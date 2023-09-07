package fun.sakurawald.config.configs;


@SuppressWarnings("ALL")
public class ConfigGSON {

    public Modules modules = new Modules();

    public class Modules {

        public ResourceWorld resource_world = new ResourceWorld();
        public NewbieWelcome newbie_welcome = new NewbieWelcome();
        public TeleportWarmup teleport_warmup = new TeleportWarmup();

        public class ResourceWorld {
            public long seed = 0L;
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
    }
}
