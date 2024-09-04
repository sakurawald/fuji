package io.github.sakurawald.core.config.model;


import com.mojang.authlib.properties.Property;
import io.github.sakurawald.module.common.structure.RegexRewriteEntry;
import io.github.sakurawald.module.common.structure.TeleportSetup;
import io.github.sakurawald.module.initializer.command_alias.structure.CommandAliasEntry;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("ALL")
public class ConfigModel {

    public @NotNull Common common = new Common();
    public @NotNull Modules modules = new Modules();

    public class Common {

        public @NotNull Debug debug = new Debug();
        public @NotNull Backup backup = new Backup();
        public @NotNull Language language = new Language();
        public @NotNull Quartz quartz = new Quartz();

        public class Quartz {
            public @NotNull String logger_level = "WARN";
        }

        public class Backup {

            public int max_slots = 15;
            public @NotNull List<String> skip = new ArrayList<>() {
                {
                    this.add("head");
                }
            };
        }

        public class Language {
            public @NotNull String default_language = "en_us";
        }

        public class Debug {
            public boolean disable_all_modules = false;
        }
    }

    public class Modules {
        public @NotNull Config config = new Config();
        public @NotNull Language language = new Language();
        public @NotNull Chat chat = new Chat();
        public @NotNull Placeholder placeholder = new Placeholder();
        public @NotNull MOTD motd = new MOTD();
        public @NotNull Nametag nametag = new Nametag();
        public @NotNull TabList tab_list = new TabList();
        public @NotNull Tpa tpa = new Tpa();
        public @NotNull Back back = new Back();
        public @NotNull Home home = new Home();
        public @NotNull Pvp pvp = new Pvp();
        public @NotNull Afk afk = new Afk();
        public @NotNull Rtp rtp = new Rtp();
        public @NotNull Works works = new Works();
        public @NotNull DeathLog deathlog = new DeathLog();
        public @NotNull View view = new View();
        public @NotNull Echo echo = new Echo();
        public @NotNull Functional functional = new Functional();
        public @NotNull SystemMessage system_message = new SystemMessage();
        public @NotNull Cleaner cleaner = new Cleaner();
        public @NotNull CommandScheduler command_scheduler = new CommandScheduler();
        public @NotNull CommandPermission command_permission = new CommandPermission();
        public @NotNull CommandRewrite command_rewrite = new CommandRewrite();
        public @NotNull CommandAlias command_alias = new CommandAlias();
        public @NotNull CommandAttachment command_attachment = new CommandAttachment();
        public @NotNull CommandInteractive command_interactive = new CommandInteractive();
        public @NotNull CommandWarmup command_warmup = new CommandWarmup();
        public @NotNull CommandCooldown command_cooldown = new CommandCooldown();
        public @NotNull CommandToolbox command_toolbox = new CommandToolbox();
        public @NotNull CommandSpy command_spy = new CommandSpy();
        public @NotNull CommandEvent command_event = new CommandEvent();
        public @NotNull World world = new World();
        public @NotNull TeleportWarmup teleport_warmup = new TeleportWarmup();
        public @NotNull TopChunks top_chunks = new TopChunks();
        public @NotNull Skin skin = new Skin();
        public @NotNull WorldDownloader world_downloader = new WorldDownloader();
        public @NotNull Whitelist whitelist = new Whitelist();
        public @NotNull Head head = new Head();
        public @NotNull Profiler profiler = new Profiler();
        public @NotNull Tester tester = new Tester();
        public @NotNull Multiplier multiplier = new Multiplier();
        public @NotNull Disabler disabler = new Disabler();
        public @NotNull AntiBuild anti_build = new AntiBuild();
        public @NotNull Color color = new Color();
        public @NotNull Kit kit = new Kit();
        public @NotNull TempBan temp_ban = new TempBan();
        public @NotNull CommandMeta command_meta = new CommandMeta();
        public @NotNull Gameplay gameplay = new Gameplay();

        public class World {
            public boolean enable = false;

            public @NotNull Blacklist blacklist = new Blacklist();

            public class Blacklist {
                public @NotNull List<String> dimension_list = new ArrayList<>() {
                    {
                        this.add("minecraft:overworld");
                        this.add("minecraft:the_nether");
                        this.add("minecraft:the_end");
                    }
                };
            }
        }

        public class MOTD {
            public boolean enable = false;

            public @NotNull List<String> list = new ArrayList<>() {
                {
                    this.add("<gradient:#FF66B2:#FFB5CC>Pure Survival %server:version% / Up %server:uptime% ❤ Discord Group XXX</gradient><newline><gradient:#99CCFF:#BBDFFF>%fuji:server_playtime%🔥 %fuji:server_mined%⛏ %fuji:server_placed%🔳 %fuji:server_killed%🗡 %fuji:server_moved%🌍");
                }
            };

            public @NotNull Icon icon = new Icon();

            public class Icon {
                public boolean enable = true;

            }
        }

        public class Nametag {
            public boolean enable = false;
            public String update_cron = "* * * ? * *";

            public Style style = new Style();

