> **Migration Guide**:
> 
> If you are using a version before `v1.5.9`, you should install this version to migrate the old configuration files. 
> 
> 1. The data structure and semantics remains the same as previous versions.
> 2. The file `config/fuji/config.json` will be migrated **automatically**.
> 3. Files `config/fuji/pvp.json`, `config/fuji/scheduler.json`, `config/fuji/warp.json`, `config/fuji/home.josn`, `config/fuji/world.json`, `config/fuji/seen.json`, `config/fuji/skin.json`, `config/fuji/head.json`, `config/fuji/nickname.json`, `config/fuji/chat.json` will be migrated **automatically**.
> 4. Some files require you to move them into the new place **manually**:
>    1. move `config/fuji/kit` directory into `config/fuji/modules/kit/kit-data`
>    2. move `config/fuji/deathlog` directory into `config/fuji/modules/deathlog/death-data`
>    3. move `config/fuji/skin` directory into `config/fuji/modules/skin/skin-data`
>    4. move `config/fuji/skin.json` file into `config/fuji/modules/skin/config.json`

ChangeLog
- feature: add the configuration migrator layer, to help our user migrate version more easily. (it's less painful now.)
- feature: split the big file `confug/fuji/config.json` into small pieces locates in `config/fuji/modules`. (see the migration guide above.)
- feature: unify the `json key` naming policy, now all json keys use the `lower case underscore style`. (The old keys are migrated automatically.)
- feature: add `/tppos offline <player>` command to teleport to the offline position of a player.
- feature: use lazy loading way to initialize all managers.
- feature: now will skip the un-necessary nametag making for a dead player. (nametag module)
- feature: now `backup` service will keep the original `file structure` in file system.
- feature: soft fail if failed to load an extra dimension. (world module)
- feature: add locales for `deathlog module`
- fix: ensure that all jobs used to save configuration on server stopping phase will be triggered anyway.
- fix: the `invulnerable` is always treated as `true`. (afk.effect module)
- fix: suppress the `console error logging` on a new player joined the server. (skin module)
- refactor: use compile-time module graph, to boost the process of computing module path.
- refactor: rewrite the configuration system, now it's much more clear.
- refactor: remove the `functional.enchantment.override_power` module, now it's the part of `functional.enchantment` module.
- build: add more tests to test symbol reference. (the reference relation between modules are stricter now, and the compatibility with other mods are enhanced.)

