> **WARNING**:
> 
> If you are using a version before `v1.5.9`, you should read the changelog of `v1.5.9`.

- feature: add `/world list` command, to list all loaded worlds. (world module)
- feature: add `/fuji list-commands` command, to query all commands registered in the server. (fuji module)
- feature: add sign cache for `color.sign` module, making the `sign text edit` reversible, don't lose your edit work! (color.sign module)
- **fix: ensure the requirement of commands is wrapped by command permission module after the server restart. (command_permission module)**
- fix: the `anti_build` mixin doesn't work in `single-player world` in the client. (anti_build module)
- fix: the `command spy` doesn't work in `single-player world` in the client. (command spy module)
