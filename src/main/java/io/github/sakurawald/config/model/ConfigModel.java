package io.github.sakurawald.config.model;


import com.mojang.authlib.properties.Property;
import io.github.sakurawald.config.annotation.Documentation;
import io.github.sakurawald.module.common.structure.RegexRewriteEntry;
import io.github.sakurawald.module.common.structure.TeleportSetup;
import io.github.sakurawald.module.initializer.command_alias.structure.CommandAliasEntry;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("ALL")
@Documentation("""
        Welcome to fuji-fabric 's official documentation.
                
        Tips:
        - The `quote bar` on the left side of your browser = the strucutre nested level. You can see which level you are in.
        - You can press `CTRL + F` keys in your keyboard, and search any `configuration key` that you want to konw.
        - If anything is unclear, please create an issue in github.
                
        Note:
        - Most of the configuration files use `.json` file format, so you might want to use a better `text-editor` to highlight the file. 
          A good editor for json is `visual studio code`: [Visual Studio Code - Web Online Editor](https://vscode.dev/)
          
        """)
public class ConfigModel {

    public @NotNull Common common = new Common();
    public @NotNull Modules modules = new Modules();

    @Documentation("""
            Common options for this mod, which will influence `all modules`.
            """)
    public class Common {

        public @NotNull Quartz quartz = new Quartz();
        public @NotNull Backup backup = new Backup();
        public @NotNull Language language = new Language();

        @Documentation("""
                Fuji use `quartz` library as scheduler, all the timer are managed by quartz.
                                
                Quartz library use a language called `cron language` to define when to trigger a job.
                See: [cron language generator](https://www.freeformatter.com/cron-expression-generator-quartz.html)
                """)
        public class Quartz {
            @Documentation("""
                    Logger level for quartz.
                                        
                    Logger levels: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL
                    - Set to `OFF` to supress all the messages from fuji.
                    - Set to `ALL` to display all the messages from fuji.
                                        
                    Note:
                    - It's recommended to set at least `WARN`, to avoid `console spam`
                    """)
            public @NotNull String logger_level = "WARN";
        }

        @Documentation("""
                Fuji will backup the folder `config/fuji` automatically before it loads any module.
                """)
        public class Backup {

            @Documentation("How many `backup` should we keep?")
            public int max_slots = 15;
            @Documentation("""
                    The list of `path resolver` to skip in backup.
                                        
                    Insert `head` means skip the folder `config/fuji/head`.
                    """)
            public @NotNull List<String> skip = new ArrayList<>() {
                {
                    this.add("head");
                }
            };
        }

        @Documentation("""
                The default language to use.
                                
                Fuji also supports multi-language based on player's client-side language if the server is able to do so.
                You need to enable `language module` to let fuji respect client-side's language settings.
                Also, if the server can't support client-side's language, it will fallback to the `deafult_language`
                """)
        public class Language {
            public @NotNull String default_language = "en_us";
        }
    }

    @Documentation("""
            A module means a standalone unit to provide a purpose.
                
            All the module can work standalone, you can enable or disable `any module` if you like.
                
            Some modules can work together to achieve more purpose. 
            e.g. the `placeholder module` can provides some placeholders for `chat module`, `motd module` and other modules to use.
                        
            Q: How can I konw which is a `module` ?
            A: All the modules will has an option called `enable`, or you can see a `module` tag right in the field.
                        
            """)
    public class Modules {
        public @NotNull Config config = new Config();
        public @NotNull Language language = new Language();
        public @NotNull Chat chat = new Chat();
        public @NotNull Placeholder placeholder = new Placeholder();
        public @NotNull MOTD motd = new MOTD();
        public @NotNull TabList tab_list = new TabList();
        public @NotNull Tpa tpa = new Tpa();
        public @NotNull Back back = new Back();
        public @NotNull Home home = new Home();
        public @NotNull Pvp pvp = new Pvp();
        public @NotNull Afk afk = new Afk();
        public @NotNull Rtp rtp = new Rtp();
        public @NotNull Works works = new Works();
        public @NotNull DeathLog deathlog = new DeathLog();
        public @NotNull Functional functional = new Functional();
        public @NotNull SystemMessage system_message = new SystemMessage();
        public @NotNull Cleaner cleaner = new Cleaner();
        public @NotNull CommandScheduler command_scheduler = new CommandScheduler();
        public @NotNull CommandPermission command_permission = new CommandPermission();
        public @NotNull CommandRewrite command_rewrite = new CommandRewrite();
        public @NotNull CommandAlias command_alias = new CommandAlias();
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
        public @NotNull CommandMeta command_meta = new CommandMeta();

        @Documentation("""
                This module allows you to create extra `dimension` of a specific `dimension type`.
                                
                Command: /world
                                
                Example 1
                To create another the_nether world: `/world create my_nether minecraft:the_nether`
                  - This will create anohter `dimension` named `fuji:my_nether`, whose `dimension type` is `minecraft:the_nether`
                                
                Example 2
                Delete the extra dimension: `/world delete fuji:my_nether`
                                
                Example 3
                Reset the extra dimension with random seed: `/world reset fuji:my_nether`
                                
                Tips:
                - The `/world tp` command use the `setup` list defined by `rtp module`.
                - If you want to run `/world reset ...` command automatically, just use `command scheduler module`
                                
                Note:
                - What is the difference between `world`, `dimension` and `dimension type` ?
                  Well, in the early stage of minecraft, it only support single-dimension, which means `1 world` only contains `1 dimension`.
                  And now, `1 world` can support `multi dimension`. Sometimes, you will see `world` and `dimension` means the same thing.
                  But clearer, we say: 1 `world` can contains 1 or more `dimension`, and each `dimension` has its `dimension type`.
                  
                  Usually, you can say a mod adds `extra dimension type` and `create extra dimension with that dimension type`  instead of `extra world`
                  See also: https://minecraft.wiki/w/Dimension_definition
                  See also: https://minecraft.wiki/w/Dimension_type
                             
                - In vanilla minecraft, 1 world contains 3 dimensions (minecraft:overworld, minecraft:the_nether, minecraft:the_end)
                  You can see the `dimension` of a `world` in `world/level.dat` file.
                - `dimension type` is used to create `dimension`, there are 4 `dimension type` in vanilla minecraft: `minecraft:overworld`, `minecraft:overworld_caves`, `minecraft:the_nether` and `minecraft:the_end`
                - In order to create extra dimensions of a `dimension type`, you need to at least exist one dimension of that dimension type.
                - Instead of writing data into the file `world/level.dat`, fuji will load the extra dimensions in game dynamically.
                - The file `server.properties` is used for the default `world properties` of extra dimensions
                                
                """)
        public class World {
            public boolean enable = false;

