> The version number of fuji follows `semver` now: https://semver.org/ 
 
> This version including the following **breaking changes** is you are using them:
> - In this version, the `chat related modules` are divided into small pieces, and intended to work with other chat-related mods, like: patbox's  `styled chat`.
>   - feature: split the `sender` and `content` in message, instead of encode the `sender` into the `content`, making it compatibility with other `chat linking mods`. (chat.style module)
>   - feature: make the joint-point of `chat.style module` more precises, with better compatibility.
>   - feature: extract `rewrite feature` from `chat.style module` into `chat.rewrite module`, making it possible to use with other chat-related mods.
>   - feature: extract `mention player feature` from `chat.style module` into `chat.mention module`, making it possible to use with other chat-related mods.
>   - feature: extract `chat spy feature` from `chat.style module` into `chat.spy module`, making it possible to use with other chat related mods.
>   - refactor: move `%fuji:player_prefix%`, `%fuji:player_suffix%` and `%fuji:pos%` placeholders from `chat.style module` into `placeholder module`.
>   - refactor: rename `/chat format` into `/chat style`. (chat.style module)


- feature: add `/fuji inspect registry` command, to list all registries in the server, including static registries and dynamic registries.
- feature: add `token replacement` for `chat.display module`, making it possible to use with other chat-related mods.
- feature: support deeper-level style passing in language file when replacing texts.
- feature: lazy computation for text replacement in language file.
- fix: accessing legacy random source from multiple threads in `mention player task`.
