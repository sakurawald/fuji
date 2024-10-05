> **WARNING**:
>
> If you are using a version before `v1.5.9`, you should read the changelog of `v1.5.9`.

> The version number of fuji follows `semver` now: https://semver.org/

> Welcome our new contributors:
> - @notlin4
> - @Kaysil

- feature: add `named cooldown` feature for `command cooldown module`, now supports to create `named cooldown` to
  associate cooldown with commands.
    - create a named cooldown: `/command-cooldown create example 3000`
    - test a named cooldown:
      `/command-cooldown test example <player> --onFailed "say false %fuji:command_cooldown_left_time 1%/%fuji:command_cooldown_left_usage 1%" say true`
- feature: improve the kit giving logic:
    - first: try to insert item in specified slot.
    - second: try to insert item in any slot.
    - third: drop the item in the ground with the player as its thrower.
- feature: allow to use the `delimiter |` to separate `command string` into `command list`. (command_meta module) 
- feature: unify the style of error messages for all registered fuji placeholders.
- build: now this project will follow the `semver` specification: https://semver.org/