            public @NotNull Blacklist blacklist = new Blacklist();

            @Documentation("""
                    The dimensions in the `blacklist` will not be operated by this module.
                                        
                    Use `blacklist` to avoid mis-operation.
                    """)
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

        @Documentation("""
                This module customs your MOTD in server-list.
                """)
        public class MOTD {
            public boolean enable = false;

            @Documentation("""
                    Fuji will `randomly` pick a motd each time the player refresh server list.
                                        
                    Tips:
                    - You may need to enable `placeholder` to support some placeholders.
                    """)
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

        @Documentation("""
                This module adds a warmup cooldown before player-teleporatation.
                                
                The teleportation will be cancelled if:
                1. the player runs too far.
                2. the player gets damage.
                3. the player is in combat.
                """)
        public class TeleportWarmup {
            public boolean enable = false;

            @Documentation("The second to wait before the teleportation.")
            public int warmup_second = 3;

            @Documentation("How far should we cancel the teleportation.")
            public double interrupt_distance = 1d;

            public @NotNull Dimension dimension = new Dimension();

            @Documentation("""
                    Only allowed in the following dimensions.
                                        
                    Note:
                      - Some other mods will add extra `dimension` (like, the mod `the-bumblezone-fabric`). Their dimension portal will work in a different way, so `teleport warmup module` may not compatibility with these mods.
                      
                        In the default options, we only allow teleport warmup works in the `vanilla miencraft` dimensions.
                                        
                    """)
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

        @Documentation("""
                This module provides a cooldown for command usage.
                                
                Use-case: use this module to avoid some heavy cost commands.
                """)
        public class CommandCooldown {
            public boolean enable = false;

            @Documentation("""
                    Use `regex language` to define issued command cooldown.
                                        
                    Note:
                    - For each player, each command has its cooldown.
                    - You may want to use some editor for `regex language`. 
                        See regex editor https://regexr.com/
                        See regex editor https://regex101.com/
                    """)
            public @NotNull HashMap<String, Long> regex2ms = new HashMap<>() {
                {
                    this.put("rw tp (overworld|the_nether|the_end)", 120 * 1000L);
                    this.put("chunks\\s*", 60 * 1000L);
                    this.put("download\\s*", 120 * 1000L);
                }
            };
        }

        @Documentation("""
                This module provides `warmup` for `command usage`.
                                
                Note:
                - `command warmup` is before `command usage`, while `command cooldown` is after `command usage`.
                """)
        public class CommandWarmup {
            public boolean enable = false;

            @Documentation("See `command_cooldown module`")
            public @NotNull HashMap<String, Integer> regex2ms = new HashMap<>() {
                {
                    this.put("back", 3 * 1000);
                }
            };
        }

        @Documentation("""
                This module provides the `/chunks` command, which shows the `top laggy chunks` in the server. 
                                
                The output is a score list, each score means a chunk, and how laggy the chunk is. (Large score means more laggy)
                """)
        public class TopChunks {
            public boolean enable = false;


            public @NotNull Top top = new Top();
            @Documentation("For a chunk, how much the radius used to search `the nearest player` around the chunk.")
            public int nearest_distance = 128;
            @Documentation("""
                    Should we hide the chunk-position for a laggy-chunk?
                                        
                    Hide chunk location to avoid grief or privacy purpose. 
                    """)
            public boolean hide_location = true;
            @Documentation("""
                    The dict to define how laggy a type(entity/entity_block) should be.
                                
                    For example: 
                    `this.put("entity.minecraft.zombie", 4);` means there are 15 zombies inside a chunk,
                    then the chunk gets score 15 * 4 = 60
                                
                    Any other types not inside the dict used the score defined for type `default`
                    """)
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

            @Documentation("The `top chunks` to show in `/chunks` command")
            public class Top {
                public int rows = 10;
                public int columns = 10;
            }
        }

        @Documentation("""
                This module provides chat custom. (You might also want to enable `placeholder module`)
                                
                Command: /chat
                                
                Feature:
                - You can create your own `regex transformaer` to repalce the input message.
                - You can insert any `placeholder` like `%world:name%` in the chat message. (See more placeholders in: https://placeholders.pb4.eu/user/default-placeholders/)
                - You can insert player's prefix and suffix. Just insert `%fuji:player_prefix%` and `%fuji:player_suffix%`. 
                    Requires `luckperms` installed. See also: https://luckperms.net/wiki/Prefixes,-Suffixes-&-Meta
                      After you installed `luckperms` mod, just issue `/lp group default meta setprefix <yellow>[awesome]` to assign prefix.
                      Don't forget to change the format of `Chat module`, and issue `/fuji reload`
                - You can insert `item`, `inv` and `ender` to display your `item in your hand`, `your inventory` and `your enderchest`
                - You can insert `Steve` to mention another player named `Steve`.
                - You can insert `pos` to show the position.
                - You can use `markdown language` to define simple format.
                - You can use `mini-message language` to define complex format.
                    See: https://docs.advntr.dev/minimessage/format.html
                    See: https://placeholders.pb4.eu/user/quicktext
                - Besides the `server chat format`, each player can use `/chat format set` command to set their `per-player chat format`
                - This module doesn't `cancel` the vanilla chat events, so it can work with `other chat realvent mods`.
                    
                Placeholder:
                - %fuji:item%
                - %fuji:inv%
                - %fuji:ender%
                - %fuji:pos%
                - %fuji:player_prefix%
                - %fuji:player_suffix%
                                
                """)
        public class Chat {
            public boolean enable = false;

            @Documentation("""
                    The server chat format for all players.
                    """)
            public @NotNull String format = "<#B1B2FF>[%fuji:player_playtime%\uD83D\uDD25 %fuji:player_mined%⛏ %fuji:player_placed%\uD83D\uDD33 %fuji:player_killed%\uD83D\uDDE1 %fuji:player_moved%\uD83C\uDF0D]<reset> <<dark_green><click:suggest_command:'/msg %player:name% '><hover:show_text:'Time: %fuji:date%<newline><italic>Click to Message'>%player:displayname_visual%</hover></click></dark_green>> %message%";

            public @NotNull Rewrite rewrite = new Rewrite();
            public @NotNull MentionPlayer mention_player = new MentionPlayer();
            public @NotNull Display display = new Display();
            public @NotNull History history = new History();
            public @NotNull Spy spy = new Spy();

            @Documentation("""
                    New joined players can see the historical chat messages.
                    """)
            public class History {
                @Documentation("How many chat messages should we save, so that we can send for a new-joined player.")
                public int buffer_size = 50;
            }

