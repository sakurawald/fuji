# Fuji
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/src/main/resources/assets/fuji/icon.png" width="128" alt="mod icon">

Fuji is a minecraft mod that provides many essential and useful modules for vanilla survival.

> This is a **server-side only** mod, but you can use it in a **single-player world**. (Yes, the single-player world also includes a logic-server)
> - For a server-maintainer: You only need to install this mod at the server-side, and the players don't need to install this mod at their client-side
> - For a player: You only need to install this mod at the client-side, and then you can use the modules in your single-player world.

# Feature
1. **Vanilla-Respect**: all the modules do the least change to the vanilla game (Never touch the game-logic).
2. **Fully-Modular**: you can disable any module completely if you don't like it (The commands and events will all be disabled, just like the code never exists, without any performance issue).
3. **High-Performance**: all the codes are optimized for performance, and the modules are designed to be as lightweight as
   possible (From data-structure, algorithm, lazy-load and cache to improve performance greatly).
4. **Easy-to-Use**: all the modules are designed to be easy to use, and the commands are designed to be easy to remember, even the language file is designed to be easy to understand.

# Modules

_**By default, all the modules are disabled, and this mod does nothing to the vanilla minecraft.**_

<details>
<summary>Click here to display all the modules</summary>

#### PvpModule
provides a command to toggle the pvp status. (/pvp [on/off/status/list])
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/pvp-toggle.gif" alt="module presentation gif">

#### ResourceWorldModule
create and manage auto-reset resource world for overworld, the_nether and the_end.  (/rw [tp/delete/reset])
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/resource-world.gif" alt="module presentation gif">

#### ChatModule
A simple chat system.

> - Support mini-message based parser
> - Support mention players
> - Support chat-history -> new joined player can see the chat-history
> - Support per-player message-format settings -> /chat format
> - Support quick-codes
>   - Insert "pos" -> current position
> - Support display
>   - Insert "item" -> item display (support shulker-box)
>   - Insert "inv" -> inventory display
>   - Insert "ender" -> enderchest display
> - Support MainStats placeholders

<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/chat-style.gif" alt="module presentation gif">
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/display.gif" alt="module presentation gif">

#### TopChunksModule
Provides a command /chunks to show the most laggy chunks in the server.
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/top-chunks.gif" alt="module presentation gif">

#### BetterFakePlayerModule
(Carpet required) provides some management for fake-player.

> - FakePlayerNameSuffixAndPrefix
> - FakePlayerManipulateLimit
>   - Type `/player who` to see the owner of fake-player
>   - Only the owner of the fake-player can manipulate the fake-player
> - FakePlayerSpawnLimit -> caps can be set to change dynamically 
> - FakePlayerRenewTime
>   - Every fake-player only lives for 12 hrs until you renew it (This avoids the fake-player to be a long-term laggy entity)
>   - Type `/player renew` to renew the fake-player

<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/better-fake-player.gif" alt="module presentation gif">

#### BetterInfoModule
(Carpet required) provides /info entity and add nbt-query for /info block
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/better-info.gif" alt="module presentation gif">

#### TeleportWarmupModule
provides a teleport warmup for all player-teleport to avoid the abuse of teleport (Including damage-cancel, combat-cancel, distance-cancel).
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/teleport-warmup.gif" alt="module presentation gif">

#### SkinModule
provides /skin command, and even an option to use local random-skin for fake-player (This fixes a laggy operation when spawning new fake-player and fetching the skin from mojang server).

#### DeathLogModule
provides /deathlog command, which can log and restore the death-log for all players.
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/death-log.gif" alt="module presentation gif">

#### BackModule
provides /back command (Support smart-ignore by distance).

#### TpaModule
provides /tpa and /tpahere (Full gui support, and easy to understand messages).
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/tpa.gif" alt="module presentation gif">

#### WorksModule
provides /works command, some bit like /warp but this module provides a very powerful hopper and minecart-hopper counter for every technical player to sample their contraption.
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/works.gif" alt="module presentation gif">

#### WorldDownloaderModule
provides /download command for every player who wants to download the nearby chunks around him. (Including rate-limit and attack-protection. This command is safe to use, because everytime the command will copy the original-region-file into a temp-file, and only send the temp-file, which does nothing to the original-region-file)
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/download.gif" alt="module presentation gif">

