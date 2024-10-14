> The version number of fuji follows `semver` now: https://semver.org/ 
 
- docs: add new many examples.
- docs: adjust the layout of pages.
- feature: add `/skin use-online-skin` command. (skin module)
- refactor: use network packet to execute commands instead of executing the commands internally. (command interactive module)
  - This also fix the `command warmup` bypass when using `command interactive` to execute commands.
  - Enhance the compatibility with other mods.
- refactor: rename `/skin clear` into `/skin use-default-skins`. (skin module)
- fix: the position of nametag will de-sync after the player set a new skin. (skin module + nametag module)
- fix: the missing language argument if the dimension of works is not exist. (works module)
- fix: the `object.value` language key doesn't parse the literal value. (fuji module)