            @Documentation("""
                    If you insert `Steve` in chat message, then the player named `Steve` will get audio mention.
                    """)
            public class MentionPlayer {
                @Documentation("You can query all the `sound identifier` using `/playsound ...` command.")
                public @NotNull String sound = "entity.experience_orb.pickup";
                public float volume = 100f;
                public float pitch = 1f;
                @Documentation("The sound repeat count.")
                public int repeat_count = 3;
                @Documentation("The interval between each repeat.")
                public int interval_ms = 1000;
            }

            @Documentation("""
                    You can insert `item`, `inv` and `ender` in message to `display` something with other players.
                    """)
            public class Display {

                @Documentation("""
                        For each display data, how long should we save in the memory.
                        Note that if a player shares its inventory items, then fuji will save a copy of his inventory data in the memory.
                        """)
                public int expiration_duration_s = 3600;
            }

            @Documentation("""
                    The `regex language` list used to `rewrite` the `player chat message`.
                                        
                    You can use `regex language` to transform player's chat input (only chat message, no command usage).
                    """)
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

        @Documentation("""
                This module provides player skin management.
                """)
        public class Skin {
            public boolean enable = false;

            @Documentation("The `default skin` used for player who has no skin set.")
            public @NotNull Property default_skin = new Property("textures", "eyJ0aW1lc3RhbXAiOjE1ODYzMjc4ODA1NjYsInByb2ZpbGVJZCI6ImI3MzY3YzA2MjYxYzRlYjBiN2Y3OGY3YzUxNzBiNzQ4IiwicHJvZmlsZU5hbWUiOiJFbXB0eUlyb255Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NWZmZjI1ZDY2NzIwNmYyZTQ2ZDQ0MmNmMzU4YjNmMWVjMzYxMzgzOTE3NTFiYTZlZGY5NjVmZmM4M2I4NjAzIiwibWV0YWRhdGEiOnsibW9kZWwiOiJzbGltIn19fX0=",
                    "PoUf4TsNx6SVHTWZJ6Iwk3acWyiDk84VeKBVcOeqimaSBAGYKfeuXRTFV8c9IBE9cjsRAVaTGC/mwRfHlcD/rmxeDDOkhsFVidr8UL+91afIO8d+EnyoBghmnbZonqpcjCv+nkxQ5SP93qTDelD3jd8xF1FAU97BBvrx0yK+QNn5rPg2RUGGoUZUg75KlEJds1dNftpHc8IyAHz/FQIywlkohu26ghOqFStjok4WPHD3ok0z7Kwcjk7u58PYf67TkEGnGbmxTUDlNbLmxUqjxCr4NshS+e3y3jRfJN0nP82dbYh/NP2Fx8m7pSMsQtm/Ta2MN7JC0Pm2yvZB/APNoNHVSZZ2SOITbPF/yAkIdHrk+ieCKqDbeuc8TFs2n+6FktYdwPXcqrK266CzlSTPycVZQeyrgrOI+fqU1HwCz+MgdlcsAdAoyuFlFPaVqDesI46YPsSJzA3C3CNhjvuebOn357U9Po82eSFAPYbtBPVNjiNgiqn5l+1x8ZVHImwpGv/toa5/fUyfMmlxijwG/C9gQ4mE+buutMn9nfE1y/AisU/2DWeFBESw3eRAICcmVVi875N8kT+Wja8WsbpDCw+pV2wZC3x3nEdOceAdXtDEb0oy3bQPW3TSZ+Wnp68qwSxjI/aDosqVuyyqqlm+w/irUmNHGL+t7g/kD932g0Q=");

            @Documentation("Random skin for fake-player, if you enable the local skin for fake-player. See: FakePlayerManagerModule")
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

        @Documentation("""
                This module provides `/back` command.
                """)
        public class Back {
            public boolean enable = false;

            @Documentation("If the player's teleportation destination is close enough, we ignore this telepotation.")
            public double ignore_distance = 32d;
        }

        @Documentation("""
                This module provides `/tpa`, `/tpahere`, `/tpaacept` and `/tpadeny` commands.
                """)
        public class Tpa {
            public boolean enable = false;

            @Documentation("Tpa request expiration duration. unit is second")
            public int timeout = 300;

        }

        @Documentation("""
                This module provides `/works` command.
                                
                A `work` means a project (a building, a red-stone device ...) that crafted by a player.
                                
                `work` types:
                - `Non-production work`: the project don't produce any resource (e.g. bone, string, coal).
                - `Production work`: the project produce some resource. 
                     For a production-work, fuji provides the `production sample` to count the `hopper` and `minecart-hopper`
                     
                Note:
                - You can use the `production counter` provided by `production work` to sample the output.
                - This module works with `carpet-fabric`'s `hopper counter`. You can use both of them at the same time.
                - The hopper counter provided by this module will not `destroy the item`.
                                
                """)
        public class Works {
            public boolean enable = false;

            @Documentation("For a production-work, how long should we sample it ?")
            public int sample_time_ms = 60 * 1000 * 60;
            @Documentation("For a production-work, how large the radius should we considered as the work's production")
            public int sample_distance_limit = 512;
            @Documentation("For a production-work, we only display the topN output items")
            public int sample_counter_top_n = 20;
        }

        @Documentation("""
                This module provides `/download` command. 
                                
                This command allows to downlaod nearby chunks around a player.
                                
                Use-case: if a player wants to download his buildings, or just want to download the redstone-structure 
                so that he can debug in his single-player world.
                """)
        public class WorldDownloader {
            public boolean enable = false;

            @Documentation("The url format used to broadcast")
            public @NotNull String url_format = "http://example.com:%port%%path%";

            public int port = 22222;

            @Documentation("Max download speed limit for each connection.")
            public int bytes_per_second_limit = 128 * 1000;

            @Documentation("Max download request allowed in the memory at the same time.")
            public int context_cache_size = 5;
        }

        @Documentation("""
                This module provides `disabler` to disable checkers in `vaniila minecraft`.
                """)
        public class Disabler {
            public boolean enable = false;

            public @NotNull ChatSpeedDisabler chat_speed_disabler = new ChatSpeedDisabler();
            public @NotNull MoveSpeedDisabler move_speed_disabler = new MoveSpeedDisabler();
            public @NotNull MaxPlayerDisabler max_player_disabler = new MaxPlayerDisabler();

            @Documentation("Disable `Kicked for spamming`")
            public class ChatSpeedDisabler {
                public boolean enable = true;
            }

            @Documentation("Disable `moved too quickly` and `vehicle too quickly` check")
            public class MoveSpeedDisabler {
                public boolean enable = true;
            }

