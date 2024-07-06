package io.github.sakurawald.config.model;


import com.mojang.authlib.properties.Property;
import io.github.sakurawald.config.annotation.Comment;
import io.github.sakurawald.module.initializer.command_alias.CommandAliasEntry;
import io.github.sakurawald.module.initializer.command_rewrite.CommandRewriteEntry;
import net.minecraft.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
@Comment("""
        Welcome to fuji-fabric 's offical documentation.
        This page is the `source code` used to generate the default `config.json` file.
                
        You can press `CTRL + F` keys in your keyboard, and search any `option key` that you want to konw.
                
        All the text inside `@Comment` are the documentation text, which describe the following field.
        """)
public class ConfigModel {

    public Common common = new Common();
    public Modules modules = new Modules();

    @Comment("""
            Common options for fuji, which influce all modules.
            """)
    public class Common {

        public Quartz quartz = new Quartz();

        @Comment("""
                Fuji use quartz library as scheduler, all the timer are managed by quartz.
                You can use quartz language to define when to trigger a job.
                See: https://www.freeformatter.com/cron-expression-generator-quartz.html
                """)
        public class Quartz {
            @Comment("""
                    The fuji logger level.
                    Options: OFF/FATAL/ERROR/WARN/INFO/DEBUG/TRACE/ALL
                                
                    Set to `OFF` to supress all the messages from fuji.
                    Set to `ALL` to display all the messages from fuji.
                    """)
            public String logger_level = "WARN";
        }

    }

    @Comment("""
            A module means a standalone unit to provide a purpose.
                
            All the module can work standalone, you can enable or disable any modules if you like.
                
            Some modules can work together to achive more purpose. 
            e.g. A module can provides some placeholders for other modules to use.
                        
            The following fields are used to define the structure of the configuration.
            Go ahead, scroll the page down, and see more...
            """)
    public class Modules {
        public ResourceWorld resource_world = new ResourceWorld();
        public NewbieWelcome newbie_welcome = new NewbieWelcome();
        public TeleportWarmup teleport_warmup = new TeleportWarmup();
        public MOTD motd = new MOTD();
        public FakePlayerManager fake_player_manager = new FakePlayerManager();
        public BetterInfo better_info = new BetterInfo();
        public CommandCooldown command_cooldown = new CommandCooldown();
        public TopChunks top_chunks = new TopChunks();
        public Chat chat = new Chat();
        public Skin skin = new Skin();
        public Back back = new Back();
        public Tpa tpa = new Tpa();
        public Works works = new Works();
        public WorldDownloader world_downloader = new WorldDownloader();
        public DeathLog deathlog = new DeathLog();
        public MainStats main_stats = new MainStats();
        public MultiObsidianPlatform multi_obsidian_platform = new MultiObsidianPlatform();
        public OpProtect op_protect = new OpProtect();
        public Pvp pvp = new Pvp();
        public FixPlayerListCME fix_player_list_cme = new FixPlayerListCME();
        public FixWhitelist fix_whitelist = new FixWhitelist();
        public CommandPermission command_permission = new CommandPermission();
        public Head head = new Head();
        public Profiler profiler = new Profiler();
        public CommandSpy command_spy = new CommandSpy();
        public Scheduler scheduler = new Scheduler();
        public BypassChatSpeed bypass_chat_speed = new BypassChatSpeed();
        public BypassMoveSpeed bypass_move_speed = new BypassMoveSpeed();
        public BypassMaxPlayerLimit bypass_max_player_limit = new BypassMaxPlayerLimit();
        public Config config = new Config();
        public Test test = new Test();
        public Hat hat = new Hat();
        public Fly fly = new Fly();
        public God god = new God();
        public Language language = new Language();
        public Reply reply = new Reply();
        public Afk afk = new Afk();
        public Suicide suicide = new Suicide();
        public CommandInteractive command_interactive = new CommandInteractive();
        public Heal heal = new Heal();
        public Feed feed = new Feed();
        public Repair repair = new Repair();
        public Seen seen = new Seen();
        public More more = new More();
        public Extinguish extinguish = new Extinguish();
        public Home home = new Home();
        public Ping ping = new Ping();
        public SystemMessage system_message = new SystemMessage();
        public EnderChest enderchest = new EnderChest();
        public Workbench workbench = new Workbench();
        public Enchantment enchantment = new Enchantment();
        public Anvil anvil = new Anvil();
        public GrindStone grindstone = new GrindStone();
        public StoneCutter stonecutter = new StoneCutter();

        @Comment("""
                This module adds another 3 worlds called `resource world`: resrouce_overworld, resource_nether, resource_the_end .
                                
                Use-case: you have 3 permanent-world which is boundary-limited, and you want to provides infinite
                resource for the newbie players, then you can use `resource world` while keeping the permanent-world.
                """)
        public class ResourceWorld {
            public boolean enable = false;
            public ResourceWorlds resource_worlds = new ResourceWorlds();