            public class Style {
                public String text = "<#B1B2FF>%fuji:player_playtime%\uD83D\uDD25 %fuji:player_mined%⛏ %fuji:player_placed%\uD83D\uDD33 %fuji:player_killed%\uD83D\uDDE1 %fuji:player_moved%\uD83C\uDF0D\n<dark_green>%player:displayname_visual%";

                public Offset offset = new Offset();

                public class Offset {
                    public float x = 0f;
                    public float y = 0.2f;
                    public float z = 0f;
                }

                public Size size = new Size();

                public class Size {
                    public float height = 0f;
                    public float width = 0f;
                }

                public Scale scale = new Scale();

                public class Scale {
                    public float x = 1.0f;
                    public float y = 1.0f;
                    public float z = 1.0f;
                }

                public Brightness brightness = new Brightness();

                public class Brightness {
                    public boolean override_brightness = false;
                    public int block = 15;
                    public int sky = 15;
                }

                public Shadow shadow = new Shadow();

                public class Shadow {
                    public boolean shadow = false;
                    public float shadow_radius = 0f;
                    public float shadow_strength = 1f;
                }

                public Color color = new Color();

                public class Color {
                    public int background = 1073741824;
                    public byte text_opacity = -1;
                }

            }

            public Render render = new Render();

            public class Render {
                public boolean see_through_blocks = false;
                public float view_range = 1.0f;
            }

        }

        public class TeleportWarmup {
            public boolean enable = false;

            public int warmup_second = 3;

            public double interrupt_distance = 1d;

            public @NotNull Dimension dimension = new Dimension();

            public class Dimension {
                public @NotNull Set<String> list = new HashSet<>() {
                    {
                        this.add("minecraft:overworld");
                        this.add("minecraft:the_nether");
                        this.add("minecraft:the_end");
                    }
                };
            }
        }

        public class CommandCooldown {
            public boolean enable = false;

            public @NotNull HashMap<String, Long> regex2ms = new HashMap<>() {
                {
                    this.put("rw tp (overworld|the_nether|the_end)", 120 * 1000L);
                    this.put("chunks\\s*", 60 * 1000L);
                    this.put("download\\s*", 120 * 1000L);
                }
            };
        }

        public class CommandWarmup {
            public boolean enable = false;

            public @NotNull HashMap<String, Integer> regex2ms = new HashMap<>() {
                {
                    this.put("back", 3 * 1000);
                }
            };
        }

        public class TopChunks {
            public boolean enable = false;


            public @NotNull Top top = new Top();
            public int nearest_distance = 128;
            public boolean hide_location = true;
            public @NotNull HashMap<String, Integer> type2score = new HashMap<>() {
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

            public class Top {
                public int rows = 10;
                public int columns = 10;
            }
        }

        public class Chat {
            public boolean enable = false;

            public @NotNull String format = "<#B1B2FF>[%fuji:player_playtime%\uD83D\uDD25 %fuji:player_mined%⛏ %fuji:player_placed%\uD83D\uDD33 %fuji:player_killed%\uD83D\uDDE1 %fuji:player_moved%\uD83C\uDF0D]<reset> <<dark_green><click:suggest_command:'/msg %player:name% '><hover:show_text:'Time: %fuji:date%<newline><italic>Click to Message'>%player:displayname_visual%</hover></click></dark_green>> %message%";

            public @NotNull Rewrite rewrite = new Rewrite();
            public @NotNull MentionPlayer mention_player = new MentionPlayer();
            public @NotNull Display display = new Display();
            public @NotNull History history = new History();
            public @NotNull Spy spy = new Spy();

            public class History {
                public boolean enable = true;

                public int buffer_size = 50;
            }

            public class MentionPlayer {
                public @NotNull String sound = "entity.experience_orb.pickup";
                public float volume = 100f;
                public float pitch = 1f;
                public int repeat_count = 3;
                public int interval_ms = 1000;
            }

            public class Display {

                public boolean enable = true;

                public int expiration_duration_s = 3600;
            }

            public class Rewrite {
                public @NotNull List<RegexRewriteEntry> regex = new ArrayList<>() {
                    {
                        this.add(new RegexRewriteEntry("^BV(\\w{10})", "<underline><blue><hover:show_text:'$1'><click:open_url:'https://www.bilibili.com/video/BV$1'>bilibili $1</click></hover></blue></underline>"));
                        this.add(new RegexRewriteEntry("(?<=^|\\s)item(?=\\s|$)", "%fuji:item%"));
                        this.add(new RegexRewriteEntry("(?<=^|\\s)inv(?=\\s|$)", "%fuji:inv%"));
                        this.add(new RegexRewriteEntry("(?<=^|\\s)ender(?=\\s|$)", "%fuji:ender%"));
                        this.add(new RegexRewriteEntry("(?<=^|\\s)pos(?=\\s|$)", "%fuji:pos%"));
                        this.add(new RegexRewriteEntry("((https?)://[^\\s/$.?#].\\S*)", "<underline><blue><hover:show_text:'$1'><click:open_url:'$1'>$1</click></hover></blue></underline>"));
                    }
                };
            }

            public class Spy {
                public boolean output_unparsed_message_into_console = false;
            }

        }

        public class Skin {
            public boolean enable = false;

