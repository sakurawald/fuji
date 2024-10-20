> The version number of fuji follows `semver` now: https://semver.org/ 
 
> This version including the following **breaking changes** if you are using them:
> - refactor: lower the power of `chat.style content parser`.
>   - remove: the ability to parse `markdown language`. (This can be done via command rewrite if necessary, avoids the bypass of the usage of style tags.)
>   - remove: the ability to parse `placeholders`, avoids the abuse of evaluating placeholders in chat message sent by players.
> - refactor: set the default requirement of level permission to 4 for `/lore` command. (command_toolbox.lore module)
> - refactor: set the default requirement of level permission to 4 for `/repair` command. (command_toolbox.repair module)

- feature: add `document string` for `/fuji inspect fuji-commands`, making all commands registered by fuji `self-explanatory`, with explanation of `function` and `argument`.
- feature: add `gui support` for `/warp` command. (warp module)
  - add `/warp set-name` to set the display name of a warp.
  - add `/warp set-item` to set the item of a warp.
  - add `/warp set-lore` to set the lore of a warp.
- feature: add new module `chat.stripe` to stripe `style tags` based on permissions, making it possible to control the usage of style tags. (chat.stripe module)
- feature: add the ability to `cancel` a system message. (system_message module)
- feature: add `transform nickname` option, which allows to add prefix, suffix and truncate the input nickname. (nickname module)
- feature: skip un-necessary re-draw for `/fuji inspect configuration` and `/fuji inspect registry`. (fuji module)
- build: improve the github templates to use new features provided by github, providing a better experience for user feedback.
- fix: should not warn the console about `required mods not installed` if the related module is disabled in `config/fuji/config.json`. (carpet module)