            @Documentation("Disable the max players limit of the server.")
            public class MaxPlayerDisabler {
                public boolean enable = true;
            }
        }

        @Documentation("""
                This module provides `/deathlog` command.
                                
                Log player's inventory when he die, so that we can restore his inventory later.
                                
                Usage:
                - If you want to query the death logs for player `Steve`: `/deathlog view Steve`
                - If you want to restore **the death log indexed 0 from `Steve`** for player `Steve`: `/deathlog restore Steve 0 Steve`
                                
                """)
        public class DeathLog {
            public boolean enable = false;
        }

        @Documentation("""
                This module provides some extra `placeholder`.
                                
                Extra placeholder:
                - %fuji:player_mined%
                - %fuji:server_mined%
                - %fuji:player_placed%
                - %fuji:server_placed%
                - %fuji:player_killed%
                - %fuji:server_killed%
                - %fuji:player_moved%
                - %fuji:server_moved%
                - %fuji:player_playtime%
                - %fuji:server_playtime%
                - %fuji:health_bar% -> shows the health bar of the player
                - %fuji:rotate hello% -> rotate the "hello" string each time.
                - %fuji:has_permission <permission>% -> check luckperms permission
                - %fuji:get_meta <meta>% -> get luckperms meta
                - %fuji:random_player% -> get a random player from online players
                - %fuji:random <min> <max>% -> get a random number
                - %fuji:escape <placeholer-name>% -> escape a placeholder.
                  You can use multi-level escape in a convenient way: `%fuji:escape player:name%` = `%fuji:escape player:name 1`
                - %fuji:date% -> You can use custom `date formatter`: `%fuji:date HH:MM`
                  See also: https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
                                
                Tips:
                - You can also use [the default available placeholders](https://placeholders.pb4.eu/user/default-placeholders/) in anywhere. (Yeah, you can use `placeholder` in the `en_us.json` language file, it works)
                - There are some mods also provides extra placeholders, see: [other mods that provides extra placeholders](https://placeholders.pb4.eu/user/mod-placeholders/)
                  
                """)
        public class Placeholder {
            public boolean enable = false;
        }

        @Documentation("""
                This module provides `/pvp` command.
                """)
        public class Pvp {
            public boolean enable = false;
        }

        @Documentation("""
                for `offline-mode` whitelist, this makes whitelist `only` compare the username and `ignore` UUID!
                """)
        public class Whitelist {
            public boolean enable = false;
        }

        @Documentation("""
                                
                Background:
                  The vanilla minecraft use a command system called `brigadier`.
                  All the commands are `registered`, `parsed` and `executed` by `brigadier`.
                  
                  In this system, all commands are build into a tree strucutre.
                  
                  For example, like the command `/gamemode creative Steve` is composed by 3 `command node`:
                  - `literal command node` -> "gamemode"
                  - `argument command node` -> a valid gamemode
                  - `argument command node` -> a valid player
                  
                  And the `command node path` stands the `tree node path`.
                  For `/gamemode creative Steve`, the path is ["gamemode", "gamemode", "target"].
                  
                  You can query a command path using `/lp group default permission set fuji.permission...` way.
                  
                  Also, each `command node` has its `requirement`, which is a condition to check if the `command user` can use the `command node`.
                                
                This module can `override` the `requirement` of a `command node` into a `permission` with prefix `fuji.permission.<command_node_path>`.
                                
                See [permission](https://github.com/sakurawald/fuji-fabric/wiki/Permission)
                                
                Example 1: let everyone use `/op` -> `/lp group default permission set fuji.permission.op true`
                                
                """)
        public class CommandPermission {
            public boolean enable = false;
        }

        @Documentation("""
                This module provides `/head` command, so that players can buy custom-head.
                """)
        public class Head {

            public boolean enable = false;
        }

        @Documentation("""
                Enable this module requires `spark` mod installed.
                                
                This module provides `/profiler` command to show server health status (including os, vm, cpu, ram, tps, mspt and gc)
                """)
        public class Profiler {
            public boolean enable = false;
        }

        @Documentation("log command issue into the console.")
        public class CommandSpy {
            public boolean enable = false;
        }

        @Documentation("""
                This module provides scheduler for auto-run jobs.
                                
                You can add schedule jobs by `cron expression`, set the random command-list to be executed.
                                
                Command:
                - /scheduler
                                
                """)
        public class CommandScheduler {
            public boolean enable = false;
        }

        @Documentation("""
                This module provides `/fuji reload` command, so that you can reload modules in game.
                                
                Note:
                - After you `enable` or `disable` a `module`, you must `restart` the server.
                  The command `/fuji reload` only works for `module options`.
                                
                """)
        public class Config {
            public boolean enable = false;
        }

        @Documentation("""
                This module provides some test commands for development.
                This module only works in development-environment.
                If you enable this module in a production-environment, then nothing will happen.
                """)
        public class Tester {
            public boolean enable = false;
        }

        @Documentation("""
                This module provides multi-language support for your players.
                                
                Difference:
                - Disable this module: all the players use the `default_language`
                - Enable this module: will try to respect the player's client-side language, if the server-side supports its language
                                
                Feature:
                - Respect the player's client-side language-setting.
                - If the player's client-side language-setting is not supported, then use the default language.
                - Lazy-load support, which means if a language is not required, then it will not be loaded.
                - Lazy-reload support, you need to enable `ConfigModule` to use reload command.
                                
                """)
        public class Language {
            public boolean enable = false;
        }

        @Documentation("""
                This module provides afk detection.
                                
                If a player is idle long enough, he will be marked as afk state.
                A afk player will display in `tab list`.
                                
                A player can issue `/afk` command to afk manually.
                                
                Note:
                - There is no protect for a `afk player` 
                                
                """)
        public class Afk {
            public boolean enable = false;

            @Documentation("The tab-name format when a player is afk")
            public @NotNull String format = "<gray>[AFK] %player:displayname_visual%";

            @Documentation("The afk checker is a timer to check and mark the player's recently active time.")
            public @NotNull AfkChecker afk_checker = new AfkChecker();

            public class AfkChecker {
                @Documentation("The cron to define how the afk_checker is triggered.")
                public @NotNull String cron = "0 0/5 * ? * *";
                @Documentation("Should we kick a player if he is afk ?")
                public boolean kick_player = false;
            }
        }

