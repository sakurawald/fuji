> **WARNING**:
> 
> If you are using a version before `v1.5.9`, you should read the changelog of `v1.5.9`.

- feature: better command feedback for `meta cmmands`, now the command feedback will be passed down, not only sends the error to the command executor, but also sends the error to the command initiator. (command_meta module and its submodules)
  - e.g.: `/run as console bad command`
- feature: now the command `/chain` will copy and pass `the command source` to the next chain. (in the past, `the first level of chain` is the `initiating command source`, and `the deeper levels` are always executed as `the console`.) 
  - e.g. `run as player <player> chain test-level-perm %player:name% 4 chain say value is true)`
- feature: add `/send-bossbar` command. (echo.send_bossbar module)
  - send a bossbar with attached commands to players.
  - all in one example: `/send-bossbar <player> --stepType BACKWARD --totalMs 5000 --color PURPLE --style NOTCHED_6 --notifyMeOnComplete true --commandList "say the player %player:name% is healed|heal %player:name%" <rb>Healing is coming [elapsed_time]/[total_time]/[left_time]`
- feature: add `interruptile` option, now you can configure the `interrupt disance`, `interrupt on damage` and `interrupt in combat` per command and using `regex` to match them from up to down `in order`. (command_warmup module)
  - **You will need to re-configure the `config/fuji/modules/command_warmup/config.json` file.**
- feature: add `/fuji inspect` command, to inspect inner states of the server. (fuji module)
  - add `/fuji inspect server-commands`: inspect all registered commands in the server.
  - add `/fuji inspect fuji-commands`: inspect all commands registered by `fuji` mod.
  - add `/fuji inspect modules`: inspect enabled/disabled modules of fuji.
- feature: add optional target player argument for `/heal` and `feed`, (`command_toolbox.heal` and `command_toolbox.feed` module)
- feature: update the `fabric loader` version from `v0.15.11` to `v0.16.5`
- feature: now will keep silent on adding missing language keys for language files, to avoid console spam.
- feature: simplify the placeholder module. (placeholder module)
  - add `/placeholder list`: a gui to list all registered placeholders in server.
  - add `/placeholder parse`: to parse a given string with a given player.
- feature: add `predicate module`, which provides commands to test conditions. 
  - add `/test-level-perm` command
  - add `/test-string-perm` command
- feature: enhance the `date parser` to support complex period, e.g. `1s2m3h4d5w6M7y`. (temp-ban module)
- refactor: rename `/helpop` into `/help-op`. (command_toolbox.help_op module)
- refactor: rename `/fuji list-commands` into `/fuji inspect server-commands`. (fuji module)
- fix: the `%fuji:inv%`, `%fuji:item%` and `%fuji:ender%` can't be resolved if the contextual player is null
- fix: remove the unusable `type string`: `ctx`, `command-source` and `source`. (command_bundle module)