            @Comment("When to auto reset resource worlds")
            public String auto_reset_cron = "0 0 20 * * ?";

            @Comment("""
                    The seed for resource worlds: overworld, the_nether and the_end.
                                
                    You don't need to input this field, since the `seed` field will randomly generated and write every time resource worlds gets reset.
                    """)
            public long seed = 0L;

            @Comment("""
                    What dimension type of resource worlds do you want to add?
                    """)
            public class ResourceWorlds {
                public boolean enable_overworld = true;
                public boolean enable_the_nether = true;
                public boolean enable_the_end = true;
            }

        }

        @Comment("""
                This module customs your MOTD in server-list.
                """)
        public class MOTD {
            public boolean enable = false;

            @Comment("""
                    Fuji will randomly pick a motd each time the player refresh server list.
                    """)
            public List<String> descriptions = new ArrayList<>() {
                {
                    this.add("<gradient:#FF66B2:#FFB5CC>Pure Survival %version% / Up %uptime%H ❤ Discord Group PyzU7Q6unb</gradient><newline><gradient:#99CCFF:#BBDFFF>%playtime%\uD83D\uDD25 %mined%⛏ %placed%\uD83D\uDD33 %killed%\uD83D\uDDE1 %moved%\uD83C\uDF0D");
                }
            };
        }

        @Comment("""
                This module provides some jobs to trigger when a player is the first time to join the server. 
                """)
        public class NewbieWelcome {
            public boolean enable = false;

            public RandomTeleport random_teleport = new RandomTeleport();

            @Comment("Random teleport the newbie player, and set his bed location.")
            public class RandomTeleport {
                public int max_try_times = 32;
                public int min_distance = 5000;
                public int max_distance = 40000;
            }
        }

        @Comment("""
                This module adds a warmup cooldown before player-teleporatation.
                                
                The teleporatation will be cancelled if:
                1. the player runs to far.
                2. the player gets damage.
                                
                """)
        public class TeleportWarmup {
            public boolean enable = false;

            @Comment("The second to wait before the teleportation.")
            public int warmup_second = 3;

            @Comment("How far should we cancel the teleportation.")
            public double interrupt_distance = 1d;
        }

        @Comment("""
                Enable this module requires `carpet-fabric` mod installed.
                                
                This module provides some management for `fake-player` and `/player` command.
                """)
        public class FakePlayerManager {
            public boolean enable = false;

            @Comment("""
                    How many fake-player can each player spawn (in different time)? 
                                        
                    The tuple means (day_of_week, minutes_of_the_day, max_fake_player_per_player).
                    The range of day_of_week is [1,7].
                    The range of minutes_of_the_day is [0, 1440].
                                         
                    For example: (1, 0, 2) means if the days_of_week >= 1, and minutes_of_the_day >= 0, then the max_fake_player_per_player now is 2.
                                        
                    Besides, you can add multi rules, the rules are checked from up to down.
                    The first rule that matches current time will be used to decide the max_fake_player_per_player.
                    """)
            public ArrayList<List<Integer>> caps_limit_rule = new ArrayList<>() {
                {
                    this.add(List.of(1, 0, 2));
                }
            };

            @Comment("""
                    How long should we renew when a player issue command `/player renew`
                                
                    The command `/player renew` allows the player to manually renew all of his `fake-player`.
                                
                    If a fake-player don't gets renew, then it will expired and get killed.
                    """)
            public int renew_duration_ms = 1000 * 60 * 60 * 12;

            @Comment("""
                    The rule to transform the name of fake-player.
                                        
                    Use-case: add prefix or suffix for fake-player.
                    """)
            public String transform_name = "_fake_%name%";

            @Comment("""
                    Should we use local skin for fake-player? 
                                        
                    Enable this can prevent fetching skins from mojang official server each time the fake-player is spawned. 
                    This is mainly used in some network siatuation if your network to mojang official server is bad.
                    """)
            public boolean use_local_random_skins_for_fake_player = true;
        }

        @Comment("""
                Adds nbt query and entity query for carpet command `/info`.
                """)
        public class BetterInfo {
            public boolean enable = false;
        }

        @Comment("""
                This module provides a cooldown before each command the player issued.
                """)
        public class CommandCooldown {
            public boolean enable = false;

            @Comment("""
                    Use `regex language` to define issued command cooldown.
                                        
                    For each player, each command has its cooldown.
                    """)
            public HashMap<String, Long> command_regex_2_cooldown_ms = new HashMap<>() {
                {
                    this.put("rw tp (overworld|the_nether|the_end)", 120 * 1000L);
                    this.put("chunks\\s*", 60 * 1000L);
                    this.put("download\\s*", 120 * 1000L);
                }
            };
        }

        @Comment("""
                This module provides the `/chunks` command, which shows the `top laggy chunks` in the server. 
                                
                The output is a score list, each score means a chunk, and how laggy the chunk is. (Large score means more laggy)
                """)
        public class TopChunks {
            public boolean enable = false;