        @Documentation("""
                Command: /rtp
                                
                Feature:
                - Per dimension configurable.
                - Ignore flulid blocks (water, lava...).
                - Ignore powered snow
                                
                Argument:
                - --dimension: target dimension
                                
                Note:
                - It's highly recommended to pre-gen the world chunks. To gen a new chunk during rtp rquires about 2~10 seconds.
                  If a chunk is pre-gen, then it will be fast.
                - If you are using rtp in the_end, and the chunks are not generated, then it will be very very slow.
                  If it's so, you can adjust the range of the_end to a small number.
                                
                """)
        public class Rtp {

            public boolean enable = false;

            public @NotNull Setup setup = new Setup();

            @Documentation("""
                    Teleport setup per dimension. Dimensions that are not in the list will be disabled to rtp.
                    """)
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

        @Documentation("""
                This module allows you to write commands in `sign block`.
                                
                Example 1
                ```
                /say hi %player:name%
                line 2 empty
                line 3 empty
                line 4 epmty
                ```
                                
                Example 2
                ```
                prefix /say first
                /say the second
                /say hi %player:name%
                /say the last command
                ```
                                
                Example 3
                ```
                prefix /say this is
                the first /say and the
                second
                line 4 empty
                ```
                                
                Note:
                - You need to press `shift + right click` to edit an `interactive sign`
                - The command is executed as `the player in the console`. (Not executed as the console)
                               
                """)
        public class CommandInteractive {
            public boolean enable = false;
        }

        @Documentation("""
                This module provides `/home` command.
                        
                Meta:
                - Integer `fuji.home.home_limit` # The home number per player limit.
                """)

        public class Home {
            public boolean enable = false;
        }

        @Documentation("""
                This module allows you to custom every system-message defined by mojang in `./assets/minecraft/lang/en_us.json`
                        
                The mojang offical en_us.json file may looks like: [en_us.json for minecraft 1.21](https://github.com/sakurawald/fuji-fabric/blob/dev/.github/files/en_us.json)
                                
                The system messages including:
                - Player join and leave server message
                - Player advancement message
                - Player death message
                - Player command feedback
                - Player white-list message
                - ... (and more other system messages)
                        
                Note:
                - Some messages in `en_us.json` are only used in the client-side, so you may more interested in keys that start with `multiplayer.`
                                
                """)
        public class SystemMessage {
            public boolean enable = false;

            @Documentation("The language keys to modify.")
            public @NotNull Map<String, String> key2value = new HashMap<>() {
                {
                    this.put("multiplayer.player.joined", "<rainbow>+ %s");
                    this.put("multiplayer.player.left", "<dark_gray>%s leeeeeeeeft the game");
                    this.put("commands.seed.success", "<rainbow> Seeeeeeeeeeed: %s");
                    this.put("multiplayer.disconnect.not_whitelisted", "<rainbow>Please apply a whitelist first!");
                    this.put("death.attack.explosion.player", "<rainbow>%1$s booooooom because of %2$s");
                    this.put("multiplayer.disconnect.server_shutdown", "Server closeeeeeeeed");
                }
            };

        }


        @Documentation("""
                This module provides command alias.
                                
                An alias means we redirect a command-node into another command-node.
                The requirement of comamnd-node is extended.
                                
                The format is: `source command node path` and `destination command node path`
                                
                """)
        public class CommandAlias {
            public boolean enable = false;
            public @NotNull List<CommandAliasEntry> alias = new ArrayList<>() {
                {
                    this.add(new CommandAliasEntry(List.of("r"), List.of("reply")));
                    this.add(new CommandAliasEntry(List.of("i", "want", "to", "modify", "chat"), List.of("chat", "format")));
                }
            };
        }

        @Documentation("""
                This module provides command rewrite, so that you can use `regex language` to rewrite the `command line` a player issued.
                """)
        public class CommandRewrite {
            public boolean enable = false;
            public @NotNull List<RegexRewriteEntry> regex = new ArrayList<>() {
                {
                    this.add(new RegexRewriteEntry("home", "home tp default"));
                }
            };

        }

        @Documentation("""
                This module provides some `numeric multiplier`.
                                
                Supported `numeric types`:
                - `damage`: damage to plaer
                - `experience`: experience a player gained
                                
                Example 1
                If you want to `doubled` the damage from zombie to a player.
                You can set a meta by: `/lp group default meta set fuji.multiplier.damage.minecraft:zombie 2`
                                
                Example 2
                If you want to cancel fall damage for all players. You can use `damage multiplier`.
                You can set a meta by: `/lp group default meta set fuji.multiplier.damage.minecraft:fall 0`
                                
                Example 3
                If you want to `doubled` all the damages to a player.
                You can set a meta by: `/lp group default meta set fuji.multiplier.damage.all 2`
                                
                Example 4
                If you want to `doubled` all the experience a player gained.
                You can set a meta by: `/lp group default meta set fuji.multiplier.experience.all 2`
                                
                Example 5
                If you want to `half` all the damages to a player.
                You can set a meta by: `/lp group default meta set fuji.multiplier.damage.all 0.5`
                                
                """)
        public class Multiplier {
            public boolean enable = false;

        }

        @Documentation("""
                This module provides anti-build purpose.
                                
                Use-case: ban some item/block/entity 
                                
                Anti types:
                - break_block
                - place_block
                - interact_item
                - interact_block
                - interact_entity
                                
                For example, let's say you want to ban TNT:
                1. add `minecraft:tnt` into `place_block` list
                                
                And it's done. 
                                
                Use `/lp user <player> permission set fuji.anti_build.place_block.bypass.minecraft:tnt` to 
                allow a player place the tnt.
                                
                Tips:
                - To query `blcok identifier`, use `/setblock ~ ~ ~ ...` command.
                - To query `entity identifier`, use `/summon ...` command.
                - To query `item identifier`, use `/give ...` command. 
                                
                """)
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


        @Documentation("""
                This module provides colorize for: sign, anvil
                                
                Tips:
                - You can use `mini-message language` to define complex message format. (See more in `chat module`)
                                
                """)
        public class Color {
            public boolean enable = false;

            public @NotNull Sign sign = new Sign();
            public @NotNull Anvil anvil = new Anvil();

            @Documentation("Enable `color` for all sign blocks.")
            public class Sign {
                public boolean enable = true;
            }

            @Documentation("Enable `color` for anvil.")
            public class Anvil {
                public boolean enable = true;
            }
        }

        @Documentation("""
                This module provides commands to open `remote functional blocks`.
                                
                Functional blocks:
                - /workbench 
                - /enchantment
                - /grindstone
                - /stonecutter
                - /anvil
                - /cartography
                - /enderchest
                - /smithing
                - /loom
                                
                """)
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

            @Documentation("This module provides `/workbench` command.")
            public class Workbench {
                public boolean enable = true;
            }