#### MainStatsModule
This module sums up some basic stats, like: total_playtime, total_mined, total_placed, total_killed and total_moved (We call these 5 stats `MainStats`). You can use these placeholders in ChatStyleModule and MOTDModule

<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/main-stats.gif" alt="module presentation gif">

#### NewbieWelcomeModule
This module broadcasts a welcome-message and random teleport the new player and sets its respawn location.

#### CommandCooldownModule
Yeah, you know what this module does. (Use this module to avoid some heavy-command abuse)

#### MotdModule
A simple MOTD that supports fancy and random motd, and supports some placeholders like MainStats
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/motd.gif" alt="module presentation gif">

#### HeadModule
provides /head command to buy player heads.

#### ProfilerModule
provides /profiler to sample the server health. (Including os, vm, cpu, ram, tps, mspt and gc)

#### ZeroCommandPermissionModule
this module modifies ALL commands (even the command is registered from other mods) and adds a prefix-permission (we called it zero-permission) for the command. If the player has zero-permission, then we check zero-permission for that command, otherwise check the command's original requires-permission. 

> Tips: if you don't know how to determine command-node name, you can just type `/lp>  group default permission zero.` and let luckperms tell you what command-node names you can use.
> - Allow the default group to use a command by adding a zero-permission (e.g. /seed) -> `/lp  group default permission set zero.seed true`
> - Disallow the default group to use a command by adding a zero-permission (e.g. /help) -> `/lp  group default permission set zero.help false`
> - Disallow the default group to use a sub-command from a command by adding a zero-permission (e.g. /player [player] mount) -> `/lp group default permission set zero.player.player.mount false`

#### BypassThingsModule
provides options to bypass some annoyed things.

- bypass-chat-rate-limit -> avoid "Kicked for spamming"
- bypass-move-speed-limit -> avoid "Moved too quickly!"
- bypass-max-player-limit -> avoid server max-player limit

#### OpProtectModule
auto deop an op-player when he leaves the server.

#### MultiObsidianPlatformModule
makes every EnderPortal generate its own Obsidian Platform (Up to 128 in survival-mode, you can even use creative-mode to build more Ender Portal and more ObsidianPlatform. Please note that: all the obsidian-platform are vanilla-respect, which means they have the SAME chunk-layout and the SAME behaviour as vanilla obsidian-platform which locates in (100,50,0))
<img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/multi-obsidian-platform.gif" alt="module presentation gif">

#### StrongerPlayerListModule
a fix patch for ServerWorld#PlayerList, to avoid CME in player-list (e.g. sometimes tick-entity and tick-block-entity will randomly crash the server because of player-list CME)

#### WhitelistFixModule
for offline whitelist, this makes whitelist ONLY compare the username and ignore UUID!

#### CommandSpyModule
log command issue into the console.

#### BiomeLookupCacheModule
an optimization for mob-spawn, this will cause the mob spawns a few blocks away from the biome-border (This will not influence structure-based mob spawn).

> After many rounds of test (about 46000 chunks and 6000 entities), we found that this optimization can boost about 5~6 mspt, which is very considerable. 

#### TickChunkCacheModule
an optimization for iterating chunks, use event-based chunk-list constructor to avoid chunk-iteration lag.

> Disable `mixin.experimental.chunk_tickets=false
` in Lithium to avoid in-compatibility with this module.
> Lithium does some other optimization in the same mixin. However, this mod provides a better performance at this point.
> 
> About 3~4 mspt boost

#### SchedulerModule
where you can add schedule jobs by cron expression, set the random command-list to be executed.

> If `left_trigger_times` < 0, then it means infinity times.

#### ConfigModule
provides `/fuji reload` to reload configs.