            public int rows = 10;
            public int columns = 10;

            @Comment("For a chunk, how much the radius used to search `the nearest player` around the chunk.")
            public int nearest_distance = 128;

            @Comment("""
                    Should we hide the chunk-position for a laggy-chunk?
                                        
                    Hide chunk location to avoid grief or privacy purpose. 
                    """)
            public boolean hide_location = true;

            @Comment("""
                    The dict to define how laggy a type(entity/entity_block) should be.
                                
                    For example, 
                    `this.put("entity.minecraft.zombie", 4);` means there are 15 zombies inside a chunk,
                    then the chunk gets score 15 * 4 = 60
                                
                    Any other types not inside the dict used the score defined for type `default`
                    """)
            public HashMap<String, Integer> type2score = new HashMap<>() {
                {
                    this.put("default", 1);
                    this.put("block.minecraft.chest", 1);
                    this.put("block.minecraft.trapped_chest", 2);
                    this.put("block.minecraft.barrel", 1);
                    this.put("block.minecraft.furnace", 3);
                    this.put("block.minecraft.blast_furnace", 4);
                    this.put("block.minecraft.smoker", 3);
                    this.put("block.minecraft.jukebox", 35);
                    this.put("block.minecraft.beacon", 35);
                    this.put("block.minecraft.conduit", 40);
                    this.put("block.minecraft.hopper", 8);
                    this.put("block.minecraft.piston", 10);
                    this.put("block.minecraft.dispenser", 10);
                    this.put("block.minecraft.dropper", 10);
                    this.put("block.minecraft.comparator", 5);
                    this.put("block.minecraft.daylight_detector", 25);
                    this.put("block.minecraft.beehive", 15);
                    this.put("block.minecraft.mob_spawner", 100);
                    this.put("entity.minecraft.player", 15);
                    this.put("entity.minecraft.falling_block", 10);
                    this.put("entity.minecraft.zombie", 4);
                    this.put("entity.minecraft.zombie_villager", 8);
                    this.put("entity.minecraft.zombified_piglin", 5);
                    this.put("entity.minecraft.zoglin", 8);
                    this.put("entity.minecraft.ravager", 80);
                    this.put("entity.minecraft.pillager", 20);
                    this.put("entity.minecraft.evoker", 20);
                    this.put("entity.minecraft.vindicator", 20);
                    this.put("entity.minecraft.vex", 20);
                    this.put("entity.minecraft.piglin", 2);
                    this.put("entity.minecraft.drowned", 2);
                    this.put("entity.minecraft.guardian", 6);
                    this.put("entity.minecraft.spider", 2);
                    this.put("entity.minecraft.skeleton", 2);
                    this.put("entity.minecraft.creeper", 3);
                    this.put("entity.minecraft.endermite", 5);
                    this.put("entity.minecraft.enderman", 4);
                    this.put("entity.minecraft.wither", 55);
                    this.put("entity.minecraft.villager", 25);
                    this.put("entity.minecraft.sheep", 5);
                    this.put("entity.minecraft.cow", 3);
                    this.put("entity.minecraft.mooshroom", 3);
                    this.put("entity.minecraft.chicken", 3);
                    this.put("entity.minecraft.panda", 5);
                    this.put("entity.minecraft.wolf", 8);
                    this.put("entity.minecraft.cat", 8);
                    this.put("entity.minecraft.bee", 15);
                    this.put("entity.minecraft.boat", 5);
                    this.put("entity.minecraft.chest_boat", 5);
                    this.put("entity.minecraft.item_frame", 3);
                    this.put("entity.minecraft.glow_item_frame", 3);
                    this.put("entity.minecraft.armor_stand", 10);
                    this.put("entity.minecraft.item", 10);
                    this.put("entity.minecraft.experience_orb", 3);
                    this.put("entity.minecraft.tnt", 70);
                    this.put("entity.minecraft.hopper_minecart", 20);
                }
            };
        }

        @Comment("""
                This module provides chat custom.
                """)
        public class Chat {
            public boolean enable = false;

            @Comment("""
                    The server chat format for all players.
                                
                    You can use `minimessage language` to define complex format.
                    See: https://docs.advntr.dev/minimessage/format.html
                                
                    """)
            public String format = "<#B1B2FF>[%playtime%\uD83D\uDD25 %mined%⛏ %placed%\uD83D\uDD33 %killed%\uD83D\uDDE1 %moved%\uD83C\uDF0D]<reset> <<dark_green><click:suggest_command:/msg %player% ><hover:show_text:\"Time: <date:'yyyy-MM-dd HH:mm:ss'><newline><italic>Click to Message\">%player%</hover></click></dark_green>> %message%";

            public MentionPlayer mention_player = new MentionPlayer();
            public History history = new History();
            public Display display = new Display();