            @Documentation("This module provides `/enchantment` command.")
            public class Enchantment {

                public boolean enable = true;

                @Documentation("Should we override the power of proviers for the opened enchant table?")
                public @NotNull OverridePower override_power = new OverridePower();

                public class OverridePower {

                    public boolean enable = true;
                    @Documentation("""
                            How many power providers for the opened enchant table.
                            For a max level of enchant table, it requires 15 power providers.""")
                    public int power_provider_amount = 15;
                }
            }

            @Documentation("This module provides `/grindstone` command.")
            public class GrindStone {
                public boolean enable = true;
            }

            @Documentation("This module provides `/stonecutter` command.")
            public class StoneCutter {

                public boolean enable = true;
            }

            @Documentation("This module provides `/anvil` command.")
            public class Anvil {

                public boolean enable = true;
            }

            @Documentation("This module provides `/cartography` command.")
            public class Cartography {
                public boolean enable = true;
            }

            @Documentation("This module provides `/enderchest` command.")
            public class EnderChest {
                public boolean enable = true;
            }

            @Documentation("This module provides `/smithing` command.")
            public class Smithing {
                public boolean enable = true;
            }

            @Documentation("This module provides `/loom` command.")
            public class Loom {
                public boolean enable = true;
            }

        }

        public Gameplay gameplay = new Gameplay();

        public class Gameplay {
            public boolean enable = false;

            public @NotNull MultiObsidianPlatform multi_obsidian_platform = new MultiObsidianPlatform();
            public @NotNull Carpet carpet = new Carpet();

            @Documentation("""
                    This module provides some purpose about `carpet-fabric` mod.
                    """)
            public class Carpet {
                public boolean enable = false;

                public @NotNull FakePlayerManager fake_player_manager = new FakePlayerManager();
                public @NotNull BetterInfo better_info = new BetterInfo();

                @Documentation("""
                        Enable this module requires `carpet-fabric` mod installed.
                                        
                        This module provides some management for `fake-player`.
                                            
                        Command:
                        - /player who -> query the owner of the fake-player.
                        - /player renew -> renew all of your fake-players.
                        """)
                public class FakePlayerManager {
                    public boolean enable = true;

                    @Documentation("""
                            How many fake-player can each player spawn (in different time)? 
                                                
                            The tuple means (day_of_week, minutes_of_the_day, max_fake_player_per_player).
                            The range of day_of_week is [1,7].
                            The range of minutes_of_the_day is [0, 1440].
                                                 
                            For example: (1, 0, 2) means if the days_of_week >= 1, and minutes_of_the_day >= 0, then the max_fake_player_per_player now is 2.
                                                
                            Besides, you can add multi rules, the rules are checked from up to down.
                            The first rule that matches current time will be used to decide the max_fake_player_per_player.
                                                
                            You can issue `/player who` to see the owner of the fake-player.
                            Only the owner can operates the fake-player. (Op can bypass this limit)
                                                
                            """)
                    public @NotNull ArrayList<List<Integer>> caps_limit_rule = new ArrayList<>() {
                        {
                            this.add(List.of(1, 0, 2));
                        }
                    };

                    @Documentation("""
                            How long should we renew when a player issue command `/player renew`
                                        
                            The command `/player renew` allows the player to manually renew all of his `fake-player`.
                                        
                            If a fake-player don't gets renew, then it will expired and get killed.
                                                
                            Use-case: to avoid some long-term alive fake-player.
                            """)
                    public int renew_duration_ms = 1000 * 60 * 60 * 12;

                    @Documentation("""
                            The rule to transform the name of fake-player.
                                                
                            Use-case: add prefix or suffix for fake-player.
                            """)
                    public @NotNull String transform_name = "_fake_%name%";

                    @Documentation("""
                            Should we use local skin for fake-player? 
                                                
                            Enable this can prevent fetching skins from mojang official server each time the fake-player is spawned. 
                            This is mainly used in some network siatuation if your network to mojang official server is bad.
                            """)
                    public boolean use_local_random_skins_for_fake_player = true;
                }

                @Documentation("""
                        - Add `nbt query` for `/info block` command.
                        - Add the command `/info entity`.
                        """)
                public class BetterInfo {
                    public boolean enable = true;
                }
            }


            @Documentation("""
                    In vanilla minecraft, each `ender-portal` links to `the only one obsidian platform`.
                    This module makes each `ender-portal` links to its own `obsidian platform`.
                                    
                    makes every EnderPortal generate its own Obsidian Platform (Up to 128 in survival-mode.
                    You can even use creative-mode to build more Ender Portal and more ObsidianPlatform. 
                                    
                    Please note that: all the obsidian-platform are vanilla-respect, which means they have the same chunk-layout and the same behaviour as vanilla obsidian-platform which locates in (100,50,0))
                                    
                    Use-case: you want more `obsidian platform` for your redstone-struture.
                    """)
            public class MultiObsidianPlatform {
                public boolean enable = false;
                @Documentation("""
                        The coordination-convertion factor between overworld and the_end.
                        In vanilla minecraft, the factor between overworld and the_nether is 8.""")
                public double factor = 4;
            }
        }


        @Documentation("""
                This module provides commands to run commands to run commands to run commands...
                                
                """)
        public class CommandMeta {
            public boolean enable = false;

            public @NotNull Run run = new Run();
            public @NotNull ForEach for_each = new ForEach();
            public @NotNull Chain chain = new Chain();
            public @NotNull Delay delay = new Delay();
            public @NotNull Shell shell = new Shell();

            @Documentation("""
                    This module provides `/run` command, which can run a `command` with `context`.
                                        
                    Example 1
                    Give random diamonds to all online players: `/run as console give @a minecraft:diamond %fuji:random 8 32%`
                                        
                    Example 2
                    Give all online players random diamonds: `/run as console foreach give %fuji:escape player:name% minecraft:diamond %fuji:escape fuji:random 8 32%`
                                        
                    Example 3
                    Execute as a player, to run other commands. (Similar to `/execute as ...`): `/run as player Steve back`
                                        
                    """)
            public class Run {
                public boolean enable = true;
            }

            @Documentation("""
                    This module provides `/foreach` command.
                                        
                    If a command is only targeted for `single player`, you can use `foreach` to apply it for `all players`
                                        
                    Example 1: `/foreach say hello %player:name%`
                                        
                    Note:
                    - If you use `foreach` in `scheduler module`, then you should `escape` (Write `%fuji:escape player:name%` insted of `%player:name%`) the `placeholder`.
                        It's because the scheduler module will try to parse the placeholder, and you need to escape the placeholder, so that the placeholder can be parsed by foreach commnad. 
                        Here is an example about `escape` the `foreach command` in scheduler command list: `foreach give %fuji:escape player:name% minecraft:diamond 16`
                      
                    """)
            public class ForEach {
                public boolean enable = true;
            }