            public @NotNull Property default_skin = new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODYzMjc4ODA1NjYsInByb2ZpbGVJZCI6ImI3MzY3YzA2MjYxYzRlYjBiN2Y3OGY3YzUxNzBiNzQ4IiwicHJvZmlsZU5hbWUiOiJFbXB0eUlyb255Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NWZmZjI1ZDY2NzIwNmYyZTQ2ZDQ0MmNmMzU4YjNmMWVjMzYxMzgzOTE3NTFiYTZlZGY5NjVmZmM4M2I4NjAzIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=",
                    "PoUf4TsNx6SVHTWZJ6Iwk3acWyiDk84VeKBVcOeqimaSBAGYKfeuXRTFV8c9IBE9cjsRAVaTGC/mwRfHlcD/rmxeDDOkhsFVidr8UL+91afIO8d+EnyoBghmnbZonqpcjCv+nkxQ5SP93qTDelD3jd8xF1FAU97BBvrx0yK+QNn5rPg2RUGGoUZUg75KlEJds1dNftpHc8IyAHz/FQIywlkohu26ghOqFStjok4WPHD3ok0z7Kwcjk7u58PYf67TkEGnGbmxTUDlNbLmxUqjxCr4NshS+e3y3jRfJN0nP82dbYh/NP2Fx8m7pSMsQtm/Ta2MN7JC0Pm2yvZB/APNoNHVSZZ2SOITbPF/yAkIdHrk+ieCKqDbeuc8TFs2n+6FktYdwPXcqrK266CzlSTPycVZQeyrgrOI+fqU1HwCz+MgdlcsAdAoyuFlFPaVqDesI46YPsSJzA3C3CNhjvuebOn357U9Po82eSFAPYbtBPVNjiNgiqn5l+1x8ZVHImwpGv/toa5/fUyfMmlxijwG/C9gQ4mE+buutMn9nfE1y/AisU/2DWeFBESw3eRAICcmVVi875N8kT+Wja8WsbpDCw+pV2wZC3x3nEdOceAdXtDEb0oy3bQPW3TSZ+Wnp68qwSxjI/aDosqVuyyqqlm+w/irUmNHGL+t7g/kD932g0Q=");

