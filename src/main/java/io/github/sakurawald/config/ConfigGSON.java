package io.github.sakurawald.config;


import com.mojang.authlib.properties.Property;
import net.fabricmc.loader.api.FabricLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("ALL")
public class ConfigGSON {

    public Common common = new Common();
    public Modules modules = new Modules();

    public class Common {

        public Quartz quartz = new Quartz();

        public class Quartz {
            public String logger_level = "WARN";
        }

    }

    public class Modules {
        public ResourceWorld resource_world = new ResourceWorld();
        public NewbieWelcome newbie_welcome = new NewbieWelcome();
        public TeleportWarmup teleport_warmup = new TeleportWarmup();
        public MOTD motd = new MOTD();
        public BetterFakePlayer better_fake_player = new BetterFakePlayer();
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
        public PvpToggle pvp_toggle = new PvpToggle();
        public StrongerPlayerList stronger_player_list = new StrongerPlayerList();
        public WhitelistFix whitelist_fix = new WhitelistFix();
        public ZeroCommandPermission zero_command_permission = new ZeroCommandPermission();
        public Head head = new Head();
        public Profiler profiler = new Profiler();
        public CommandSpy command_spy = new CommandSpy();
        public Scheduler scheduler = new Scheduler();
        public BypassChatSpeed bypass_chat_speed = new BypassChatSpeed();
        public BypassMoveSpeed bypass_move_speed = new BypassMoveSpeed();
        public BypassMaxPlayerLimit bypass_max_player_limit = new BypassMaxPlayerLimit();
        public BiomeLookupCache biome_lookup_cache = new BiomeLookupCache();
        public TickChunkCache tick_chunk_cache = new TickChunkCache();
        public Config config = new Config();
        public Test test = new Test();
        public Hat hat = new Hat();
        public Fly fly = new Fly();
        public God god = new God();
        public Language language = new Language();
        public Reply reply = new Reply();
        public Afk afk = new Afk();

        public class ResourceWorld {
            public boolean enable = false;

            public ResourceWorlds resource_worlds = new ResourceWorlds();
            public String auto_reset_cron = "0 0 20 * * ?";
            public long seed = 0L;

            public class ResourceWorlds {
                public boolean enable_overworld = true;
                public boolean enable_the_nether = true;
                public boolean enable_the_end = true;
            }

        }

        public class MOTD {
            public boolean enable = false;
            public List<String> descriptions = new ArrayList<>() {
                {
                    this.add("<gradient:#FF66B2:#FFB5CC>Pure Survival %version% / Up %uptime%H ❤ Discord Group Uv5X8amt</gradient><newline><gradient:#99CCFF:#BBDFFF>%playtime%\uD83D\uDD25 %mined%⛏ %placed%\uD83D\uDD33 %killed%\uD83D\uDDE1 %moved%\uD83C\uDF0D");
                }
            };
        }

        public class NewbieWelcome {
            public boolean enable = false;
            public RandomTeleport random_teleport = new RandomTeleport();

            public class RandomTeleport {
                public int max_try_times = 32;
                public int min_distance = 5000;
                public int max_distance = 40000;
            }
        }

        public class TeleportWarmup {
            public boolean enable = false;
            public int warmup_second = 3;
            public double interrupt_distance = 1d;
        }

        public class BetterFakePlayer {
            public boolean enable = false;
            public ArrayList<List<Integer>> caps_limit_rule = new ArrayList<>() {
                {
                    this.add(List.of(1, 0, 2));
                }
            };

            public int renew_duration_ms = 1000 * 60 * 60 * 12;
            public String transform_name = "_fake_%name%";
            public boolean use_local_random_skins_for_fake_player = true;
        }

        public class BetterInfo {
            public boolean enable = false;
        }

        public class CommandCooldown {
            public boolean enable = false;
            public HashMap<String, Long> command_regex_2_cooldown_ms = new HashMap<>() {
                {
                    this.put("rw tp (overworld|the_nether|the_end)", 120 * 1000L);
                    this.put("chunks\\s*", 60 * 1000L);
                    this.put("download\\s*", 120 * 1000L);
                }
            };
        }

        public class TopChunks {
            public boolean enable = false;

            public int rows = 10;
            public int columns = 10;
            public int nearest_distance = 128;

            public boolean hide_location = true;
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

        public class Chat {
            public boolean enable = false;
            public String format = "<#B1B2FF>[%playtime%\uD83D\uDD25 %mined%⛏ %placed%\uD83D\uDD33 %killed%\uD83D\uDDE1 %moved%\uD83C\uDF0D]<reset> <<dark_green><click:suggest_command:/msg %player% ><hover:show_text:\"Time: <date:'yyyy-MM-dd HH:mm:ss'><newline><italic>Click to Message\">%player%</hover></click></dark_green>> %message%";
            public MentionPlayer mention_player = new MentionPlayer();

            public History history = new History();
            public Display display = new Display();

            public class History {
                public int cache_size = 50;
            }

            public class MentionPlayer {
                public String sound = "entity.experience_orb.pickup";
                public float volume = 100f;
                public float pitch = 1f;
                public int repeat_count = 3;
                public int interval_ms = 1000;
            }

            public class Display {

                public int expiration_duration_s = 3600;
            }
        }

        public class Skin {
            public boolean enable = false;

            public Property default_skin = new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODYzMjc4ODA1NjYsInByb2ZpbGVJZCI6ImI3MzY3YzA2MjYxYzRlYjBiN2Y3OGY3YzUxNzBiNzQ4IiwicHJvZmlsZU5hbWUiOiJFbXB0eUlyb255Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NWZmZjI1ZDY2NzIwNmYyZTQ2ZDQ0MmNmMzU4YjNmMWVjMzYxMzgzOTE3NTFiYTZlZGY5NjVmZmM4M2I4NjAzIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=",
                    "PoUf4TsNx6SVHTWZJ6Iwk3acWyiDk84VeKBVcOeqimaSBAGYKfeuXRTFV8c9IBE9cjsRAVaTGC/mwRfHlcD/rmxeDDOkhsFVidr8UL+91afIO8d+EnyoBghmnbZonqpcjCv+nkxQ5SP93qTDelD3jd8xF1FAU97BBvrx0yK+QNn5rPg2RUGGoUZUg75KlEJds1dNftpHc8IyAHz/FQIywlkohu26ghOqFStjok4WPHD3ok0z7Kwcjk7u58PYf67TkEGnGbmxTUDlNbLmxUqjxCr4NshS+e3y3jRfJN0nP82dbYh/NP2Fx8m7pSMsQtm/Ta2MN7JC0Pm2yvZB/APNoNHVSZZ2SOITbPF/yAkIdHrk+ieCKqDbeuc8TFs2n+6FktYdwPXcqrK266CzlSTPycVZQeyrgrOI+fqU1HwCz+MgdlcsAdAoyuFlFPaVqDesI46YPsSJzA3C3CNhjvuebOn357U9Po82eSFAPYbtBPVNjiNgiqn5l+1x8ZVHImwpGv/toa5/fUyfMmlxijwG/C9gQ4mE+buutMn9nfE1y/AisU/2DWeFBESw3eRAICcmVVi875N8kT+Wja8WsbpDCw+pV2wZC3x3nEdOceAdXtDEb0oy3bQPW3TSZ+Wnp68qwSxjI/aDosqVuyyqqlm+w/irUmNHGL+t7g/kD932g0Q=");