            @Documentation("""
                    A chain command allows you to run another 2 commands, the first is any command, and the second is the chain command. 
                                        
                    Example 1: 
                    The command will be executed one by one.
                    `/chain say 1 chain say 2 chain say 3`
                                        
                    Example 2: 
                    The chain will `break` if the previous command `failed`.
                    `/chain bad command here chain say 2`
                                        
                    Note:
                    - In vanilla minecraft, the `return value` of command, are `failed`, `pass` and `success`
                                        
                    """)
            public class Chain {
                public boolean enable = true;
            }

            @Documentation("""
                    Delay command allows you to ............................ execute a command.
                                        
                    Example 1: `/delay 3 say three seconds passed`
                                        
                    Example 2: `/delay 1 delay 2 delay 3 say 6 seconds passed.`
                                        
                    """)
            public class Delay {
                public boolean enable = true;
            }

            @Documentation("""
                    This module provides `/shell` command, which executes `command line` in your `host shell`.
                                        
                    This module is a powerful and **`dangerous`** module, **not recommended to enable it**.
                                        
                    Exmaple 1: `/shell touch %player:name%.dangerous` (Create a file using placeholder)
                                        
                    Exmaple 2: `/shell emacs` (Execute a program in the host os)
                                        
                    Example 3: `/shell ...` (Possible to download a virus from Internet and execute it!)
                                        
                    """)
            public class Shell {
                public @NotNull String enable_warning = "ENABLE THIS MODULE IS POTENTIAL TO HARM YOUR COMPUTER! YOU NEED TO CHANGE THIS FIELD INTO `CONFIRM` TO ENABLE THIS MODULE";
                public boolean enable = false;
            }

        }

        @Documentation("""
                This module provies some `simple` commands.
                We said a `command` is `simple` since its puporse is not big enough to be a standalone `facility`.
                """)
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
            public @NotNull SendMessage send_message = new SendMessage();
            public @NotNull SendBroadcast send_broadcast = new SendBroadcast();
            public @NotNull SendActionBar send_actionbar = new SendActionBar();
            public @NotNull TrashCan trashcan = new TrashCan();
            public @NotNull Tppos tppos = new Tppos();
            public @NotNull Warp warp = new Warp();

            @Documentation("This module provides `/bed` command, which teleports the player to his bed.")
            public class Bed {
                public boolean enable = true;
            }

            @Documentation("This module provides `/extinguish` command.")
            public class Extinguish {
                public boolean enable = true;
            }

            @Documentation("This module provides `/feed` command.")
            public class Feed {
                public boolean enable = true;
            }

            @Documentation("This module provides `/fly` command.")
            public class Fly {
                public boolean enable = true;
            }

            @Documentation("This module provides `/god` command.")
            public class God {
                public boolean enable = true;
            }

            @Documentation("This module provides `/hat` command.")
            public class Hat {
                public boolean enable = true;
            }

            @Documentation("This module provides `/heal` command.")
            public class Heal {
                public boolean enable = true;
            }

            @Documentation("""
                    Allows you to custom lore in your hand.
                                        
                    Command: /lore
                                        
                    Example 1: `/lore set <rainbow>the first line<newline><bold><green>the second`
                                        
                                        
                                        
                    """)
            public class Lore {
                public boolean enable = true;
            }

            @Documentation("""
                    This module provides `/sit` command, and the ability to sit by right-click any chair.
                    """)
            public class Sit {
                public boolean enable = true;
                public boolean require_stairs_to_sit = true;
                public boolean allow_right_click_sit = true;
                public boolean allow_sneaking_to_sit = false;
                public boolean require_empty_hand_to_sit = false;
                public boolean require_no_opaque_block_above_to_sit = false;
                public int max_distance_to_sit = -1;
            }

            @Documentation("This module provides `/more` command.")
            public class More {
                public boolean enable = true;
            }

            @Documentation("This module provides `/ping` command.")
            public class Ping {
                public boolean enable = true;
            }

            @Documentation("This module provides `/realname` command.")
            public class Realname {
                public boolean enable = true;

            }

            @Documentation("""
                    This module provides `/nickname` command.
                                    
                    Tips:
                    - You can query real name using `realname module`
                    - To show the `nickname`, you need to use `%player:displayname%` or `%player:displayname_visual%` placeholders.
                                    
                    """)
            public class Nickname {
                public boolean enable = true;
            }

            @Documentation("This module provides `/repair` command.")
            public class Repair {
                public boolean enable = true;

            }

            @Documentation("This module provides `/reply` command, which replys the recent player who `/msg` you")
            public class Reply {
                public boolean enable = true;
            }

            @Documentation("This module provides `/seen` command.")
            public class Seen {
                public boolean enable = true;
            }

            @Documentation("This module provides `/suicide` command.")
            public class Suicide {

                public boolean enable = true;
            }

            @Documentation("""
                    This module provides `/top` command.
                                        
                    Go up to the ground conveniently !
                    """)
            public class Top {
                public boolean enable = true;
            }

            @Documentation("""
                    This module provides `/sendmessage` command.
                    """)
            public class SendMessage {
                public boolean enable = true;
            }

            @Documentation("""
                    This module provides `/sendbroadcast` command.
                    """)
            public class SendBroadcast {
                public boolean enable = true;
            }

            @Documentation("""
                    This module provides `/sendactionbar` command.
                    """)
            public class SendActionBar {
                public boolean enable = true;
            }

            @Documentation("Command: /trashcan")
            public class TrashCan {
                public boolean enable = true;
            }

            @Documentation("""
                    The unified teleport command.
                                        
                    Argument:
                    - --dimension: target dimension
                    - --x: target x
                    - --y: target y
                    - --z: target z
                    - --yaw: target yaw
                    - --pitch: target pitch
                    - --centerX: centerX for rtp
                    - --centerZ: centerZ for rtp
                    - --circle: rtp shape, circle or rectangle
                    - --minRange: rtp min range
                    - --maxRange: rtp max range
                    - --minY: rtp min Y
                    - --maxY: rtp max Y
                    - --maxTryTimes: rtp max try times.
                                        
                    Note:
                    - If you specify the `--x`, `--y` or `--z` argument, then the command will teleport to a `fix position`, or else to `random position`.
                                        
                    Command: /tppos
                    """)
            public class Tppos {
                public boolean enable = true;
            }

