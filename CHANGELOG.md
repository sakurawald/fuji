> **WARNING**:
> 
> If you are using a version before `v1.5.9`, you should read the changelog of `v1.5.9`.


- feature: add `command bundle` module. (command-bundle module)
  - a user-friendly DSL to create new custom commands easily, with the interoperation of `variables`, `placeholders` and `selectors`.
  - support complex `command argument type`: `required argument`, `literal argument` and even the `optional argument` with a default value. 
  - a powerful type-system to ensure the `type-safe` input, with fully command suggestion.
  - register and un-register commands on the fly, without the server restart!
  - new commands: `/command-bundle list`, `/command-bundle list-type-strings`...
- feature: add `log_debug_messages` option in `core` in `config.json`.
- fix: the y finder for chunk teleportation. (top_chunks module)