#### TestModule
provides `/test` command only for test purpose. (Disable this by default, and you don't need to enable this unless you know what you are doing)

#### HatModule
provides `/hat` command

#### FlyModule
provides `/fly` command

#### GodModule
provides `/god` command

#### LanguageModule
provides multi-language support for your players.
(Disable this module will force all the players to use the default language)

- The default language is en_us.
- Respect the player's client-side language-setting.
- If the player's client-side language-setting is not supported, then use the default language.
- Lazy-load support, which means if a language is not required, then it will not be loaded.
- Dynamic-reload support, you need to enable `ConfigModule` to use reload command.

#### ReplyModule
provides `/reply` command to quickly reply messages to the player who recently `/msg` you! 

#### AfkModule
provides `/afk` command to set your afk status and auto-afk

#### SuicideModule
provides `/suicide` command.

#### CommandInteractiveModule
provides interactive sign command. You can insert `//` plus commands in any sign, and then right-click it to execute the command quickly.

- If the sign contains `//`, then you must press `shift` to edit this sign
- You can add some comments before the first `//`
- You can use all the four lines to insert `//` (Every `//` means one command)
- Placeholder `@u` means the user of this sign

#### SeenModule
provides `/seen` command.

#### MoreModule
provides `/more` command.

#### ExtinguishModule
provides `/extinguish` command.

#### HomeModule
provides `/home` command.

#### PingModule
provides `/ping` command.

#### SystemMessageModule
This module hijacks the vanilla system-message so that you can modify any system-message you want.
(Actually, you can hijack almost all the language messages in the vanilla `en_us.json` language file)
The system messages including:
- Player join and leave server message
- Player advancement message
- Player death message
- Player command feedback
- Player white-list message
- ...

  <img src="https://github.com/SakuraWald/fuji-fabric/raw/1.20.2/.github/images/system_message.gif" alt="module presentation gif">

#### EnderChestModule
provides `/enderchest` command.

#### WorkbenchModule
provides `/workbench` command.


</details>

# Config

### What is the format of config files?
To make the config files more `readable` and `transparent`, we use `.json` as the config file format.
We try our best to avoid the usage of `.dat` format
(The vanilla minecraft data file format which needs an NBTExplorer to view and edit).

### Where are the config files?
All the config files are inside `config/fuji/` directory so
that you don't need to find out where the config files are.

### How to update to a newer version?
Normally, the newer version will generate missing configuration keys automatically, but if this doesn't work, you can delete the old config file and restart the server to let the newer version generate the default config file.

# Permission
This mod uses a low-level permission system, which means that most of the admin commands are required level-permission to use. However, if you really want a command-permission-node for every command, you can use the `zero-command-permission` module (This module adds a prefix command-permission for ALL commands, even the command is not provided by this mod!).

> In other words, most of the commands doesn't require any permission to use, but if you want, you can use `ZeroPermissionModule` to add a prefix-permission for every command!

# Optimization
For a better server performance and without vanilla sacrifice, we recommend you to use `Lithium` and `Krypton` with this mod.

For JVM, we recommended GraalVM, which performs better than OpenJDK.
> Here are the JVM arguments we optimized for GraalVM (Reference from papermc and graalvm manual):
> 
> java -Xms16G -Xmx16G -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=40 -XX:G1MaxNewSizePercent=50 -XX:G1HeapRegionSize=16M -XX:G1ReservePercent=15 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -XX:+UseLargePages -XX:+UseTransparentHugePages -XX:+EnableDynamicAgentLoading -Djava.io.tmpdir=/tmp -jar server.jar --nogui

# Diagnosis
By default, this mod will output its `debug messages` into `logs/debug.log` so that you can know what this mod is doing.
You can add `-Dfuji.level="INFO"` to change the logger level.

# Build from source
This mod is written on linux environment, but there are no native-method calls, so you can compile it on any platform.

Clone the source: 
```shell
$ git clone https://github.com/SakuraWald/fuji-fabric.git
```
Change the working-directory: 
```shell
$ cd fuji-fabric
```
Compile the source:
```shell
$ ./gradlew build
```

# FAQ
###  What is `cron expression`?

Cron expression is an easy and powerful language used to define when a job should be triggered.
You can learn more and generate a cron expression from the generator: https://www.freeformatter.com/cron-expression-generator-quartz.html

### This mod failed to mixin the server at server-startup stage, what should I do?

This mod need `fabric-api` mod to work, so make sure you have installed `fabric-api` mod.

Before you enable `BetterFakePlayerModule`, you need to install `carpet` mod.

Before you enable `ProfilerMoudle`, you need to install `spark` mod.

### How can I report bugs or suggest new features?

You can create an issue at the project's `github` page.

# Thanks
At the early stage of this project, we reference some source and ideas from the following projects:
1. https://www.zrips.net/cmi/
2. https://essentialsx.net/
3. https://modrinth.com/mod/essential-commands
4. https://modrinth.com/mod/skinrestorer
5. https://modrinth.com/mod/headindex