            public @NotNull ArrayList<Property> random_skins = new ArrayList<>() {
                {
                    this.add(new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODYzMjc4ODA1NjYsInByb2ZpbGVJZCI6ImI3MzY3YzA2MjYxYzRlYjBiN2Y3OGY3YzUxNzBiNzQ4IiwicHJvZmlsZU5hbWUiOiJFbXB0eUlyb255Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NWZmZjI1ZDY2NzIwNmYyZTQ2ZDQ0MmNmMzU4YjNmMWVjMzYxMzgzOTE3NTFiYTZlZGY5NjVmZmM4M2I4NjAzIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=", "PoUf4TsNx6SVHTWZJ6Iwk3acWyiDk84VeKBVcOeqimaSBAGYKfeuXRTFV8c9IBE9cjsRAVaTGC/mwRfHlcD/rmxeDDOkhsFVidr8UL+91afIO8d+EnyoBghmnbZonqpcjCv+nkxQ5SP93qTDelD3jd8xF1FAU97BBvrx0yK+QNn5rPg2RUGGoUZUg75KlEJds1dNftpHc8IyAHz/FQIywlkohu26ghOqFStjok4WPHD3ok0z7Kwcjk7u58PYf67TkEGnGbmxTUDlNbLmxUqjxCr4NshS+e3y3jRfJN0nP82dbYh/NP2Fx8m7pSMsQtm/Ta2MN7JC0Pm2yvZB/APNoNHVSZZ2SOITbPF/yAkIdHrk+ieCKqDbeuc8TFs2n+6FktYdwPXcqrK266CzlSTPycVZQeyrgrOI+fqU1HwCz+MgdlcsAdAoyuFlFPaVqDesI46YPsSJzA3C3CNhjvuebOn357U9Po82eSFAPYbtBPVNjiNgiqn5l+1x8ZVHImwpGv/toa5/fUyfMmlxijwG/C9gQ4mE+buutMn9nfE1y/AisU/2DWeFBESw3eRAICcmVVi875N8kT+Wja8WsbpDCw+pV2wZC3x3nEdOceAdXtDEb0oy3bQPW3TSZ+Wnp68qwSxjI/aDosqVuyyqqlm+w/irUmNHGL+t7g/kD932g0Q="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYyMDIyMDc4MTQyNCwKICAicHJvZmlsZUlkIiA6ICJiYjdjY2E3MTA0MzQ0NDEyOGQzMDg5ZTEzYmRmYWI1OSIsCiAgInByb2ZpbGVOYW1lIiA6ICJsYXVyZW5jaW8zMDMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjVmMTcyMGM3ODBhNzk1OGI0MWYxNTNlNTA2OWRiNjg2MWJkMjgxYmU0MzJlN2JjNzk0MTE0YTdmNGVjNTJmZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "olnQih89nZxKFe0UzWiXU8+wlndGBClUqXxAabwEm6j15SZH9ue7Xd2OM2kRKBiHoqbT+2TSg4xG7cBeAeapVN4vpRP5NPujERl/JI41jYNhMb+DmskreS59fh0QfZPAxOpj/rmmAJVfNN1QblxRM3wlMGaEgS5TH9HfeehgLrBaaDM8/JAgnas4Yh6L0uRoNebjXHrhqgguVBMF3xsWpvpAPCzQCYX2vjCCF3WtOEy7EEUF4u5Lo4teQhr9yfnYGBc/ktE4I0MByqTaKrLqvF45n4jOShPP0RcmLh9JpOXyrScRuaUDhQ8bd8xhkWEb94HMzwznvDLNh1/nbNybCMb5GydYf51hJVfqjU5TMWID71F8FTTBJrCZDBRESFIP+QZ3czYP+urgzmfLgDmcoPIukMaHWLU6qFpTF0QazAgF4u5Fe4J6QEZSyZz0B2kqQG3vN1dXxLgHItjQbEeceChNYNjuZFOTleXzpYkg5/4Zqy6Oek3bMscTYY7IPBV56WiO8eGw5JYMfyDeM3iyh4ZxLEC3HDRtOTBHo7WxWPR/AUOU9HP9CdmKQbGThGAUuqlqRJzbg5XNRvKIcnngI329VZV5RmAnt+G5Vfy6uqBagpMQZ3720PXPG6H5q4SBuXmHt1ccKgJvQv9lTh20EymuIALTnCodr8qDbnRfdrI="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTYzODY4MjMwMjQ3MSwKICAicHJvZmlsZUlkIiA6ICI0ZjU2ZTg2ODk2OGU0ZWEwYmNjM2M2NzRlNzQ3ODdjOCIsCiAgInByb2ZpbGVOYW1lIiA6ICJDVUNGTDE1IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE0YjRlODI2OTJiOWRjYmY2YzEyOTJkNjJiNzMxN2MxMjRjNGMzOTQ2Y2FiMjFlY2E4NDJiNDJlNzBmMGMwMjIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==", "renIrkVFQUor3+5AeDYOcGtUnUpdk9/i5ANWFGCBTSVZjeKQ/t5xDEe9kqwsWawR/55N2+1Db2c7lIpHpJ4cGqtG7BzTm+TZNUgSOu0rG27DwxheiuGbYSMm/lQSiNi7FvRlhLXuxsYZ0nHhXKoeG4xW5PXaE/zjXeXR1hffnfR/ROanmK/m2nIbkfPo59wjc+ZTF3nxhX+tGay+7dy/Y6xqhyZ4ZnM1a9+z8hC8ERgXzUUczfhRaDPQcv9dEdpyQhlfJyEV6r6NBSpBVVNaZ2bGs+VyxrRVtr/nXigps1KtFXH3j+gBiNYJWu7LpDS+1DTezlP9qkbDUPSKuO1O913GDRdJxdcVn7HGYD3W6yGB0r6sDBvb7RYESMzafRIFbBjhJrJFi3/aQjxTuFSc66bUkDqNBGYQcXyUXP1wEuB22mwQABv2OZiFdXMMRDniSZvPsxoriDdAS+umHcrAgTApu13xLyJJa8tFBD9rpGxDDoUbnNJdzZSpjrgfu38Kgpa3pW45HY21eSOubQNdz7qBTBmQwVViuVoAqH9mM/HqeIrGzwdRJaOH3GsZRofr4zh9HVc+5o02W72d39BskA56ae8zjGza9sF1jhkgiaW1NH/zuu7LnfujjvvMcczrddv8P1r7yqsIwUrP0ObB+ylsCsrb6mAV5uqXuklS7e4="));
                    this.add(new Property("textures", "ewogICJ0aW1lc3RhbXAiIDogMTY0Nzg5MjUwMzUyMywKICAicHJvZmlsZUlkIiA6ICIxN2Q0ODA1ZDRmMTA0YTA5OWRiYzJmNzYzMDNjYmRkZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJnaWZ0bWV0b25uZXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ4MDU0OWEyNTJjYzI4ODJhNDUxMDc4NTUxMTNhMTg0NzJlNmI1YTVjNjE2YjI4NTc2Yjk4MjVjNDUzZGMzZSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9", "LtRvG/OteephxnjkSQfrdfbDUWUhoG1p4L2pIit1e7KF0UV/5BjgzfQUt+MXRrmJTGQVoJCZzHt+VFHT9UL1ptguabvM6sbBWaVmg7RPo1KKBYkmai+dP5ceCXPA2StVogKN+JjRwNF6Paw850IVha6h90It2VRk43/IbMzdSyTnMIH92WLb93BvENYX00yRCJY/m48tYECibH6FtX6vgqK6UgOgNVqW0g6Otuwx2Z4Pi0xUHn6i9gCayPrWSg6Y7cVQ5pM49t1A9tfN+Kt4J+sB63Ez0LSws3y/5MP/8sFUBQXKaZYpATK5dmBohZao9wroX9Ni9sADzPcGF3XAcHXZjRr2Qk6Y0AsukuAlXMPlDWFGxnVK+jElwcGEsx8g8cK5iufAuDAQLQSUJ6vgBUE8DndEfL+l8LpebRlgMT4kZIn75ZbhY4BU2zvs/fUp3mPhQjjkrTMqwofDFj6YxnZksUi+qwEIcRc3ysoFl6phMN5n2mRV6616l6DTSh6y7Vd6tT4s6UsfWlFUwC0gdaPrU0CSX96Afk+BSVceh6qxs0IJVBn1bBe0uDTwK1a3yUOMXvjosG0L8jzpNY7sGE6ybwHUxPMaNIilqvnhO2NdSgsNHVLlYHzh0jUMs5ap4U+9fAEYYr0OpkqgZ7l6aQeV4ME96RrGqZmEkxJPjas="));
                    this.add(new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODc4MDcxMTkyNzEsInByb2ZpbGVJZCI6IjIzZjFhNTlmNDY5YjQzZGRiZGI1MzdiZmVjMTA0NzFmIiwicHJvZmlsZU5hbWUiOiIyODA3Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iMjI4MTk0OWZlZDc2M2Q3OWYwODZiNGU4MjE0ZGVjNTdiZDM4NzgzODhkOTJmYWQ4NmRjMzQxNzE2MWNkYjJkIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=", "N/b/lSK6Y3Wqm4lTj47YHbec9yVAj7XmDjfWhVAa033UNA30U8o+2pTY0aVDAzFut624iC3xjqMzBlXt7SczsT0w8EV+MnW51V6aPlanj5SQ6zVwB20TdhhAzBNvIQbvo4x4BL99ZpyBJMBRcCVEehjaD3rgshBxH6t2z7WzzYM1cij/5egedjhm8ek8DMdYYakN6DWIOWDv05VQSiWRMhitSI2sqJMTYKaJcLph7/56Ke5zRNtA2mwEcdB+GnDPkeEINzx3A0WG/vOS3iYL8L4T5Dv1GzBlq9s10R1K4Ks5TQLhVJ4Rp2S4COLvvWsgREHQVf6NEIOG2ww4wqTi/xmHni2d6TM9K+vtLSBE7umEvLeOzp8oqbQvtD1ipa0iatR8lEXU1bcGITtwZi+i+zLeOIfx2592XevcOGwTuvhBBM53rN5suLnpcGFIT5TuOQrFinT1+vXoE2D/UkDll8nvtGzJyqFgSSFDrvf0e6ZkbFlIQRoJGkfhnDLON2aEycOe9EcD+NiLDXQc9++j+3Kl5QFyze3xd21+ConIZRGDXKqvoEhfp1ovR7ND76IVOAoGMcDT4N+n+NWdXIilipux3gQ5UZkALw1ocFzhEZY9pCYw9e7XGQRh27N/RYns+sSI1qXbtBbl0FCl7X5efvsJLWId0JuEag5f5RAYYYo="));
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

            public int sample_time_ms = 60 * 1000 * 60;
            public int sample_distance_limit = 512;
            public int sample_counter_top_n = 20;
        }

        public class WorldDownloader {
            public boolean enable = false;

            public @NotNull String url_format = "http://example.com:%port%%path%";

            public int port = 22222;

            public int bytes_per_second_limit = 128 * 1000;

            public int context_cache_size = 5;
        }

        public class Disabler {
            public boolean enable = false;

            public @NotNull ChatSpeedDisabler chat_speed_disabler = new ChatSpeedDisabler();
            public @NotNull MoveSpeedDisabler move_speed_disabler = new MoveSpeedDisabler();
            public @NotNull MoveWronglyDisabler move_wrongly_disabler = new MoveWronglyDisabler();
            public @NotNull MaxPlayerDisabler max_player_disabler = new MaxPlayerDisabler();

            public class ChatSpeedDisabler {
                public boolean enable = false;
            }

            public class MoveSpeedDisabler {
                public boolean enable = false;
            }

            public class MoveWronglyDisabler {
                public boolean enable = false;
            }

            public class MaxPlayerDisabler {
                public boolean enable = false;
            }
        }

        public class DeathLog {
            public boolean enable = false;
        }

        public class Echo {
            public boolean enable = false;

            public @NotNull SendMessage send_message = new SendMessage();
            public @NotNull SendBroadcast send_broadcast = new SendBroadcast();
            public @NotNull SendActionBar send_actionbar = new SendActionBar();
            public @NotNull SendTitle send_title = new SendTitle();
            public @NotNull SendToast send_toast = new SendToast();
            public @NotNull SendChat send_chat = new SendChat();

            public class SendMessage {
                public boolean enable = true;
            }

            public class SendBroadcast {
                public boolean enable = true;
            }

            public class SendActionBar {
                public boolean enable = true;
            }

            public class SendTitle {
                public boolean enable = true;
            }

            public class SendToast {
                public boolean enable = true;
            }

            public class SendChat {
                public boolean enable = true;
            }
        }

        public class View {
            public boolean enable = false;
        }

        public class Placeholder {
            public boolean enable = false;
        }

        public class Pvp {
            public boolean enable = false;
        }

        public class Whitelist {
            public boolean enable = false;
        }

        public class CommandPermission {
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

        public class CommandScheduler {
            public boolean enable = false;
        }

        public class Config {
            public boolean enable = false;
        }

        public class Tester {
            public boolean enable = false;
        }

        public class Language {
            public boolean enable = false;
        }

        public class Afk {
            public boolean enable = false;

            public @NotNull String format = "<gray>[AFK] %player:displayname_visual%";

            public @NotNull AfkChecker afk_checker = new AfkChecker();

            public class AfkChecker {
                public @NotNull String cron = "0 0/5 * ? * *";
            }

            public AfkEvent afk_event = new AfkEvent();

            public class AfkEvent {
                public List<String> on_enter_afk = new ArrayList<>() {
                    {
                        this.add("send-broadcast <gold>Player %player:name% is now afk");

                    }
                };

                public List<String> on_leave_afk = new ArrayList<>() {
                    {
                        this.add("send-broadcast <gold>Player %player:name% is no longer afk");
                        this.add("effect give %player:name% minecraft:absorption 5 4");
                    }
                };
            }

            public AfkEffect effect = new AfkEffect();

            public class AfkEffect {
                public boolean enable = true;

                public boolean invulnerable = true;

                public boolean targetable = false;

                public boolean moveable = false;
            }

        }

        public class Rtp {

            public boolean enable = false;

            public @NotNull Setup setup = new Setup();

            public class Setup {
                public @NotNull List<TeleportSetup> dimension = new ArrayList() {

                    {
                        this.add(new TeleportSetup("minecraft:overworld", 0, 0, false, 1000, 5000, -64, 320, 16));
                        this.add(new TeleportSetup("minecraft:the_nether", 0, 0, false, 1000, 5000, 0, 128, 16));
                        this.add(new TeleportSetup("minecraft:the_end", 0, 0, false, 1000, 5000, 0, 256, 16));
                        this.add(new TeleportSetup("fuji:overworld", 0, 0, false, 1000, 5000, -64, 320, 16));
                        this.add(new TeleportSetup("fuji:the_nether", 0, 0, false, 1000, 5000, 0, 128, 16));
                        this.add(new TeleportSetup("fuji:the_end", 0, 0, false, 0, 48, 0, 256, 16));
                    }

                };
            }


        }

        public class CommandInteractive {
            public boolean enable = false;
        }

        public class Home {
            public boolean enable = false;
        }

        public class SystemMessage {
            public boolean enable = false;

            public @NotNull Map<String, String> key2value = new HashMap<>() {
                {
                    this.put("commands.seed.success", "<rainbow> Seeeeeeeeeeed: %s");
                }
            };

        }

        public class CommandAlias {
            public boolean enable = false;
            public @NotNull List<CommandAliasEntry> alias = new ArrayList<>() {
                {
                    this.add(new CommandAliasEntry(List.of("r"), List.of("reply")));
                    this.add(new CommandAliasEntry(List.of("sudo"), List.of("run", "as", "fake-op")));
                    this.add(new CommandAliasEntry(List.of("i", "want", "to", "modify", "chat"), List.of("chat", "format")));
                }
            };
        }

        public class CommandAttachment {
            public boolean enable = false;
        }

        public class CommandRewrite {
            public boolean enable = false;
            public @NotNull List<RegexRewriteEntry> regex = new ArrayList<>() {
                {
                    this.add(new RegexRewriteEntry("home", "home tp default"));
                }
            };

        }

        public class Multiplier {
            public boolean enable = false;

        }

        public class AntiBuild {
            public boolean enable = false;

            public @NotNull Anti anti = new Anti();

            public class Anti {
                public @NotNull Break break_block = new Break();
                public @NotNull Place place_block = new Place();
                public @NotNull InteractItem interact_item = new InteractItem();
                public @NotNull InteractBlock interact_block = new InteractBlock();
                public @NotNull InteractEntity interact_entity = new InteractEntity();

                public class Break {
                    public @NotNull Set<String> id = new HashSet<>() {
                        {
                            this.add("minecraft:gold_block");
                        }
                    };
                }

                public class Place {
                    public @NotNull Set<String> id = new HashSet<>() {
                        {
                            this.add("minecraft:tnt");
                        }
                    };
                }

                public class InteractItem {
                    public @NotNull Set<String> id = new HashSet<>() {
                        {
                            this.add("minecraft:lava_bucket");
                        }
                    };
                }

                public class InteractBlock {
                    public @NotNull Set<String> id = new HashSet<>() {
                        {
                            this.add("minecraft:lever");
                        }
                    };
                }

                public class InteractEntity {
                    public @NotNull Set<String> id = new HashSet<>() {
                        {
                            this.add("minecraft:villager");
                        }
                    };

                }
            }
        }

        public class Color {
            public boolean enable = false;

            public @NotNull Sign sign = new Sign();
            public @NotNull Anvil anvil = new Anvil();

            public class Sign {
                public boolean enable = true;
            }

            public class Anvil {
                public boolean enable = true;
            }
        }

        public class Functional {
            public boolean enable = false;

            public @NotNull Workbench workbench = new Workbench();
            public @NotNull Enchantment enchantment = new Enchantment();
            public @NotNull GrindStone grindstone = new GrindStone();
            public @NotNull StoneCutter stonecutter = new StoneCutter();
            public @NotNull Anvil anvil = new Anvil();
            public @NotNull Cartography cartography = new Cartography();
            public @NotNull EnderChest enderchest = new EnderChest();
            public @NotNull Smithing smithing = new Smithing();
            public @NotNull Loom loom = new Loom();

            public class Workbench {
                public boolean enable = false;
            }

            public class Enchantment {

                public boolean enable = false;

                public @NotNull OverridePower override_power = new OverridePower();

                public class OverridePower {

                    public boolean enable = false;
                    public int power_provider_amount = 15;
                }
            }

            public class GrindStone {
                public boolean enable = false;
            }

            public class StoneCutter {

                public boolean enable = false;
            }

            public class Anvil {

                public boolean enable = false;
            }

            public class Cartography {
                public boolean enable = false;
            }

            public class EnderChest {
                public boolean enable = false;
            }

            public class Smithing {
                public boolean enable = false;
            }

            public class Loom {
                public boolean enable = false;
            }

        }

        public class Gameplay {
            public boolean enable = false;

            public @NotNull MultiObsidianPlatform multi_obsidian_platform = new MultiObsidianPlatform();
            public @NotNull Carpet carpet = new Carpet();

            public class Carpet {
                public boolean enable = false;

                public @NotNull FakePlayerManager fake_player_manager = new FakePlayerManager();
                public @NotNull BetterInfo better_info = new BetterInfo();

                public class FakePlayerManager {
                    public boolean enable = false;

                    public @NotNull ArrayList<List<Integer>> caps_limit_rule = new ArrayList<>() {
                        {
                            this.add(List.of(1, 0, 2));
                        }
                    };

                    public int renew_duration_ms = 1000 * 60 * 60 * 12;

                    public @NotNull String transform_name = "_fake_%name%";

                    public boolean use_local_random_skins_for_fake_player = true;
                }

                public class BetterInfo {
                    public boolean enable = false;
                }
            }


            public class MultiObsidianPlatform {
                public boolean enable = false;
                public double factor = 4;
            }
        }


        public class CommandMeta {
            public boolean enable = false;

            public @NotNull Run run = new Run();
            public @NotNull ForEach for_each = new ForEach();
            public @NotNull Chain chain = new Chain();
            public @NotNull Delay delay = new Delay();
            public @NotNull Json json = new Json();
            public @NotNull Attachment attachment = new Attachment();
            public @NotNull Shell shell = new Shell();

            public class Run {
                public boolean enable = false;
            }

            public class ForEach {
                public boolean enable = false;
            }

            public class Chain {
                public boolean enable = false;
            }

            public class Delay {
                public boolean enable = false;
            }

            public class Json {
                public boolean enable = false;
            }

            public class Attachment {
                public boolean enable = false;
            }

            public class Shell {
                public @NotNull String enable_warning = "ENABLE THIS MODULE IS POTENTIAL TO HARM YOUR COMPUTER! YOU NEED TO CHANGE THIS FIELD INTO `CONFIRM` TO ENABLE THIS MODULE";
                public boolean enable = false;
            }

        }

        public class CommandToolbox {
            public boolean enable = false;
            public @NotNull Bed bed = new Bed();
            public @NotNull Extinguish extinguish = new Extinguish();
            public @NotNull Feed feed = new Feed();
            public @NotNull Fly fly = new Fly();
            public @NotNull God god = new God();
            public @NotNull Hat hat = new Hat();
            public @NotNull Sit sit = new Sit();
            public @NotNull Heal heal = new Heal();
            public @NotNull Lore lore = new Lore();
            public @NotNull More more = new More();
            public @NotNull Ping ping = new Ping();
            public @NotNull Realname realname = new Realname();
            public @NotNull Nickname nickname = new Nickname();
            public @NotNull Repair repair = new Repair();
            public @NotNull Reply reply = new Reply();
            public @NotNull Seen seen = new Seen();
            public @NotNull Suicide suicide = new Suicide();
            public @NotNull Top top = new Top();
            public @NotNull TrashCan trashcan = new TrashCan();
            public @NotNull Tppos tppos = new Tppos();
            public @NotNull Warp warp = new Warp();
            public Burn burn = new Burn();
            public HelpOp help_op = new HelpOp();
            public Near near = new Near();
            public Jump jump = new Jump();
            public Compass compass = new Compass();

            public class Bed {
                public boolean enable = false;
            }

            public class Extinguish {
                public boolean enable = false;
            }

            public class Feed {
                public boolean enable = false;
            }

            public class Fly {
                public boolean enable = false;
            }

            public class God {
                public boolean enable = false;
            }

            public class Hat {
                public boolean enable = false;
            }

            public class Heal {
                public boolean enable = false;
            }

            public class Lore {
                public boolean enable = false;
            }

            public class Sit {
                public boolean enable = false;
                public boolean allow_right_click_sit = true;
                public boolean allow_sneaking_to_sit = false;
                public boolean require_empty_hand_to_sit = false;
                public boolean require_no_opaque_block_above_to_sit = false;
                public int max_distance_to_sit = -1;
            }

            public class More {
                public boolean enable = false;
            }

            public class Ping {
                public boolean enable = false;
            }

            public class Realname {
                public boolean enable = false;

            }

            public class Nickname {
                public boolean enable = false;
            }

            public class Repair {
                public boolean enable = false;

            }

            public class Reply {
                public boolean enable = false;
            }

            public class Seen {
                public boolean enable = false;
            }

            public class Suicide {
                public boolean enable = false;
            }

            public class Top {
                public boolean enable = false;
            }

            public class TrashCan {
                public boolean enable = false;
            }

            public class Tppos {
                public boolean enable = false;
            }

            public class Warp {
                public boolean enable = false;
            }

            public class Burn {
                public boolean enable = false;
            }

            public class HelpOp {
                public boolean enable = false;
            }

            public class Near {
                public boolean enable = false;
            }

            public class Jump {
                public boolean enable = false;
            }

            public class Compass {
                public boolean enable = false;
            }
        }

        public class TabList {
            public boolean enable = false;
            public @NotNull String update_cron = "* * * ? * *";
            public @NotNull Style style = new Style();
            public @NotNull Sort sort = new Sort();
            public @NotNull Faker faker = new Faker();

            public class Style {
                public @NotNull List<String> header = new ArrayList<>() {
                    {
                        this.add("<#FFA1F5>PlayerList<newline>------%server:online%/%server:max_players%------");
                    }
                };
                public @NotNull List<String> body = new ArrayList<>() {
                    {
                        this.add("<rainbow>%player:displayname_visual%");
                    }
                };
                public @NotNull List<String> footer = new ArrayList<>() {
                    {
                        this.add("<#FFA1F5>-----------------<newline>TPS: %server:tps_colored% PING: %player:ping_colored%<newline><rainbow>Memory: %server:used_ram%/%server:max_ram% MB<newline>%fuji:rotate Welcome to the server. %");
                    }

                };
            }

            public class Sort {
                public boolean enable = false;

                public SyncGameProgile sync_game_profile = new SyncGameProgile();

                public class SyncGameProgile {
                    public boolean enable = true;
                }
            }

            public class Faker {
                public boolean enable = false;
                public @NotNull Ping ping = new Ping();

                public class Ping {
                    public int min_ping = 15;
                    public int max_ping = 35;
                }
            }
        }

        public class Kit {
            public boolean enable = false;
        }

        public class TempBan {
            public boolean enable = false;
        }

        public class CommandEvent {

            public boolean enable = false;

            public @NotNull Event event = new Event();

            public class Event {

                public @NotNull OnPlayerDeath on_player_death = new OnPlayerDeath();
                public @NotNull AfterPlayerBreakBlock after_player_break_block = new AfterPlayerBreakBlock();
                public @NotNull AfterPlayerPlaceBlock after_player_place_block = new AfterPlayerPlaceBlock();
                public @NotNull AfterPlayerRespawn after_player_respawn = new AfterPlayerRespawn();
                public @NotNull AfterPlayerChangeWorld after_player_change_world = new AfterPlayerChangeWorld();
                public @NotNull OnPlayerFirstJoined on_player_first_joined = new OnPlayerFirstJoined();
                public @NotNull OnPlayerJoined on_player_joined = new OnPlayerJoined();
                public @NotNull OnPlayerLeft on_player_left = new OnPlayerLeft();

                public class OnPlayerDeath {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("send-message %player:name% you just die.");
                        }
                    };
                }

                public class AfterPlayerBreakBlock {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("send-message %player:name% you just break a block.");
                            this.add("experience add %player:name% %fuji:random 2 8%");
                        }
                    };
                }