            public class History {
                @Comment("How many chat components should we save, so that we can send for a new-join player.")
                public int cache_size = 50;
            }

            @Comment("""
                    If you insert `@Steve` in chat message, then the player named `Steve` will get audio mention.
                    """)
            public class MentionPlayer {
                public String sound = "entity.experience_orb.pickup";
                public float volume = 100f;
                public float pitch = 1f;
                public int repeat_count = 3;
                public int interval_ms = 1000;
            }

            @Comment("""
                    You can insert `item`, `inv` and `ender` in message to display something with other players.
                    """)
            public class Display {

                @Comment("""
                        For each display data, how long should we save in the memory.
                        Note that if a player shares its inventory items, then fuji will save a copy of his inventory data in the memory.
                        """)
                public int expiration_duration_s = 3600;
            }
        }

        @Comment("""
                This module provides player skin management.
                """)
        public class Skin {
            public boolean enable = false;

            @Comment("The `default skin` used for player who has no skin set.")
            public Property default_skin = new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODYzMjc4ODA1NjYsInByb2ZpbGVJZCI6ImI3MzY3YzA2MjYxYzRlYjBiN2Y3OGY3YzUxNzBiNzQ4IiwicHJvZmlsZU5hbWUiOiJFbXB0eUlyb255Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NWZmZjI1ZDY2NzIwNmYyZTQ2ZDQ0MmNmMzU4YjNmMWVjMzYxMzgzOTE3NTFiYTZlZGY5NjVmZmM4M2I4NjAzIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=",
                    "PoUf4TsNx6SVHTWZJ6Iwk3acWyiDk84VeKBVcOeqimaSBAGYKfeuXRTFV8c9IBE9cjsRAVaTGC/mwRfHlcD/rmxeDDOkhsFVidr8UL+91afIO8d+EnyoBghmnbZonqpcjCv+nkxQ5SP93qTDelD3jd8xF1FAU97BBvrx0yK+QNn5rPg2RUGGoUZUg75KlEJds1dNftpHc8IyAHz/FQIywlkohu26ghOqFStjok4WPHD3ok0z7Kwcjk7u58PYf67TkEGnGbmxTUDlNbLmxUqjxCr4NshS+e3y3jRfJN0nP82dbYh/NP2Fx8m7pSMsQtm/Ta2MN7JC0Pm2yvZB/APNoNHVSZZ2SOITbPF/yAkIdHrk+ieCKqDbeuc8TFs2n+6FktYdwPXcqrK266CzlSTPycVZQeyrgrOI+fqU1HwCz+MgdlcsAdAoyuFlFPaVqDesI46YPsSJzA3C3CNhjvuebOn357U9Po82eSFAPYbtBPVNjiNgiqn5l+1x8ZVHImwpGv/toa5/fUyfMmlxijwG/C9gQ4mE+buutMn9nfE1y/AisU/2DWeFBESw3eRAICcmVVi875N8kT+Wja8WsbpDCw+pV2wZC3x3nEdOceAdXtDEb0oy3bQPW3TSZ+Wnp68qwSxjI/aDosqVuyyqqlm+w/irUmNHGL+t7g/kD932g0Q=");

