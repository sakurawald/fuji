> **Migration Guide**:
> 
> If you are using a version before `v1.5.9`, you should install this version to migrate the old configuration files. 
> 
> 1. The data structure and semantics remains the same as previous versions.
> 2. The file `config/fuji/config.json` will be migrated **automatically**.
> 3. Files `config/fuji/pvp.json`, `config/fuji/scheduler.json`, `config/fuji/warp.json`, `config/fuji/home.josn`, `config/fuji/world.json`, `config/fuji/seen.json`, `config/fuji/skin.json`, `config/fuji/head.json`, `config/fuji/nickname.json`, `config/fuji/chat.json` will be migrated **automatically**.
> 4. Some files require you to move them into the new location **manually**
>    1. move `config/fuji/kit` directory into `config/fuji/modules/kit/kit-data`
>    2. move `config/fuji/deathlog` directory into `config/fuji/modules/deathlog/death-data`
>    3. move `config/fuji/skin` directory into `config/fuji/modules/skin/skin-data`
>    4. move `config/fuji/skin.json` file into `config/fuji/modules/skin/config.json`

ChangeLog