                public class AfterPlayerPlaceBlock {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("send-message %player:name% you just place a block.");
                        }
                    };
                }

                public class AfterPlayerRespawn {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("give %player:name% minecraft:apple 8");
                        }
                    };
                }

                public class AfterPlayerChangeWorld {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("send-message %player:name% You are in %world:id% now!");
                        }
                    };
                }

                public class OnPlayerFirstJoined {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("send-broadcast <rainbow>welcome new player %player:name% to join us!");
                        }
                    };
                }

                public class OnPlayerJoined {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("send-message %player:name% welcome to the server.");
                        }
                    };
                }

                public class OnPlayerLeft {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("send-broadcast %player:name% left the server.");
                        }
                    };
                }
            }
        }

        public class Cleaner {

            public boolean enable = false;

            public String cron = "0 * * ? * * *";

            public Map<String, Integer> key2age = new HashMap<>() {
                {
                    this.put("block.minecraft.sand", 1200);
                    this.put("item.minecraft.ender_pearl", 1200);
                    this.put("block.minecraft.white_carpet", 1200);
                    this.put("block.minecraft.cobblestone", 1200);
                }
            };

            public Ignore ignore = new Ignore();

            public class Ignore {
                public boolean ignoreItemEntity = false;
                public boolean ignoreLivingEntity = true;
                public boolean ignoreNamedEntity = true;
                public boolean ignoreEntityWithVehicle = true;
                public boolean ignoreEntityWithPassengers = true;
                public boolean ignoreGlowingEntity = true;
                public boolean ignoreLeashedEntity = true;
            }
        }

    }
}
