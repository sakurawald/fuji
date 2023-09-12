package fun.sakurawald.config;


import com.mojang.authlib.properties.Property;
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
        public MainStats main_stats = new MainStats();
        public BetterFakePlayer better_fake_player = new BetterFakePlayer();
        public CommandCooldown command_cooldown = new CommandCooldown();
        public TopChunks top_chunks = new TopChunks();
        public ChatStyle chat_style = new ChatStyle();
        public Skin skin = new Skin();

        public class ResourceWorld {
            public long seed = 0L;
        }

        public class MainStats {
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
            public String in_combat_message = "§bIn combat!";
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

            public History history = new History();

            public class History {
                public int cache_size = 30;
            }

            public class MentionPlayer {
                public String sound = "entity.experience_orb.pickup";
                public float volume = 100f;
                public float pitch = 1f;

                public int limit = 5;
                public int interval = 1000;
            }
        }

        public class Skin {

            public ArrayList<Property> default_skins = new ArrayList<>() {
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
                }
            };
        }
    }
}
