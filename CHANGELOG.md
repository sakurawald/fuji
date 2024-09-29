> **WARNING**:
> 
> If you are using a version before `v1.5.9`, you should read the changelog of `v1.5.9`.

- feature: add `/command-permission` command, which provides a user-friendly gui to query registered commands. (command permission module)
- feature: add `/glow` command. (glow module)
- feature: add `/freeze` command. (freeze module)
- feature: improve the implementation of command permission module, this also allows the neo-forge to use this module via sinytra-connector. (command permission module)
- feature: add `spy_on_console` option. (command spy module)
- feature: improve the command spy listener, now ensures all commands will be spied on. (command spy module)
- feature: now you can click the chunk score to teleport to the chunk position. (top_chunks module)
- feature: improve the compatibility with other mods. (chat module) 
- feature: add soft fail for tickWeather() mixin, this also allow the neo-forge to use this module via sinytra-connector. (world module)
- feature: now will also copy the requirement of the root of the target command node, so that there will be no a dummy command for players without permission. (command alias module)
- feature: improve the priority of command warmup module. (better compatibility with other mods)
- feature: now requires the permission level 4 to use `/fly`, `/god`, `/jump` commands.
- fix: possible concurrent modification exception if a player enter/leave the server. (tab_list module)
- fix: create multiple bossbar after issue `/world delete`. (teleport warmup module)
- fix: in-correct max pages display after the search operation in a paged gui.
- fix: the `/world reset` command will not reset the `force-loading chunks` in the target world.
- refactor: no longer depends on the `fabric-api` mod.
- **refactor: make `chat` module a sub-module `chat.style` module, this also allows to use the `chat.display` and `chat.history` module with `StyledChat` mod, without the `chat.style` module.**
- **refactor: move the directory `config/fuji/modules/motd/motd/icon` into `config/fuji/modules/motd/icon`. (motd module)**
