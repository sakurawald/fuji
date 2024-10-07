> The version number of fuji follows `semver` now: https://semver.org/ 

- feature: now the command suggestion for dynamic contents will be updated on the fly, without the server restart.
- feature: now will put a default kit set into a created new empty kit, as the slot placeholder to reduce the confusion. (kit module)
- feature: enhance the render function of bossbar, so that an instant bossbar will not be drawn in the client-side screen, if it's aborted in the first game-tick. (teleport warmup module and command warmup module)
- feature: now will not send the `Operation Success` as the command feedback if it has nothing to say. (follow the `no news is good news` rule.)
  - Influence: `/send-title`, `/send-toast`, `/command-attachment`, `/attachment`, `/burn`, `/freeze` and `/json`
- feature: now will not `info the console` on loading head categories, reducing the console spam. (head module)
- feature: a clearer gui design, now making the `helper button` using `book` item.
- feature: skip the un-necessary nametag making while the player is changing dimension. (nametag module) 
- feature: set the default requirement of commands registered by `predicate module` to level 4, since these commands is useless for default player. (predicate module)
- fix: the player stats of interact with functional blocks doesn't get increased when interact these functional blocks using `functional commands`: `/anvil`, `/grindstone`, `/stonecutter`. (functional module)
- fix: now will not create a new kit using `/kit give` command if the kit is not-exist.
- fix: ensure the kit item will be given even the specified slot is not empty.
- refactor: simplify the `fake player manager` module.