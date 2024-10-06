> **WARNING**:
>
> If you are using a version before `v1.5.9`, you should read the changelog of `v1.5.9`.

> The version number of fuji follows `semver` now: https://semver.org/

> Welcome our new contributors:
> - @notlin4
> - @Kaysil

- feature: add `/send-custom` command, which sends the `custom text` to a player, with pagination feature. (echo.send_custom module)
  - send custom text as a `book`: `/send-custom as-book <player> guide --author "alice" --title "<rb>The Guice" --giveBook true --openBook true `
  - send custom text as a `message`: `/send-custom as-message <player> guide `
- docs: add many examples into the docs.
- 