            @Documentation("Command: /warp")
            public class Warp {
                public boolean enable = true;
            }
        }

        @Documentation("""
                This module provides tab-list custom.
                                
                """)
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
                        this.add("<#FFA1F5>-----------------<newline><rainbow>This is another one");
                    }

                };
            }

            @Documentation("""
                    If enable this moudle, the `player names` in `tab list` will be sorted by `weight`.
                                        
                    You can set sort `weight` for a group using `/lp group default meta set fuji.tab_list.sort.weight 1` to set weight to 1.
                    You can set sort `weight` for a player using `/lp user Steve meta set fuji.tab_list.sort.weight 2`
                                        
                    The default weight is 0, the range of weight is [0, 675], which means you can set at most 676 sort groups.
                                        
                    Issue:
                    - The `tab list` sort method is client-side decided. So the workaround is to send dummy-player entry to the client-side, and hide the real entry in client-side's tablist.
                      In this case, the client-side will find that, all `command target selector` will display the dummy-entry.
                      And you can see the dummy-entry in client-side's `Player Reporting` UI.
                    - After you set a new `weight` for a player, the `tab list` will temporary dupe the entry. (Re-connect the server to sovle this)
                                        
                    Note:
                    - The dummy-entry is just an entry listed in `tab list`, when the client ask the server tab list, the server lie with the dummy-entry list.
                      There is not a real player entity in the server side, so no extra performance problem.
                      
                    """)
            public class Sort {
                public boolean enable = false;
                public @NotNull String sync_cron = "* * * ? * *";
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

        @Documentation("""
                                
                Command: /kit
                                
                """)
        public class Kit {
            public boolean enable = false;
        }

        @Documentation("""
                This module allows the `server` to execute commands after an `event` occurs.
                                
                Example 1:
                Let's say you want to send broadcast and set random spawnpoint of a newbie player.
                You can write the command list in player first join event
                ```
                "on_player_first_joined": {
                  "command_list": [
                    "sendbroadcast <light_purple>Welcome new player %player:name% to join us!",
                    "execute as %player:name% run rtp",
                    "delay 10 spawnpoint %player:name%"
                  ]
                },
                ```
                                
                Example 2:
                You want to give a `kit` to a newbie player.
                ```
                "on_player_first_joined": {
                  "command_list": [
                    "kit give %player:name% <kit-name>"
                  ]
                },
                ```
                                
                Note:
                - You can use placeholders provided by `placeholder module`.
                                
                """)
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
                            this.add("sendmessage %player:name% you just die.");
                        }
                    };
                }

                public class AfterPlayerBreakBlock {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("sendmessage %player:name% you just break a block.");
                            this.add("experience add %player:name% %fuji:random 2 8%");
                        }
                    };
                }

                public class AfterPlayerPlaceBlock {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("sendmessage %player:name% you just place a block.");
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
                            this.add("sendmessage %player:name% You are in %world:id% now!");
                        }
                    };
                }

                public class OnPlayerFirstJoined {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("sendbroadcast <rainbow>welcome new player %player:name% to join us!");
                        }
                    };
                }

                public class OnPlayerJoined {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("sendmessage %player:name% welcome to the server.");
                        }
                    };
                }

                public class OnPlayerLeft {
                    public @NotNull List<String> command_list = new ArrayList<>() {
                        {
                            this.add("sendbroadcast %player:name% left the server.");
                        }
                    };
                }
            }
        }

        @Documentation("""
                The cleaner to clean `item` and `entity` automatically.
                                
                Since the vanilla minecraft also has a `cleaner` to remove the item stack in the ground, so it's recommended to only use this module to `clean` some `weak-loading entities`, like: the sand item stack ...
                                
                """)
        public class Cleaner {

            public boolean enable = false;

            @Documentation("""
                    The job used to trigger `/cleaner clean`.
                                        
                    - The `cleaner clean` will only be triggered by the job.
                                        
                    Note:
                    - If the `cleaner` cleans nothing, then it will keep silent. (Which means you will not see any message in console, or in-game chat)
                                        
                    """)
            public String cron = "0 * * ? * * *";

            @Documentation("""
                    The `key` is `translatable key`, which you can query in [en_us.json language file in minecraft 1.21](https://github.com/sakurawald/fuji-fabric/blob/dev/.github/files/en_us.json).
                      - The translable key of `entity` starts with `entity.minecraft`.
                      - The translable key of `item` starts with `item.minecraft` and `block.minecraft`.
                                        
                    The `age` is the existence time of the `entity`, the unit of `age` is `game tick`, which means `20 age` = `20 ticks` = `1 second`.
                                        
                    Example 1: If you want to clean the `sand` item which exsits more than 60 seconds, you can write `"block.minecraft.sand": 1200`.
                                        
                    Note:
                    - The `cleaner` will only `remove` the `entities` whose `translatable key` equals `key`, and `age` >= `the defined age`. (And the `entity` must not in the `ignore` list)
                    - Hover your mosue on the `cleaner broadcast`, you can see waht is been removed.
                                        
                    """)
            public Map<String, Integer> key2age = new HashMap<>() {
                {
                    this.put("block.minecraft.sand", 1200);
                    this.put("item.minecraft.ender_pearl", 1200);
                    this.put("block.minecraft.white_carpet", 1200);
                    this.put("block.minecraft.cobblestone", 1200);
                }
            };

            public Ignore ignore = new Ignore();

            @Documentation("""
                    Entities match the `ignore list` will not be `cleaned`.
                                        
                    The `cleaner` will always ignore the following types:
                    - player
                    - any block attached entity (e.g. leash_knot)
                    - any vehicle entity (e.g. minecart, boat ...)
                                        
                    Note:
                    - The `item entity` = item stack dropped in the ground
                    - The `living entity` = pig, sheep, zombie, villager ...
                                        
                    """)
            public class Ignore {
                @Documentation("Should we ignore all `item entity`.")
                public boolean ignoreItemEntity = false;
                @Documentation("""
                        Should we ignore all `living entity`.
                        If you want the `cleaner` to remove `monster` or `animals`, you should enable this option.
                        """)
                public boolean ignoreLivingEntity = true;
                @Documentation("""
                        Should we ignore named entity. (With name tag, or name changed by anvil.)
                        """)
                public boolean ignoreNamedEntity = true;
                @Documentation("Like entity riding in some other entity, e.g. minecraft, pig or spider")
                public boolean ignoreEntityWithVehicle = true;
                @Documentation("Contrary to above.")
                public boolean ignoreEntityWithPassengers = true;
                public boolean ignoreGlowingEntity = true;
                public boolean ignoreLeashedEntity = true;
            }
        }
    }
}
