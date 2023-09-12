package fun.sakurawald.config;


import net.fabricmc.tinyremapper.extension.mixin.common.data.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("ALL")
public class ConfigGSON {

    public Modules modules = new Modules();

    public class Modules {

        public ResourceWorld resource_world = new ResourceWorld();
        public NewbieWelcome newbie_welcome = new NewbieWelcome();
        public TeleportWarmup teleport_warmup = new TeleportWarmup();
        public CustomStats custom_stats = new CustomStats();
        public ChatHistory chat_history = new ChatHistory();
        public BetterFakePlayer better_fake_player = new BetterFakePlayer();
        public CommandCooldown command_cooldown = new CommandCooldown();
        public TopChunks top_chunks = new TopChunks();
        public ChatStyle chat_style = new ChatStyle();

        public class ResourceWorld {
            public long seed = 0L;
        }

        public class CustomStats {
            public String dynamic_motd = "\u00a7l  \u00a72   Pure Survival 1.20.1 \u00a7c\u2764 \u00a76QQ Group 912363929\n    §b%playtime%\uD83D\uDD25 %mined%⛏ %placed%\uD83D\uDD33 %killed%\uD83D\uDDE1 %moved%\uD83C\uDF0D";
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

        public class BetterFakePlayer {
            public ArrayList<Pair<Integer, Integer>> time2limit = new ArrayList<Pair<Integer, Integer>>() {
                {
                    this.add(Pair.of(0, 2));
                    this.add(Pair.of(840, 1));
                    this.add(Pair.of(1080, 0));
                    this.add(Pair.of(1260, 2));
                }
            };
        }

        public class CommandCooldown {
            public HashMap<String, Long> command_regex_2_cooldown_ms = new HashMap<>() {
                {
                    this.put("rw tp (overworld|the_nether|the_end)", 120 * 1000L);
                }
            };
        }

        public class TopChunks {

            public int rows = 10;
            public int columns = 10;
            public int nearest_distance = 128;

            public boolean hide_location = true;
            public HashMap<String, Integer> type2score = new HashMap<>() {
                {
                    this.put("default", 1);
                    this.put(BlockEntityType.getKey(BlockEntityType.CHEST).getPath(), 1);
                    this.put(BlockEntityType.getKey(BlockEntityType.TRAPPED_CHEST).getPath(), 2);
                    this.put(BlockEntityType.getKey(BlockEntityType.FURNACE).getPath(), 3);
                    this.put(BlockEntityType.getKey(BlockEntityType.JUKEBOX).getPath(), 35);
                    this.put(BlockEntityType.getKey(BlockEntityType.BEACON).getPath(), 35);
                    this.put(BlockEntityType.getKey(BlockEntityType.HOPPER).getPath(), 8);
                    this.put(BlockEntityType.getKey(BlockEntityType.PISTON).getPath(), 10);
                    this.put(BlockEntityType.getKey(BlockEntityType.DISPENSER).getPath(), 10);
                    this.put(BlockEntityType.getKey(BlockEntityType.DROPPER).getPath(), 10);
                    this.put(BlockEntityType.getKey(BlockEntityType.BEEHIVE).getPath(), 15);
                    this.put(BlockEntityType.getKey(BlockEntityType.MOB_SPAWNER).getPath(), 100);
                    this.put(EntityType.getKey(EntityType.PLAYER).getPath(), 15);
                    this.put(EntityType.getKey(EntityType.FALLING_BLOCK).getPath(), 10);
                    this.put(EntityType.getKey(EntityType.ZOMBIE).getPath(), 4);
                    this.put(EntityType.getKey(EntityType.ZOMBIE_VILLAGER).getPath(), 8);
                    this.put(EntityType.getKey(EntityType.ZOMBIFIED_PIGLIN).getPath(), 5);
                    this.put(EntityType.getKey(EntityType.ZOGLIN).getPath(), 8);
                    this.put(EntityType.getKey(EntityType.PILLAGER).getPath(), 10);
                    this.put(EntityType.getKey(EntityType.RAVAGER).getPath(), 4);
                    this.put(EntityType.getKey(EntityType.EVOKER).getPath(), 5);
                    this.put(EntityType.getKey(EntityType.VEX).getPath(), 5);
                    this.put(EntityType.getKey(EntityType.PIGLIN).getPath(), 2);
                    this.put(EntityType.getKey(EntityType.DROWNED).getPath(), 2);
                    this.put(EntityType.getKey(EntityType.GUARDIAN).getPath(), 6);
                    this.put(EntityType.getKey(EntityType.SPIDER).getPath(), 2);
                    this.put(EntityType.getKey(EntityType.SKELETON).getPath(), 2);
                    this.put(EntityType.getKey(EntityType.CREEPER).getPath(), 3);
                    this.put(EntityType.getKey(EntityType.ENDERMITE).getPath(), 5);
                    this.put(EntityType.getKey(EntityType.ENDERMAN).getPath(), 4);
                    this.put(EntityType.getKey(EntityType.WITHER).getPath(), 55);
                    this.put(EntityType.getKey(EntityType.VILLAGER).getPath(), 25);
                    this.put(EntityType.getKey(EntityType.SHEEP).getPath(), 5);
                    this.put(EntityType.getKey(EntityType.COW).getPath(), 3);
                    this.put(EntityType.getKey(EntityType.MOOSHROOM).getPath(), 3);
                    this.put(EntityType.getKey(EntityType.CHICKEN).getPath(), 3);
                    this.put(EntityType.getKey(EntityType.PANDA).getPath(), 5);
                    this.put(EntityType.getKey(EntityType.WOLF).getPath(), 8);
                    this.put(EntityType.getKey(EntityType.CAT).getPath(), 8);
                    this.put(EntityType.getKey(EntityType.BEE).getPath(), 15);
                    this.put(EntityType.getKey(EntityType.BOAT).getPath(), 5);
                    this.put(EntityType.getKey(EntityType.CHEST_BOAT).getPath(), 5);
                    this.put(EntityType.getKey(EntityType.ITEM_FRAME).getPath(), 3);
                    this.put(EntityType.getKey(EntityType.GLOW_ITEM_FRAME).getPath(), 3);
                    this.put(EntityType.getKey(EntityType.ARMOR_STAND).getPath(), 10);
                    this.put(EntityType.getKey(EntityType.ITEM).getPath(), 2);
                    this.put(EntityType.getKey(EntityType.EXPERIENCE_ORB).getPath(), 3);
                    this.put(EntityType.getKey(EntityType.TNT).getPath(), 70);
                    this.put(EntityType.getKey(EntityType.HOPPER_MINECART).getPath(), 20);
                }
            };
        }

        public class ChatStyle {
            public String format = "<#B1B2FF>[%playtime%\uD83D\uDD25 %mined%⛏ %placed%\uD83D\uDD33 %killed%\uD83D\uDDE1 %moved%\uD83C\uDF0D]<reset> <<dark_green><click:suggest_command:/msg %player% ><hover:show_text:\"Time: <date:'yyyy-MM-dd HH:mm:ss'><newline><italic>Click to Message\">%player%</hover></click></dark_green>> %message%";
            public MentionPlayer mention_player = new MentionPlayer();

            public class MentionPlayer {
                public String sound = "entity.experience_orb.pickup";
                public float volume = 100f;
                public float pitch = 1f;

                public int limit = 5;
                public int interval = 1000;
            }
        }
    }
}
