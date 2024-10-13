> The version number of fuji follows `semver` now: https://semver.org/ 
 
> This version including the **breaking changes** if you are using them:
> 1. refactor: rename the commands registered by `predicate module` into a unified style, all predicate commands are now suffixes with `?` character. (predicate module)


- feature: add `all_commands_require_level_4_permission_to_use_by_default` option.
- feature: add `gui support` for `/command-bundle list` command. (command bundle module)
- feature: add `/fuji inspect argument-types` command, to list all registered argument types. (fuji module)
- feature: add `/fuji inspect configurations` command, to list loaded configuration files. (fuji module)
- feature: only send the message feedback to player command source, to avoid the console spam. (predicate module)
- feature: add `predicate commands`: `/is-op?`, `/is-holding?`, `/has-exp?`, `/has-exp-level?`, `/is-in-world?`, `/is-in-gamemode?` (predicate module)
- feature: allow to define `multiple default skins`. (skin module)
- refactor: enhance the compatibility of hopper mixin of `production work`. (works module)
- refactor: change the ui style from `oak button` into `quartz button`, it's clearer.
- refactor: remove the un-used option `random_skin`