            public ArrayList<Property> random_skins = new ArrayList<>() {
                {
                    this.add(new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODYzMjc4ODA1NjYsInByb2ZpbGVJZCI6ImI3MzY3YzA2MjYxYzRlYjBiN2Y3OGY3YzUxNzBiNzQ4IiwicHJvZmlsZU5hbWUiOiJFbXB0eUlyb255Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NWZmZjI1ZDY2NzIwNmYyZTQ2ZDQ0MmNmMzU4YjNmMWVjMzYxMzgzOTE3NTFiYTZlZGY5NjVmZmM4M2I4NjAzIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=", "PoUf4TsNx6SVHTWZJ6Iwk3acWyiDk84VeKBVcOeqimaSBAGYKfeuXRTFV8c9IBE9cjsRAVaTGC/mwRfHlcD/rmxeDDOkhsFVidr8UL+91afIO8d+EnyoBghmnbZonqpcjCv+nkxQ5SP93qTDelD3jd8xF1FAU97BBvrx0yK+QNn5rPg2RUGGoUZUg75KlEJds1dNftpHc8IyAHz/FQIywlkohu26ghOqFStjok4WPHD3ok0z7Kwcjk7u58PYf67TkEGnGbmxTUDlNbLmxUqjxCr4NshS+e3y3jRfJN0nP82dbYh/NP2Fx8m7pSMsQtm/Ta2MN7JC0Pm2yvZB/APNoNHVSZZ2SOITbPF/yAkIdHrk+ieCKqDbeuc8TFs2n+6FktYdwPXcqrK266CzlSTPycVZQeyrgrOI+fqU1HwCz+MgdlcsAdAoyuFlFPaVqDesI46YPsSJzA3C3CNhjvuebOn357U9Po82eSFAPYbtBPVNjiNgiqn5l+1x8ZVHImwpGv/toa5/fUyfMmlxijwG/C9gQ4mE+buutMn9nfE1y/AisU/2DWeFBESw3eRAICcmVVi875N8kT+Wja8WsbpDCw+pV2wZC3x3nEdOceAdXtDEb0oy3bQPW3TSZ+Wnp68qwSxjI/aDosqVuyyqqlm+w/irUmNHGL+t7g/kD932g0Q="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyMTcyMTI4NjI0OCwKICAicHJvZmlsZUlkIiA6ICJiYzRlZGZiNWYzNmM0OGE3YWM5ZjFhMzlkYzIzZjRmOCIsCiAgInByb2ZpbGVOYW1lIiA6ICI4YWNhNjgwYjIyNDYxMzQwIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQwNGRjZDdhODFiNzllMGRkODUyMzE4ZDUxMDRmMThhNDA2MDdlODA1NjM5OWZkYzUxNTU5ZjhmN2M4ZWRiOWEiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "AiHSx+/7R5CjzeKxfYRnBMu0XW4uWGbSW30WqEC9q2GEV8aacMTJRPg5HNWXneCHJ1CNdiXBgs9meaw+uffKxm05KvrN5DwgQbU3yhf+g3megmN/qk1fmIgsMBEncD+NEcyMF7WQQ4GZCrnyfHbvCFetmgwkJr4On8vlnkGxiUuRuq+6FmntsVII5pS7Vyv8hrv0YDOs5amaggtlh5L8RGbRp10JSMSsno3fF2oPFUmwnyQKEIQnzhRbO9Fi+KtKF3nQyQG0N/I+BsBtVYcuoX/UQ+rjGDWxNiwgELuUrx3bFTiUBDGEgRMFP1JrWe9LPtkhcGfF6hBHL3vJyim3wvGU5L3S3x+aXr8Tv/sR0BVppeu4pSEZV+FgXeDKMWWHzNd2NvhnxpbVUttTycKSuAQHV4cc9AL32Oq15Oo3lBtvtspIa/y+VF9nesO5d+K4Ys6fW3pvNfFK473JmFRl3LFwdCbhBKsLDerBKm/UL51d/aT0xpqSSYKumjDgWaS6wQVhTqICf1UZjQp2OM9Oo/bo74e+exUGsJsQiaSqGF9YuR5yAqosj1wsFfaV/kPQO2rDH7Yj3aEkQEmLubukLGiBbXADSDG23ZzQkDDreznlGmHeYhSf7XGh/LNE45Dtd2iG5FHr8DAmqm+ipQ6xkw7/SNmqDJr+6JgCFNSzVIg="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY1Mjk3MTQ5MjU2MywKICAicHJvZmlsZUlkIiA6ICJhOGJhMGY1YTFmNjQ0MTgzODZkZGI3OWExZmY5ZWRlYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJDcmVlcGVyOTA3NSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jYjMxMDk0OWU4NTU0MmJmNzA5Y2VhNzk2M2EzMDFkZDMxNGFmMjk0Yjc2YTUyYThkNGMzOTVkM2FjNjk0Y2IzIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "keO3tnrsR27Dx7vWNWSpMOd4YP5jH1PEilbkOfOhvpIrlYh8ciYJl002kXDvlG8gFrujUpVuxf5VvT/7svtZokoGkgGY3Z2yKpdGiHtf4FVwQC4Bro+GRPdkxRf3nRNLE4fbkWy3tAhm6beqCDpbPcJ5NORYdQDcWx/AniRqskiz1/xb5S8FV6293IOvlLvVV4bnESe1bPfk/g5kAera2yjHmmg+sIBcQxbIzUdFsfsLRh/se8mOW5jS368K1MP1iMMFkMlW/pXG17ITA73cJOK+N48UAyjM7kVu+Yx+zWG4mMAarUdLM1apT+lsFCVe4mKq2JjFw6vliUYG3Y6LCBm2JhR/N5P4cEfs63SmsuIm9zhDe1yJsNU6Io4iQTjr2NMpcq/lbK0rZngSlUVzADptcOOY+ERbPLbC3nWU7QbPnupgTOsIxI1RQGKyG1MV24PDgSKlQVZ1P7GyIu785FBvxd64inXWGk/GQmZ+WytzRbhSCYsP2RCbnyBqEL/qp0KbvCjd0P3WgxSfjwBUFiIbKBf9SNKkOp80KO4azp0XD/cNezhdeTXRGgB5EMgFi22cEV1dn0d3I5Zut5vNgQrptUU0fwHPizzwykGfzCNUc/tkSW91j1Gh3z9AA8JMlmxcqVG2kfUZqKBbmsrV5jewO+ctppC+1Qi4mgUPixY="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyMDIyMDc4MTQyNCwKICAicHJvZmlsZUlkIiA6ICJiYjdjY2E3MTA0MzQ0NDEyOGQzMDg5ZTEzYmRmYWI1OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJsYXVyZW5jaW8zMDMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVmMTcyMGM3ODBhNzk1OGI0MWYxNTNlNTA2OWRiNjg2MWJkMjgxYmU0MzJlN2JjNzk0MTE0YTdmNGVjNTJmZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "olnQih89nZxKFe0UzWiXU8+wlndGBClUqXxAabwEm6j15SZH9ue7Xd2OM2kRKBiHoqbT+2TSg4xG7cBeAeapVN4vpRP5NPujERl/JI41jYNhMb+DmskreS59fh0QfZPAxOpj/rmmAJVfNN1QblxRM3wlMGaEgS5TH9HfeehgLrBaaDM8/JAgnas4Yh6L0uRoNebjXHrhqgguVBMF3xsWpvpAPCzQCYX2vjCCF3WtOEy7EEUF4u5Lo4teQhr9yfnYGBc/ktE4I0MByqTaKrLqvF45n4jOShPP0RcmLh9JpOXyrScRuaUDhQ8bd8xhkWEb94HMzwznvDLNh1/nbNybCMb5GydYf51hJVfqjU5TMWID71F8FTTBJrCZDBRESFIP+QZ3czYP+urgzmfLgDmcoPIukMaHWLU6qFpTF0QazAgF4u5Fe4J6QEZSyZz0B2kqQG3vN1dXxLgHItjQbEeceChNYNjuZFOTleXzpYkg5/4Zqy6Oek3bMscTYY7IPBV56WiO8eGw5JYMfyDeM3iyh4ZxLEC3HDRtOTBHo7WxWPR/AUOU9HP9CdmKQbGThGAUuqlqRJzbg5XNRvKIcnngI329VZV5RmAnt+G5Vfy6uqBagpMQZ3720PXPG6H5q4SBuXmHt1ccKgJvQv9lTh20EymuIALTnCodr8qDbnRfdrI="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTU5MjQ5Nzg0MjM2NCwKICAicHJvZmlsZUlkIiA6ICJlM2I0NDVjODQ3ZjU0OGZiOGM4ZmEzZjFmN2VmYmE4ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJNaW5pRGlnZ2VyVGVzdCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iYTZhMzY1MDIyYjE2ODVlMWVjNzgxNTU3Zjk2NDE1NDA3ZjQyZDY0Nzc0OTZhZTliZjYyY2UzMjkzOWQ0ZjRiIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "il6M+JLeg3Le2Z07Oo9DHqw85krb7PFJCSTvWE23pq3RzRa/YnN/IHoSkeE+Sv1XxaHfoPZa0+B/7n/SzU1AMoXDQTPigvRVXRT+i/dfCEuQb3gfbi3OW4LjhpkAnP/s6Vakrfurpm09JkjDyISjLpBEU8j80nTn1td+yuCS5MIGNGDlaq7nL1S524osMKrZzY5nIfGxGL9af8JMW2GcM65VYWAJPUA1YtU1OFA1dVmu6t2yFna6NEm58DcE38GeEYLE23v/HLgYNdb1euNtFFLXMdRiqtTtP6066RjgvRGhFf6CRRf3t0Z8xfGvetS913HuqA2Z/5fu2noHP2YRNkll28RLq0D7wDuePKpSD3/Gk7vFYbYCJ5FSEnLoc7K5oTpcEowZAMsKf64oI3VbE74D8quSXNT8JKek6BlxKVgkas/Wzx01k5OzWLwfXPtWexPM2HmfvCzO7GVAotSSIG4yf6b7qZuHtXSHcHT/g3wsTi+r9Is5GW7t9d09lh4bYhqVijEOyhKTkWjoPIJxeMX1CV2HKhjcNXIhI4HkdSlRLRcuO9jl0AGBkH25py220TCWKLDcXKp+ZMdezYsOPr3F9LhZ0xYuJPdjG+vsMlfy4QLBwcsxvqV5XIYpX5csTXiwl2Dv87YK4MpCkNLoWImi/o5pbismxQWdAhhVgQk="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyNDI0NDczNDU3MCwKICAicHJvZmlsZUlkIiA6ICI4MmM2MDZjNWM2NTI0Yjc5OGI5MWExMmQzYTYxNjk3NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJOb3ROb3RvcmlvdXNOZW1vIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2IyMTU1MjA5NzQxZjRlOWUzMGExYjZhNjEzMmM3OWM0ZDMyZWRhOWUyYTIxMzc4Yjg5Yzc3YWYyOWQ4MmZiODUiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "eAMJqWmhSM5ZTjaUep3duZbS2HW+2/uLebRfTXbNe7U+ZSZSz1GxZ9+92H60PyFcZb0vlGB7fZAviud4s39n8Zae9IirwPG1Ad96D7LK5B/E+jsbDrtwcywHH29nl3FgWjWy28eqIi1z/gmG8tQ0RzjBHUQzhaSnqmwQ0ea0cYLLNQqDMoXPKNOfG79VG4SDSR7m7fywD23w58bDiXEJbD/xwx4wQm98MzIyOg8UNjXaAsRyHKj5DlIGh7+1NbemChcoaLb4gkNCWfkKraMHT4n/J4zpjUMJRcF2adhhXXtnldPRupZXkeA45mOW3Jhamg8UGESANzhZsnXjQ819CFOjl9WusRHFEzQyOPFOtg+340log054xdTzzbRwsqfF2b5YS+LItRxPCut8ZcCqu37wwLjx1e4Jj9yLvYMk9TIcD2V+UZ3oTFuNNzy55z1rlKiKxjQcSkMw20eRdySBCRqafqNDKEPJUWpMcoej6ALS0UjRyrKlz2FGw8gdQrS+dd9czUFoXHpAEvvqvEsQcQJ/QK+uazwiD+2QsW+XRmtj2fJmE6NJCmyBvGRuRiH+fvTmdQ0saBiJV/wc20brk4V/3eLp5S8dQL3PMo0EuesTJ5j7kjr/FC6NfxPEity0H6bNPI4PmbtHI4ujYytFSH3m6IgoqWvhZdp1pWLMQIA="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYwODY5MTA0OTcxNCwKICAicHJvZmlsZUlkIiA6ICJhNzdkNmQ2YmFjOWE0NzY3YTFhNzU1NjYxOTllYmY5MiIsCiAgInByb2ZpbGVOYW1lIiA6ICIwOEJFRDUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTc3NWYyZTE2NDFiNWM2YmVkNDc2NGIzYjNiNGFkMzkyZTRmMTVhNzgwMTU1ZGMyMDFjNzY2ZmRlZTRjZTM1ZiIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "rLEtXBQQihP9d8yww6zvbfPCU//nb80/rM0SDz1qTcPQrDmGKXLJtoIeD0q2J/IqQi33ucRAY2bEiWSMDUHpC3ry1EQb73ut1sC6ErcCa9/F6PT0o8bcbAdd9JkIjtEmumy516xNe5mejXULZC7LDkZ7nXyJgAJ6D3jaAm0o89kcwt9ofr3IVEw+d662ZpmIjJeEekX3Y6ExqKTiKrFV1soZWlnGyvAgTJBNuvFYu0uI304p1o9K4ePa9nSa54fBAc04NYktowLrRgG3so8SgYYCzCtYxaL7YFNIPnmUEylcXVomiPshE5hEhtVOM98rQLr7hoKLiai2IUPILqGJoO7C9TJYbevrM+so+ukd3SPfFC8LTt3/VrPFqWKbt+8D8lM5qgPpzBQVCpVXV6K7mpnCA/CewMvkKRYMbkB1lUDgcj2nkH+mzPZaa5bljrf4FMvwL+OpNdV394IWjCeZ8SObv/EqgsWD6X4k7IOWtvqraUg29sMIkU3879IhRBdSpJUChss4sVk5fxh8l6mmNE68hYE+BvzV4sn+i9bbnjBCUeg6nu2zex7Usalv6Z40od3LbZGJRTjjekYfYUzcsmTnfRYFo6+Ae9X2ZxXRNozA4Nyu/4LbiWCAxM0bRwgzaOeSVCpWFbFBu+HYqBA7K6OUJtgtevlMoscoQfJPe4k="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyNDgzODYzNzc5MCwKICAicHJvZmlsZUlkIiA6ICIxNzhmMTJkYWMzNTQ0ZjRhYjExNzkyZDc1MDkzY2JmYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaWxlbnRkZXRydWN0aW9uIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE1NDgwMjk0NGRkNzk4NGQ0YjFmMTJiMjUxYTU2ZmFkZjRjZWM2MjM3NzI0NzEwYzMyMzAwYzQ1N2E0ZjYzMDAiCiAgICB9CiAgfQp9", "XQvO7IqapXVFto9HTjaEUWX59IhlWXGGa/fo0NCuN5PJRzxaLGDLd/IaU7hYmDfk7gqMc8fHojzOFD3722UUHCGxeq88q0vDp3yUjVEeVEL9KofTyH2XcuGrpJxG9UYPrmdGVVpOqxHRJOloJW+zezPLY9Qh+VADFy+ElCeAhNahvUL6JrWNcxUODj0PG9g+GJmIKL5MhsWYsvZHzMYkrI+clDJTVGGh3iSjSNb325emmuNhkIGzAyf7XW76A/FEZy1UOYZol4lc0CWa83QSqhwSoZ6q4PbQAfEpC8xnRXsuLUBRcE5IqIkRr6pdNyRLpZWjlPUY+Qcv71cg0yg/kR2BNtgS7Zk0NvGHiqsndZsyoH/12bersSjoNrRdEEmWJ/mRW47C/qt/Syq5KJWetJf0eemKuo6opiKbuySbCly4uM7EjPqUDFnXUjVbAOCzDLlimnGM4g6OYrBSamyE8P1wocurCrRxW5sZSuauKvUe2xRI39m4XzpBHX+8qaX16oDNkrmZVlR4NKJ5uKMaUgcOXIY1TnFJlv96urZquEKmEfVotS7dT8srdgxPwvffcKgdQmFUZFiD2fkgVrKIkGUkzgr1EufvYlb7pkDeMzBj8q6NA0m3+5cTHB7CCa7FfieN1/JHFXiGBZErlENQ7PS0kSOgCBhHwFTkkdTvbNU="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYxODM4NjExODU0NywKICAicHJvZmlsZUlkIiA6ICJhYTZhNDA5NjU4YTk0MDIwYmU3OGQwN2JkMzVlNTg5MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJiejE0IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ0YTI2M2E5NTQ5NjJmZDhiZGQ1NjhlNjZmNDFkMGNlYmJhNGIwYzA0NWNmNzBhODA3NGY5ZjI5ZDcxNzA1MGYiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "t2oMBS1i6yONHoiFacR5qjzySyYBhrcy59PAGba6cV4sQBBKn90++r1+5zxZDrLZ1ZE9hKvVPpxzicSemzbdpz44EQ6lJLA4APrA5y4JN1aamapZS4PUtIvlQAoFEY9alu+YEhiHNQUzY3Bvb/32jxkUv0jXYmOhrQBg9cfEexE+umXnEd8lakjLUOgNw/th4cr3GDRUKY0SP1fBxx1BPmXjNwYhrZI+XzeLVDRKf+LxIOGxcGv1ilyQBw6M9bDLtPTnQZ9OYH4Kh5W72SddicMR96+vARgsLVoOxXWRi528ka869fWYTu/uvO3494eNeT3eCyzv7bzE0tnGKjvmYsIBL3RWhkbCTuhi46BBwLqBA09GdIsODocVbORMyo/ePkyLNwlM0QZr5V4eR3JSVCOZNkWGBhV5txVe2cdOIUJcNo+dE86VG2UraZg+wtUewylXQiD/qc5EmvZ77b9IcPuTaeB8aJB20xXd72fzqkp5A8fqRxjVPkTGntNBqi6gdETP80OIHcOcPSYc+nUKNv/o2URrGo9IbDORP9FjZDZA4k3/CFe4ALvc7lzdkDZhJEQmS7bLJlW9fmgnokgK2ZlPbCq/dU6zEv2HjwhwFpjhFmw0vjfw9+6/sc95obHjdeB8TOIRxaG9ES1SOkyOSCx1b836C9PEcrjwfApmlTw="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTU5MzUyODQ5NTE0MCwKICAicHJvZmlsZUlkIiA6ICI0ZDcwNDg2ZjUwOTI0ZDMzODZiYmZjOWMxMmJhYjRhZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaXJGYWJpb3pzY2hlIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdhZjBmZjI5N2I3YmYyZWY1OGYxODk3MDUyOTU2ZDYzMzg0MGYzOWVhMjdlMTIxMjY2MzMyMWExMzJhMjJmNjUiCiAgICB9CiAgfQp9", "L0uf/pwI5ayEiFlR19U3YJQI1fKQfLt6ip+xxZbdcxeTPBe3BZYzeOgbogT6/iCDz+WVRN2nW/JDHhT+dNX23MYXS9N02bR0+dwigtvWbCMfeRfwHHR8UGIQbLHErt+TBAYdgJW4I+KlK7Z8DFG37dlvLm66E67YHV9uWudKdMkdpa9ycD0vD4AniT/7LrNVpxBXk0HjG4WT4IYV4IAps/HqsNxWYmWITn1T4uJmDLtRLdxSmRSPhJBqIx8BstQV0O+YlI5ZbSjYViyvOslQY1GDNNoU8VNFIrEUVjn6qBFPMfBt92p7znfvFRvbE3yQx9XQJRM2hGzGf/WAGubGqayL4nIUVouiRqSH77q8AcUH+WEUa5dML0FOEaMw1UVkn/sBwPMyTHlw+P3dClo92NEi6+BtBXu4ocMaaJ7SH0ncGhI09gixq4zpoWXsiT53HIWJiD0dtlmFp0dzLrA4gduySLWohJLcaMf5LOoncqg7JiUIXLfmE+HX0GtCjpZKezxKeJIBidntZ5r0e7y6Jy8GoJ9dpOnY9bnwbYO9XGXFTfV4BpFLcraXv9adZGM+TXuNo0Nt/peq1bd7extfR3mQVRlgMg9+ni1734LoFxeeEWALRWDGzB0fkVPXLQU4YL/ekJfEM6dxwyE30sf0JmyDoffEk4mRZPldCWBjU34="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYzODY4MjMwMjQ3MSwKICAicHJvZmlsZUlkIiA6ICI0ZjU2ZTg2ODk2OGU0ZWEwYmNjM2M2NzRlNzQ3ODdjOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE0YjRlODI2OTJiOWRjYmY2YzEyOTJkNjJiNzMxN2MxMjRjNGMzOTQ2Y2FiMjFlY2E4NDJiNDJlNzBmMGMwMjIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "renIrkVFQUor3+5AeDYOcGtUnUpdk9/i5ANWFGCBTSVZjeKQ/t5xDEe9kqwsWawR/55N2+1Db2c7lIpHpJ4cGqtG7BzTm+TZNUgSOu0rG27DwxheiuGbYSMm/lQSiNi7FvRlhLXuxsYZ0nHhXKoeG4xW5PXaE/zjXeXR1hffnfR/ROanmK/m2nIbkfPo59wjc+ZTF3nxhX+tGay+7dy/Y6xqhyZ4ZnM1a9+z8hC8ERgXzUUczfhRaDPQcv9dEdpyQhlfJyEV6r6NBSpBVVNaZ2bGs+VyxrRVtr/nXigps1KtFXH3j+gBiNYJWu7LpDS+1DTezlP9qkbDUPSKuO1O913GDRdJxdcVn7HGYD3W6yGB0r6sDBvb7RYESMzafRIFbBjhJrJFi3/aQjxTuFSc66bUkDqNBGYQcXyUXP1wEuB22mwQABv2OZiFdXMMRDniSZvPsxoriDdAS+umHcrAgTApu13xLyJJa8tFBD9rpGxDDoUbnNJdzZSpjrgfu38Kgpa3pW45HY21eSOubQNdz7qBTBmQwVViuVoAqH9mM/HqeIrGzwdRJaOH3GsZRofr4zh9HVc+5o02W72d39BskA56ae8zjGza9sF1jhkgiaW1NH/zuu7LnfujjvvMcczrddv8P1r7yqsIwUrP0ObB+ylsCsrb6mAV5uqXuklS7e4="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYwMDgxMTU4MzU2NSwKICAicHJvZmlsZUlkIiA6ICI2OTBkMDM2OGM2NTE0OGM5ODZjMzEwN2FjMmRjNjFlYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJ5emZyXzciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDU2NTViODE3MmVmOTVjYWI4NDkwODk2NTFjZjU3NmFjZWFhMTBiMjExMTQ0ZGRjNDdlNzAwMGM3ZGE2OGM4ZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "bK4ylZquICTUR1ZzPJYdqZmD5oHXRJ4GMhHrsqOY3zY2ZKMoFtCTVI5LaYohazvV+jmttnV8VuAIo1v+TvOnHwu6spAdXtRkPTSOtyJTXb8+01FonUXxyEx+AMyWGRPPQpjGOjHfJQP+GauOn5L9M0hgadWdMokcPUUv9fw0CkcJdBMWmU/i5MxnMjVHIZ+5bRLSQTVHTaM2LrtD2RmSv7ZvJgr9Y0syw2Qmr2KVC5ERFCgWdLdZwwrR9qC0PLnik3DuP89P+qXhqUzJ5N5NZS9nsEhVHKEMrkfiHbundeqw62aHqP144I+mYj+Q3tgLO4i35MKKFTMhnuwTbjK2AR++rREEtTPjZ1zBGVNfFrkrFfHPKYlr1Ew+wriLCUtSXRFWoebH3xfQsFYTjctFFy8Q/Wh7jyFEdImnCCobsU2qfQsq+tEa29oIXbYWgKbKoW9f9peVLo3PfcN9A3zC8BLSJQtxRA6WcTMLGUaswfL/6GoL6KEZY3tNyylieic0Aqi+f5HbjDiow6upPAIPKSy46bYXI7s4UMM1+gOjF3pr0Rm5iIIY5jfvuOGkC864Ox8K1z8W4Hds+2+r9iGzn/pwNvNAX0zw2Lwb9adcrrusuIRTggHpWzWIn/9UpuY19eqrEHQNGVqOiNkOY3Y5f7k5lfap1lLu6ls8od+PMyQ="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTU5ODE5NzM4NDM4NywKICAicHJvZmlsZUlkIiA6ICJiMTQyNDBiMWM3ZDQ0MzkyYjMxY2E3Y2IyM2NmZWExMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJOZXBpcnVfIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2IzMzhmNWFjZjU3MTVhY2E4NjJmOWVmYzY1NzRmM2E4MGUyNjZjMmQ0MDhkNTcyODExYzdlNDg4MzA4ZDUzMDQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "Jfnt3pOSwq3nFYZUXqlP1NRBHfQBZJW1OEklB4ZKp2XdBPmLtVuk1jPN10RD792apdLReiz4Qt2HIWZj/S41nc0QeP6FdTQc/50CngAtPMTk3RCtDLYrGZN8QAjZ3xYn5kPsolthvKYS/csQzNT1NJM5pQYYtQX1aBCYrvWlxJsX1F/MYpG94JNUiMyeU4zaZZgNeofa3YqSJ3Ys5A/W3gPcryDyZbj4x/rrREBAUByzZu3SnF422XefNIz81OowmmVdvtNW2/vosV94OhXoa5AQMgtudOhXV+T8PZS1KlFwWM6rnFBuowlULZtodxRTzBdxRyT/oFVsTsH1RNjJ9l4sYW4gGj8qiLxf92mqeHvIdw7U3391LE174xP+BhCV75EMb1h+2ARASmIFtvAt6D2SoWPGqnO1PyR0KBBqg+RVgaTd9pBLvjboanhd7HYyVSlqY3fSsdT/Yy/+pscrREN2HTXW/4LrntsoYz5OjfhB1t3hXhtU4cGL03LFaN95qN5VE2oKIVvDqGdS9PUcAaSRqIa63qot1oKQzrHR+FjmILs+erBITj0jplgszysOZ1VU71WOhqPpOb58qhudVcOxHDEySW8vr98gdJ55knyKdih2oeuJknds20n3DApdnXmW8W/LowLUTqibxusx/lmERNgIWoafPMSPWHV0Fk0="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY0Nzg5MjUwMzUyMywKICAicHJvZmlsZUlkIiA6ICIxN2Q0ODA1ZDRmMTA0YTA5OWRiYzJmNzYzMDNjYmRkZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJnaWZ0bWV0b25uZXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ4MDU0OWEyNTJjYzI4ODJhNDUxMDc4NTUxMTNhMTg0NzJlNmI1YTVjNjE2YjI4NTc2Yjk4MjVjNDUzZGMzZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "LtRvG/OteephxnjkSQfrdfbDUWUhoG1p4L2pIit1e7KF0UV/5BjgzfQUt+MXRrmJTGQVoJCZzHt+VFHT9UL1ptguabvM6sbBWaVmg7RPo1KKBYkmai+dP5ceCXPA2StVogKN+JjRwNF6Paw850IVha6h90It2VRk43/IbMzdSyTnMIH92WLb93BvENYX00yRCJY/m48tYECibH6FtX6vgqK6UgOgNVqW0g6Otuwx2Z4Pi0xUHn6i9gCayPrWSg6Y7cVQ5pM49t1A9tfN+Kt4J+sB63Ez0LSws3y/5MP/8sFUBQXKaZYpATK5dmBohZao9wroX9Ni9sADzPcGF3XAcHXZjRr2Qk6Y0AsukuAlXMPlDWFGxnVK+jElwcGEsx8g8cK5iufAuDAQLQSUJ6vgBUE8DndEfL+l8LpebRlgMT4kZIn75ZbhY4BU2zvs/fUp3mPhQjjkrTMqwofDFj6YxnZksUi+qwEIcRc3ysoFl6phMN5n2mRV6616l6DTSh6y7Vd6tT4s6UsfWlFUwC0gdaPrU0CSX96Afk+BSVceh6qxs0IJVBn1bBe0uDTwK1a3yUOMXvjosG0L8jzpNY7sGE6ybwHUxPMaNIilqvnhO2NdSgsNHVLlYHzh0jUMs5ap4U+9fAEYYr0OpkqgZ7l6aQeV4ME96RrGqZmEkxJPjas="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY4MjUyMjEwMzIwOSwKICAicHJvZmlsZUlkIiA6ICJkYmEyNmVkNTk2ZmE0NjBjOTZjOThhYWYwOWM2MDZhNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJPcmlnaW5hbFJlemEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ4MmU5Y2JkMmJiMjg3NmNkYjczMjQ5YTNkMDhlMWU1MjljNTk1ZDExNGUzNzA2Zjg4Mjk5MzNlOGE4Y2M2ZCIKICAgIH0KICB9Cn0=", "j01rUhHFGAskv3OEkzdRDE5iTvnWWCjWQFfi0cFfe1IGWzLYK/bbtZK4rZ7Gt5o/tJdBHZH8MqU/ce9LN+mofLAqycWHJT0tUJZi84JjXsz0KufaX0ZPL9QO9ngohOUtbCk1TYwHIySe4aLiT6DU2hRocS0/HnjsFUG93vhznpxMNVMiwLRTxhRhATqPXnmnBt+LcLwrGHnKOUPDB4iwBDZTtSR8U9TYwYZXoMZ4EL9ND4xxw3U/MDsYde72pHpZVcJxFW01E/iZiVpe/TLFA8gIPG6K+ilDJm8wR4oDmjSLk3bRjwvChHLJ418s1wThSp465YknZpp5R5nwRTne7lkqHMB5k+Uv9IDewSEb3HVqYGTMzeZ112iyS3jk+ukWss0Fkvn/dZQ7T0tfVxlmXVezpwtWGvMgINXNv7gARcsoDmz7ZWISmfO20QXbV/SxO0ymgKzocZ5Sa4WjF8TbmqVVA2exbsnI0dfFoivTshJPgd4wxzlB9Qfey4KSMuxS4sQu7cztaEtvO3W3HaP3IApluYU8lyLNbCvEZ6nCkl7XUH2KVUvgzeFW4WrVHQBkiZyEiA+2RsOwKZSf1r6X4YfgGpNdwAAa5Iq11Ss1R14rh4t2oeYqBDqegAVIZPZR4hVZMjD8RsAbuiZngBPQXGEQAHI6qdgvlgiUbvnwWDM="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY5MzQxMDU0MjA5OSwKICAicHJvZmlsZUlkIiA6ICI3N2RiZmY0ZDIyYWU0YjExYTExMWZiMzE1ODJiZWI2YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJIYWppbWVLdW4yMjAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTk0Yjg0ODkzY2Q5NzE2OWMyODExYWE4M2JmN2MzM2MzZWMyNTE1ZjlhNjcwM2UzMGI0MTNkMzRhN2Y2ZGE3OSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "KxPrgCDUJOIcqqsWig1r8osPWar+Hh4EJ6Opz7MpBQ91wJxavmPLmxwZ8e3PuKtcSjJD1Gg8Czo/EcNBSqYfREr2z8Z3gOKw1+upunapcm76Z3wS1Vl+xtgUANU2CLuyfdaA2fvdW7RL085rBUsZRqL0DZlYath0LlWLChfbHaIEfhvE9nUBF6jfPYhiNLJr7EmBT91qm41fl1G0Yhs/OOI67E0VMXG9MfeHW8RBqHKhTD6Ph/CPmgLQlEjVxJnZjcEC9qkY16ANrx838HTuvRKMHWIlMYVAED03mYCvLo8wObApamkI13Iz+HWNaC0tdv7voXiZ2gjwJx2GO5RrZ6OUECkzPCC0HBGjAZDDFiClqpI1m2vITSqzuZcPe8fntpVGuNpY/euH4GCow5cBqowu0jSOj7PZmAKaaicvV1dZUGstWxCEoNCw0IuA/LMzxCRHlRMft3k3qk2FJ4z9HVVRZIohIHt3u4q6dD/y+5eI409bhzHJbP4sONLw1KUayNQpfAJrGab3MdtDXwRgjGTpl1L3Q+p6eQXpGvx9ABcxpXlzzHulx5+lvQEMJgFZVLyWV+OG/04esgEJeOtBn0/jFPA425t+ijWcZ2ruB4aqrZjegOYKIxQUdXKqP9JTT0+EMlwU57zwZZch6aSiPBObK5X9HHZVWTqqM1xV2oo="));
                    this.add(new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODc4MDcxMTkyNzEsInByb2ZpbGVJZCI6IjIzZjFhNTlmNDY5YjQzZGRiZGI1MzdiZmVjMTA0NzFmIiwicHJvZmlsZU5hbWUiOiIyODA3Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iMjI4MTk0OWZlZDc2M2Q3OWYwODZiNGU4MjE0ZGVjNTdiZDM4NzgzODhkOTJmYWQ4NmRjMzQxNzE2MWNkYjJkIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=", "N/b/lSK6Y3Wqm4lTj47YHbec9yVAj7XmDjfWhVAa033UNA30U8o+2pTY0aVDAzFut624iC3xjqMzBlXt7SczsT0w8EV+MnW51V6aPlanj5SQ6zVwB20TdhhAzBNvIQbvo4x4BL99ZpyBJMBRcCVEehjaD3rgshBxH6t2z7WzzYM1cij/5egedjhm8ek8DMdYYakN6DWIOWDv05VQSiWRMhitSI2sqJMTYKaJcLph7/56Ke5zRNtA2mwEcdB+GnDPkeEINzx3A0WG/vOS3iYL8L4T5Dv1GzBlq9s10R1K4Ks5TQLhVJ4Rp2S4COLvvWsgREHQVf6NEIOG2ww4wqTi/xmHni2d6TM9K+vtLSBE7umEvLeOzp8oqbQvtD1ipa0iatR8lEXU1bcGITtwZi+i+zLeOIfx2592XevcOGwTuvhBBM53rN5suLnpcGFIT5TuOQrFinT1+vXoE2D/UkDll8nvtGzJyqFgSSFDrvf0e6ZkbFlIQRoJGkfhnDLON2aEycOe9EcD+NiLDXQc9++j+3Kl5QFyze3xd21+ConIZRGDXKqvoEhfp1ovR7ND76IVOAoGMcDT4N+n+NWdXIilipux3gQ5UZkALw1ocFzhEZY9pCYw9e7XGQRh27N/RYns+sSI1qXbtBbl0FCl7X5efvsJLWId0JuEag5f5RAYYYo="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY0MjkzNTM3ODAxNiwKICAicHJvZmlsZUlkIiA6ICIxNzU1N2FjNTEzMWE0YTUzODAwODg3Y2E4ZTQ4YWQyNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJQZW50YXRpbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85YTkxZTYzYzhlZDEzNWU5MzcxOTIzZWZkYTM0ZGEzZjQ3MmFlYWU2OGU0OGYwNTk4YmQzMjFlYmU2ZGRhODQ0IiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=", "axlLVBXTguxfcZe/96c4Srg+PB06MborzBRG3QUNlZfSGwKHN2QLXd1dmGhzvMOfVax88Hnatqbn1fKknM5pu/A/KJ2UoxKXisJAld9lN5CZ01XkifWFSyEev0GXa7b1fq7zmh/nNQnJ1K0jpVycL22YHjFE4dSywC2JTG7yl8hq1qtf6sJX9CLxfRKejm+b9cxSlajligJeITNdQeSBmEH6jOTxlsBVwLWp2gqNuanpHulNtMkemKGrDp07xoO6ZymIUNH9/bhlDc1Q7dmaOlQCbIPZNvkDVV7C+nJmf89BOSNac28MRL226vxJKwBEeWOclYSnYWEADc+6ptlaHHHAwmnaSDfsnHQfWjK3vUNHiRlvLtpjE4G/gQtJSlSBfcX2ge/No+Rjwidpm/pLvOpF0l1Y2EzeRzjiHuLd3bFNN+qARCKzKzbP8bNyra1jWVsqn/MOrsv/7ZJNeFV1LmpkPLzdTr01C3cvk1DBEqxBzmuQVtphK9xMVz+9C5BM1nd//f8KbsuFZtF5KIZd8qqtfNe0GYikTEq8VHIgOxpp4q9M9vTjKh//E96/khZmY0aiv1hfAu17qJpDM5yMErGn0IcNjfBTcZRnAesrmNaILxAV3vAN1n3kLNbOVEK1jaSK1pwQG2Cu+XL1WJ99AgIw+1LVLNqKyDecAghByA0="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyMDUzODc2OTY1OSwKICAicHJvZmlsZUlkIiA6ICI0ZjU2ZTg2ODk2OGU0ZWEwYmNjM2M2NzRlNzQ3ODdjOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2IwOGVlMGI1MWM4ZjFjMzc0M2YxNTAyNWJhZDkzNjczZTg3Yjc4NDVhODU5YTFlNGY2ODUxMDM3Y2QyNDYwNjIiCiAgICB9CiAgfQp9", "BpYxbKDjyWnHTjQM36pSK6doKULuQ+PFWEtNOefoOqf3JZLJ4mrpT9plB195KJ0vzU4Yaqg8GefVlqiENpbNVTnn4wihOSJ8lAcEFdBPDhADLBD7BbUW0NGHJwgMEichaR4JCjFjjTfei013sQyYQZ+KFPaqLb/vuU+8Pfw1MV39luYXUAzNosrZv1pAMN4RDyIP9bzsmwBdbWgVR+tuN43sly/LWlW85SB2aOd0z7N7/j71vN2BOmpmOYLj0nqDZyBw7hXgnBy04/msSGVAOgILW0WOYKAaYjGss967wGlysy/cBpDG1/KlLhueUht9C485/qWG5N4j0/iS7hsSI+wVCs/tSKmYHHB/Z1f9eiRkJulm1xQL4f4cmGpwbzP6VNzSSXhInBM67tsT5kUVhvvHyZ2DkZLL+30I1tfcbfIEZaLCmKYCEpbxMgawHpOtq6W8wX8oNLNji+BVndWTmNGt92eX1JmrUVP/0MVyGIiwZo5AHtZZPnKuUc8SJDckvtljeimAzczTM+u+wXKagi1oYGPZJaCkPK9wlMe5XGj6br/UHBs3WbvzH3FND7NNAE4cahd0vayuNIS5VLQtyPhX8eYGJPUps+5kIjfA0RqP0UtvSDVZ9bMXUYokzNtkvjZwuQR7QQNCoh5+tXg0KzKaKQyRbyU9VcEG6YfDTLU="));
                    this.add(new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODc4ODc0MjE4NzMsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjUxMDFkZmFiY2EwMjVmNmZmOTdlOTExYWMyYThlMjNkYjhlNDBlOTgzNTY4YjdmMDAxOTJiZmI0NWE0ZTUxYiIsIm1ldGFkYXRhIjp7Im1vZGVsIjoic2xpbSJ9fX19", "Xg96/raiX+7NX6zCENGeT5iy6pnghgK8kBdoCVFuMPjHRKTpwBOuhPhat2LzGeHNH5pPRANsjrp3+ug7F0c49d5/AubOq4xNr1DnqNM0Rj76ZuZ2V1i3uc9sLYyfuBPw98ggaiYJq4QHjPZagLDvPhY/WnyZa0Vml6X1nWrD7tNPg2Nj8VIiwrFB3eMsoWzC5WFyze4oOfUtTTVyDmU524I3Wy26wt0x6Ch+EqE5VJZha4JwQgb48RUiOXZDimw8paq6k3C4GrQnT2j+OQL4ndALiT8XxY4NsZS6XCAZukfkJh9S80x41SwTLHbQSWDx+O+prMeoHeM3zfl27E/w5yizugtcMjN3qLCY8J+1MqoIvr2trJKY/3VBDv29o3XmwFeB0M2iGaO9HK7qenRHCCv0w64xIdsO+VtsS0mtUFAh2Hh5nJqif9spOzaWA4rYPRtiYilXYpMLni9dSsRRhD8whWp4Rvnhw0x1Usyh5YA9HW2mEOdplbjYIzn0lZMmcrAZNY6zXqrjFPQ9Gx/5y9s12YTfgcjlE+gGn6M7J8B1N2FIXMLhib7PBh1oJBFNdNFqu2eeGLvh70eC1e219XwZVhajxVKKCVmwtXpaiCoFsMgTNlmr1j1J4Hr+lobeGxeq57mCJFET8LPjPzIWZF0F5dpBZ4S8hD2rnxPN4Sc="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY2MjY0NTA2MzU5MiwKICAicHJvZmlsZUlkIiA6ICI5YzM1ZGU3MjdmMzU0ZTVlYjFiOWRhOGViYTZhYzM1YyIsCiAgInByb2ZpbGVOYW1lIiA6ICJSZXphV1MiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmNjYzdmZWJhOTc5OGY0NzE2OTZiNjk1MTE5OWQ5NDY4ZmJmZjVhZmIxNTJhODBjOTU4MjMzY2M1OWMxNTQ3YSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "d2wdSQ2ZuayUbU3PJdY43OMZujsdUALKYb7zY4FSkY27PJZYiUH/sQfYEM5g52NIUGjL+qw2o12q3VNYdk++ytKqzHbLzHe4iUAS7G8oUdns3tzyOchBMbDfrur60ItkZMtRGkP5PjH6pHjyLj17tI8Cr5LorZeak5/uFmbuoyKC4SIBQzJvkZYTd1V8DYZkQfLzn6P54EtcUS1LZg48NjCdlcoU26w6qU4jzXLCEjey6qIaERar0kqDjeiuyd320DtaNWw2r3/eZ1ca7hUCPX5ALdPvJAAaGCzc7bK4nkePTwVZVG9FvsUQ/z1l1mPBUgbgf5DahDJW+wm6lsqeH/FYDhTlDQ98PPFdq+S2Tg/P1Hcs/K34mxbg7MA/8lKxEPOIhUbWrJACx1OQJxt4F9PHs11bjA5//tgUa/Y5dDIP/Eg2tZSZpx7FBhuKxjiYlcE8qQEQy6o3X8msBP1nIuZBKH67JfXcx6fKSPOuMwevdeqetfR2D7/77KVsEgSI9pUdb5noy5bHwan0gX+wtXfjcXCPOzuPaFdony8xkc1hJzmPXb9QBuMxzHurropJ/ayobwVpRedObJ0qm5/ksBZKi9CMLBKoqvYWdQY6L5Ci94FoNpo7G50ILhb1CW5cfEBtBDPGtYVdIv1R2Pg/3TE7Qo8i9gEuaYcU/g3OWtU="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY3MjcyNzk5MDgyNywKICAicHJvZmlsZUlkIiA6ICI3YmRhNDBlM2E1YjU0YzE0YWJmZGYzNGMyODY2NjQ0NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJfRWdvcl9wbGF5XyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hYjk0YjBlMjY2YjM5YTM3ODQ4OTI3NzhiZTg5YTFkNmIzYjIxZGFjZDc1NDMxZjRkMDVlYTI0ZjE0OTcwMmQ4IgogICAgfQogIH0KfQ==", "USwVBGfqtLBXEeAqAMaqB/l+ND4Qo3DlcglcTxDU1u+c8Ps1fp8gGWiXMtr5Tvep6nsqJin0JOeBuRj6RJDlP8txORrbW+C4c2BVNHZdCO8L0sQ/QISG5f8qBM4YU+8G5gIHMPDva5rVPmgj2hsgPDUyZimETXw2Hs6oyphit0r/fWnbURjfBOm4rpKzlKSpyLncxWFI2SmKl2+xLs8w/0oCv0X9vbVdWVjIzmi48/w3DOpIGkmqCzRvr1YHa5kpf80CNGWq/8KMngWlzA0LfILSGzkfWxxTBMSs2L/SoWnOuvwqRLkVZtZ15yNnkuDhI93BnT/k9+fwXaLs/6aqvmWmGh+s7D+JjI07SPSfcasO7c1jb8atA1cqsujuNzWj7JywZDJNRfWnCqbapwLFbllLvvkZL7QV13k7POcLoy71SRI0DRD9mDR9GWYVcxuBCTseIz3Cb1Bo0W+TXXF31RIJSHlF+Bz0Wy4IKfEt9y+LbKiUIqhq7LzppxZ386hmaM1uCoj7L1/JMlJZlro85jo1+ryN78b29qcpm9cTjZd8N48Etz+OM4bLv5ihmSQHgxnhFodY3oVC/VnCYFoQtdciZDOBiM1kAKIy7jokEJWbjvWz0hTSYJFJjrkwWmeeP3IQjr9Y4oc3pUzRFYdR93rwMFQERmskst5DP38cy0k="));
                }
            };
        }

        public class Back {
            public boolean enable = false;
            public double ignore_distance = 32d;
        }

        public class Tpa {
            public boolean enable = false;
            public int timeout = 300;

        }

        public class Works {
            public boolean enable = false;

            public int sample_time_ms = FabricLoader.getInstance().isDevelopmentEnvironment() ? 60 * 1000 : 60 * 1000 * 60;
            public int sample_distance_limit = 512;
            public int sample_counter_top_n = 20;
        }

        public class WorldDownloader {
            public boolean enable = false;
            public String url_format = "http://example.com:%port%%path%";
            public int port = 22222;
            public int bytes_per_second_limit = 128 * 1000;
            public int context_cache_size = 5;
        }

        public class BypassChatSpeed {
            public boolean enable = false;
        }

        public class BypassMoveSpeed {
            public boolean enable = false;
        }

        public class BypassMaxPlayerLimit {
            public boolean enable = false;
        }

        public class DeathLog {
            public boolean enable = false;
        }

        public class MainStats {
            public boolean enable = false;
        }

        public class MultiObsidianPlatform {
            public boolean enable = false;
            public double factor = 4;
        }

        public class OpProtect {
            public boolean enable = false;
        }

        public class PvpToggle {
            public boolean enable = false;
        }

        public class StrongerPlayerList {
            public boolean enable = false;
        }

        public class WhitelistFix {

            public boolean enable = false;
        }

        public class ZeroCommandPermission {
            public boolean enable = false;
        }

        public class Head {

            public boolean enable = false;
        }

        public class Profiler {

            public boolean enable = false;
        }

        public class CommandSpy {
            public boolean enable = false;
        }

        public class Scheduler {
            public boolean enable = false;
        }

        public class BiomeLookupCache {
            public boolean enable = false;
        }

        public class TickChunkCache {
            public boolean enable = false;
        }

        public class Config {
            public boolean enable = false;
        }

        public class Test {
            // disable TestModule by default
            public boolean enable = false;
        }

        public class Hat {
            public boolean enable = false;
        }

        public class Fly {
            public boolean enable = false;
        }

        public class God {
            public boolean enable = false;
        }

        public class Language {
            public boolean enable = false;
        }

        public class Reply {
            public boolean enable = false;
        }

        public class Afk {

            public boolean enable = false;
            public String format = "<gray>[AFK] <reset>%player_display_name%";

            public AfkChecker afk_checker = new AfkChecker();

            public class AfkChecker {
                public String cron = "0 0/5 * ? * *";
                public boolean kick_player = false;
            }
        }

        public Suicide suicide = new Suicide();
        public class Suicide {

            public boolean enable = false;
        }
    }
}