            @Comment("Random skin for fake-player, if you enable the local skin for fake-player. See: FakePlayerManagerModule")
            public ArrayList<Property> random_skins = new ArrayList<>() {
                {
                    this.add(new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODYzMjc4ODA1NjYsInByb2ZpbGVJZCI6ImI3MzY3YzA2MjYxYzRlYjBiN2Y3OGY3YzUxNzBiNzQ4IiwicHJvZmlsZU5hbWUiOiJFbXB0eUlyb255Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NWZmZjI1ZDY2NzIwNmYyZTQ2ZDQ0MmNmMzU4YjNmMWVjMzYxMzgzOTE3NTFiYTZlZGY5NjVmZmM4M2I4NjAzIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=", "PoUf4TsNx6SVHTWZJ6Iwk3acWyiDk84VeKBVcOeqimaSBAGYKfeuXRTFV8c9IBE9cjsRAVaTGC/mwRfHlcD/rmxeDDOkhsFVidr8UL+91afIO8d+EnyoBghmnbZonqpcjCv+nkxQ5SP93qTDelD3jd8xF1FAU97BBvrx0yK+QNn5rPg2RUGGoUZUg75KlEJds1dNftpHc8IyAHz/FQIywlkohu26ghOqFStjok4WPHD3ok0z7Kwcjk7u58PYf67TkEGnGbmxTUDlNbLmxUqjxCr4NshS+e3y3jRfJN0nP82dbYh/NP2Fx8m7pSMsQtm/Ta2MN7JC0Pm2yvZB/APNoNHVSZZ2SOITbPF/yAkIdHrk+ieCKqDbeuc8TFs2n+6FktYdwPXcqrK266CzlSTPycVZQeyrgrOI+fqU1HwCz+MgdlcsAdAoyuFlFPaVqDesI46YPsSJzA3C3CNhjvuebOn357U9Po82eSFAPYbtBPVNjiNgiqn5l+1x8ZVHImwpGv/toa5/fUyfMmlxijwG/C9gQ4mE+buutMn9nfE1y/AisU/2DWeFBESw3eRAICcmVVi875N8kT+Wja8WsbpDCw+pV2wZC3x3nEdOceAdXtDEb0oy3bQPW3TSZ+Wnp68qwSxjI/aDosqVuyyqqlm+w/irUmNHGL+t7g/kD932g0Q="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyMDIyMDc4MTQyNCwKICAicHJvZmlsZUlkIiA6ICJiYjdjY2E3MTA0MzQ0NDEyOGQzMDg5ZTEzYmRmYWI1OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJsYXVyZW5jaW8zMDMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVmMTcyMGM3ODBhNzk1OGI0MWYxNTNlNTA2OWRiNjg2MWJkMjgxYmU0MzJlN2JjNzk0MTE0YTdmNGVjNTJmZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "olnQih89nZxKFe0UzWiXU8+wlndGBClUqXxAabwEm6j15SZH9ue7Xd2OM2kRKBiHoqbT+2TSg4xG7cBeAeapVN4vpRP5NPujERl/JI41jYNhMb+DmskreS59fh0QfZPAxOpj/rmmAJVfNN1QblxRM3wlMGaEgS5TH9HfeehgLrBaaDM8/JAgnas4Yh6L0uRoNebjXHrhqgguVBMF3xsWpvpAPCzQCYX2vjCCF3WtOEy7EEUF4u5Lo4teQhr9yfnYGBc/ktE4I0MByqTaKrLqvF45n4jOShPP0RcmLh9JpOXyrScRuaUDhQ8bd8xhkWEb94HMzwznvDLNh1/nbNybCMb5GydYf51hJVfqjU5TMWID71F8FTTBJrCZDBRESFIP+QZ3czYP+urgzmfLgDmcoPIukMaHWLU6qFpTF0QazAgF4u5Fe4J6QEZSyZz0B2kqQG3vN1dXxLgHItjQbEeceChNYNjuZFOTleXzpYkg5/4Zqy6Oek3bMscTYY7IPBV56WiO8eGw5JYMfyDeM3iyh4ZxLEC3HDRtOTBHo7WxWPR/AUOU9HP9CdmKQbGThGAUuqlqRJzbg5XNRvKIcnngI329VZV5RmAnt+G5Vfy6uqBagpMQZ3720PXPG6H5q4SBuXmHt1ccKgJvQv9lTh20EymuIALTnCodr8qDbnRfdrI="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYzODY4MjMwMjQ3MSwKICAicHJvZmlsZUlkIiA6ICI0ZjU2ZTg2ODk2OGU0ZWEwYmNjM2M2NzRlNzQ3ODdjOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE0YjRlODI2OTJiOWRjYmY2YzEyOTJkNjJiNzMxN2MxMjRjNGMzOTQ2Y2FiMjFlY2E4NDJiNDJlNzBmMGMwMjIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "renIrkVFQUor3+5AeDYOcGtUnUpdk9/i5ANWFGCBTSVZjeKQ/t5xDEe9kqwsWawR/55N2+1Db2c7lIpHpJ4cGqtG7BzTm+TZNUgSOu0rG27DwxheiuGbYSMm/lQSiNi7FvRlhLXuxsYZ0nHhXKoeG4xW5PXaE/zjXeXR1hffnfR/ROanmK/m2nIbkfPo59wjc+ZTF3nxhX+tGay+7dy/Y6xqhyZ4ZnM1a9+z8hC8ERgXzUUczfhRaDPQcv9dEdpyQhlfJyEV6r6NBSpBVVNaZ2bGs+VyxrRVtr/nXigps1KtFXH3j+gBiNYJWu7LpDS+1DTezlP9qkbDUPSKuO1O913GDRdJxdcVn7HGYD3W6yGB0r6sDBvb7RYESMzafRIFbBjhJrJFi3/aQjxTuFSc66bUkDqNBGYQcXyUXP1wEuB22mwQABv2OZiFdXMMRDniSZvPsxoriDdAS+umHcrAgTApu13xLyJJa8tFBD9rpGxDDoUbnNJdzZSpjrgfu38Kgpa3pW45HY21eSOubQNdz7qBTBmQwVViuVoAqH9mM/HqeIrGzwdRJaOH3GsZRofr4zh9HVc+5o02W72d39BskA56ae8zjGza9sF1jhkgiaW1NH/zuu7LnfujjvvMcczrddv8P1r7yqsIwUrP0ObB+ylsCsrb6mAV5uqXuklS7e4="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY0Nzg5MjUwMzUyMywKICAicHJvZmlsZUlkIiA6ICIxN2Q0ODA1ZDRmMTA0YTA5OWRiYzJmNzYzMDNjYmRkZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJnaWZ0bWV0b25uZXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ4MDU0OWEyNTJjYzI4ODJhNDUxMDc4NTUxMTNhMTg0NzJlNmI1YTVjNjE2YjI4NTc2Yjk4MjVjNDUzZGMzZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "LtRvG/OteephxnjkSQfrdfbDUWUhoG1p4L2pIit1e7KF0UV/5BjgzfQUt+MXRrmJTGQVoJCZzHt+VFHT9UL1ptguabvM6sbBWaVmg7RPo1KKBYkmai+dP5ceCXPA2StVogKN+JjRwNF6Paw850IVha6h90It2VRk43/IbMzdSyTnMIH92WLb93BvENYX00yRCJY/m48tYECibH6FtX6vgqK6UgOgNVqW0g6Otuwx2Z4Pi0xUHn6i9gCayPrWSg6Y7cVQ5pM49t1A9tfN+Kt4J+sB63Ez0LSws3y/5MP/8sFUBQXKaZYpATK5dmBohZao9wroX9Ni9sADzPcGF3XAcHXZjRr2Qk6Y0AsukuAlXMPlDWFGxnVK+jElwcGEsx8g8cK5iufAuDAQLQSUJ6vgBUE8DndEfL+l8LpebRlgMT4kZIn75ZbhY4BU2zvs/fUp3mPhQjjkrTMqwofDFj6YxnZksUi+qwEIcRc3ysoFl6phMN5n2mRV6616l6DTSh6y7Vd6tT4s6UsfWlFUwC0gdaPrU0CSX96Afk+BSVceh6qxs0IJVBn1bBe0uDTwK1a3yUOMXvjosG0L8jzpNY7sGE6ybwHUxPMaNIilqvnhO2NdSgsNHVLlYHzh0jUMs5ap4U+9fAEYYr0OpkqgZ7l6aQeV4ME96RrGqZmEkxJPjas="));
                    this.add(new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODc4MDcxMTkyNzEsInByb2ZpbGVJZCI6IjIzZjFhNTlmNDY5YjQzZGRiZGI1MzdiZmVjMTA0NzFmIiwicHJvZmlsZU5hbWUiOiIyODA3Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iMjI4MTk0OWZlZDc2M2Q3OWYwODZiNGU4MjE0ZGVjNTdiZDM4NzgzODhkOTJmYWQ4NmRjMzQxNzE2MWNkYjJkIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=", "N/b/lSK6Y3Wqm4lTj47YHbec9yVAj7XmDjfWhVAa033UNA30U8o+2pTY0aVDAzFut624iC3xjqMzBlXt7SczsT0w8EV+MnW51V6aPlanj5SQ6zVwB20TdhhAzBNvIQbvo4x4BL99ZpyBJMBRcCVEehjaD3rgshBxH6t2z7WzzYM1cij/5egedjhm8ek8DMdYYakN6DWIOWDv05VQSiWRMhitSI2sqJMTYKaJcLph7/56Ke5zRNtA2mwEcdB+GnDPkeEINzx3A0WG/vOS3iYL8L4T5Dv1GzBlq9s10R1K4Ks5TQLhVJ4Rp2S4COLvvWsgREHQVf6NEIOG2ww4wqTi/xmHni2d6TM9K+vtLSBE7umEvLeOzp8oqbQvtD1ipa0iatR8lEXU1bcGITtwZi+i+zLeOIfx2592XevcOGwTuvhBBM53rN5suLnpcGFIT5TuOQrFinT1+vXoE2D/UkDll8nvtGzJyqFgSSFDrvf0e6ZkbFlIQRoJGkfhnDLON2aEycOe9EcD+NiLDXQc9++j+3Kl5QFyze3xd21+ConIZRGDXKqvoEhfp1ovR7ND76IVOAoGMcDT4N+n+NWdXIilipux3gQ5UZkALw1ocFzhEZY9pCYw9e7XGQRh27N/RYns+sSI1qXbtBbl0FCl7X5efvsJLWId0JuEag5f5RAYYYo="));
                }
            };
        }

        @Comment("""
                This module provides `/back` command.
                """)
        public class Back {
            public boolean enable = false;

            @Comment("If the player's teleportation destination is close enough, we ignore this telepotation.")
            public double ignore_distance = 32d;
        }

        @Comment("""
                This module provides `/tpa`, `/tpahere`, `/tpaacept` and `/tpadeny` commands.
                """)
        public class Tpa {
            public boolean enable = false;

            @Comment("Tpa request expiration duration. unit is second")
            public int timeout = 300;

        }

        @Comment("""
                This module provides `/work` command.
                                
                A `work` means a project (a building, a red-stone device ...) that crafted by a player.
                                
                All `work` types:
                1. Non-production work: the project don't produce any resource (e.g. bone, string, coal).
                2. Production work: the project produce some resource.
                """)
        public class Works {
            public boolean enable = false;

            @Comment("For a production-work, how long should we sample it ?")
            public int sample_time_ms = 60 * 1000 * 60;
            @Comment("For a production-work, how large the radius should we considered as the work's production")
            public int sample_distance_limit = 512;
            @Comment("For a production-work, we only display the topN output items")
            public int sample_counter_top_n = 20;
        }

        @Comment("""
                This module provides `/download` command. 
                                
                This command allows to downlaod nearby chunks around a player.
                                
                Use-case: if a player wants to download his buildings, or just want to download the redstone-structure 
                so that he can debug in his single-player world.
                """)
        public class WorldDownloader {
            public boolean enable = false;

            @Comment("The url format used to broadcast")
            public String url_format = "http://example.com:%port%%path%";

            public int port = 22222;

            @Comment("Max download speed limit for each connection.")
            public int bytes_per_second_limit = 128 * 1000;

            @Comment("Max download request allowed in the memory at the same time.")
            public int context_cache_size = 5;
        }

        public class BypassChatSpeed {
            public boolean enable = false;
        }

        @Comment("Disable `moved too quickly` and `vehicle too quickly` check")
        public class BypassMoveSpeed {
            public boolean enable = false;
        }

        @Comment("""
                Bypass the max players limit of the server.
                """)
        public class BypassMaxPlayerLimit {
            public boolean enable = false;
        }

        @Comment("""
                This module provides `/deathlog` command.
                                
                Log player's inventory when he die, so that we can restore his inventory later.
                                
                """)
        public class DeathLog {
            public boolean enable = false;
        }

        @Comment("""
                This module provides some useful stats, you can use the stats placeholder with ChatModule and MotdModule
                """)
        public class MainStats {
            public boolean enable = false;
        }

        @Comment("""
                In vanilla minecraft, each `ender-portal` links to `the only one obsidian platform`.
                This module makes each `ender-portal` links to its own `obsidian platform`.
                                
                Use-case: you want more `obsidian platform` for your redstone-struture.
                """)
        public class MultiObsidianPlatform {
            public boolean enable = false;
            @Comment("""
                    The coordination-convertion factor between overworld and the_end.
                    In vanilla minecraft, the factor between overworld and the_nether is 8.""")
            public double factor = 4;
        }

        public class OpProtect {
            public boolean enable = false;
        }

        @Comment("""
                This module provides `/pvp` command. 
                """)
        public class Pvp {
            public boolean enable = false;
        }

        public class FixPlayerListCME {
            public boolean enable = false;
        }

        public class FixWhitelist {

            public boolean enable = false;
        }

        public class CommandPermission {
            public boolean enable = false;
        }

        @Comment("""
                This module provides `/head` command, so that players can buy custom-head.
                """)
        public class Head {

            public boolean enable = false;
        }

        @Comment("""
                Enable this module requires `spark` mod installed.
                                
                This module provides `/profiler` command.
                """)
        public class Profiler {

            public boolean enable = false;
        }

        public class CommandSpy {
            public boolean enable = false;
        }

        @Comment("""
                This module provides scheduler for auto-run jobs, and `/schudler_trigger` command.
                                
                """)
        public class Scheduler {
            public boolean enable = false;
        }

        @Comment("This module provides `/fuji reload` command, so that you can reload modules in game.")
        public class Config {
            public boolean enable = false;
        }

        public class Test {
            // disable TestModule by default
            public boolean enable = false;
        }

        @Comment("This module provides `/hat` command.")
        public class Hat {
            public boolean enable = false;
        }

        @Comment("This module provides `/fly` command.")
        public class Fly {
            public boolean enable = false;
        }

        @Comment("This module provides `/god` command.")
        public class God {
            public boolean enable = false;
        }

        @Comment("""
                If this module is enabled, then `/fuji reload` command will reload the files inside `/config/fuji/lang`.
                """)
        public class Language {
            public boolean enable = false;
        }

        @Comment("This module provides `/reply` command.")
        public class Reply {
            public boolean enable = false;
        }

        @Comment("""
                This module provides afk detection.
                
                If a player is idle long enough, he will be marked as afk state.
                A afk player will display in `tab list`.
                
                A player can issue `/afk` command to afk manually.
                """)
        public class Afk {

            public boolean enable = false;
            @Comment("The tab-name format when a player is afk")
            public String format = "<gray>[AFK] <reset>%player_display_name%";

            @Comment("The afk checker is a timer to check and mark the player's recently active time.")
            public AfkChecker afk_checker = new AfkChecker();

            public class AfkChecker {
                @Comment("The cron to define how the afk_checker is triggered.")
                public String cron = "0 0/5 * ? * *";
                @Comment("Should we kick a player if he is afk ?")
                public boolean kick_player = false;
            }
        }

        @Comment("This module provides `/suicide` command.")
        public class Suicide {

            public boolean enable = false;
        }

        @Comment("""
                This module allows you to write commands in `sign block`.
                
                A sign-block contains 4 lines.
                You can write commands in the sign-block.
                
                Each command starts with prefix `//`
                e.g. Line 1 contains `Click me //back` will execute the command `/back`
                
                The placeholder `@u` means the user player name.
                e.g. Line 1 contains `//kill @u` will execute the command `/kill {player_name}`
                 
                """)
        public class CommandInteractive {

            public boolean enable = false;

            @Comment("Should we log the command used by signs into the console ?")
            public boolean log_use = true;

        }

        @Comment("This module provides `/heal` command.")
        public class Heal {
            public boolean enable = false;
        }

        @Comment("This module provides `/feed` command.")
        public class Feed {
            public boolean enable = false;
        }

        @Comment("This module provides `/repair` command.")
        public class Repair {
            public boolean enable = false;

        }

        @Comment("This module provides `/seen` command.")
        public class Seen {
            public boolean enable = false;
        }

        @Comment("This module provides `/more` command.")
        public class More {
            public boolean enable = false;
        }

        @Comment("This module provides `/extinguish` command.")
        public class Extinguish {
            public boolean enable = false;
        }

        @Comment("This module provides `/home` command.")
        public class Home {
            public boolean enable = false;
            public int max_homes = 3;
        }

        @Comment("This module provides `/ping` command.")
        public class Ping {
            public boolean enable = false;
        }

        @Comment("""
        This module allows you to custom every system-message defined by mojang in `./assets/minecraft/lang/en_us.json`
        
        The mojang offical en_us.json file may looks like: [en_us.json for minecraft 1.21](https://github.com/sakurawald/fuji-fabric/blob/dev/.github/files/en_us.json)
        
        """)
        public class SystemMessage {
            public boolean enable = false;

            @Comment("The language keys to hijack.")
            public Map<String, String> key2value = new HashMap<>() {
                {
                    this.put("multiplayer.player.joined", "<rainbow>+ %s");
                    this.put("commands.seed.success", "<rainbow> Seeeeeeeeeeed: %s");
                    this.put("multiplayer.disconnect.not_whitelisted", "<rainbow>Please apply a whitelist first!");
                    this.put("death.attack.explosion.player", "<rainbow>%1$s booooooom because of %2$s");
                }
            };
        }

        @Comment("This module provides `/enderchest` command.")
        public class EnderChest {
            public boolean enable = false;
        }

        @Comment("This module provides `/workbench` command.")
        public class Workbench {
            public boolean enable = false;
        }

        @Comment("This module provides `/enchantment` command.")
        public class Enchantment {

            public boolean enable = false;

            @Comment("Should we override the power of proviers for the opened enchant table?")
            public OverridePower override_power = new OverridePower();

            public class OverridePower {

                public boolean enable = true;
                @Comment("""
                        How many power providers for the opened enchant table.
                        For a max level of enchant table, it requires 15 power providers.""")
                public int power_provider_amount = 15;
            }

        }

        @Comment("This module provides `/anvil` command.")
        public class Anvil {

            public boolean enable = false;
        }

        @Comment("This module provides `/grindstone` command.")
        public class GrindStone {

            public boolean enable = false;
        }

        @Comment("This module provides `/stonecutter` command.")
        public class StoneCutter {

            public boolean enable = false;
        }

        @Comment("This module provides `/bed` command, which teleports the player to his bed.")
        public Bed bed = new Bed();

        public class Bed {

            public boolean enable = false;
        }

        @Comment("""
                This module provides `/sit` command, and the ability to sit by right-click any chair.
                """)
        public Sit sit = new Sit();

        public class Sit {

            public boolean enable = false;
            public boolean allow_right_click_sit = true;
            public int max_distance_to_sit = -1;
            public boolean must_be_stairs = true;
            public boolean required_empty_hand = false;
            public boolean allow_sneaking = false;
            public boolean no_opaque_block_above = false;
        }

        public CommandAlias command_alias = new CommandAlias();

        @Comment("""
                This module provides command alias.
                
                An alias means we redirect a command-node into another command-node.
                The requirement of comamnd-node is extended.
                """)
        public class CommandAlias {
            public boolean enable = false;
            public List<CommandAliasEntry> alias = new ArrayList<>() {
                {
                    this.add(new CommandAliasEntry(List.of("r"), List.of("reply")));
                    this.add(new CommandAliasEntry(List.of("i", "want", "to", "modify", "chat"), List.of("chat", "format")));
                }
            };
        }

        public CommandRewrite command_rewrite = new CommandRewrite();

        @Comment("""
                This module provides command rewrite, so that you can rewrite the `command line` a player issued.
                """)
        public class CommandRewrite {
            public boolean enable = false;
            public List<CommandRewriteEntry> rules = new ArrayList<>() {
                {
                    this.add(new CommandRewriteEntry("home", "home tp default"));
                }
            };

        }

    }
}
