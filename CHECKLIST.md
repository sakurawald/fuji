# Checklist

It's good to have a checklist, to avoid forgetting something.

## Bump version

- read the change log of minecraft version
- update the version of fabric loader
- update the version of mappings
- update the version of dependent mods
- test the jar file in a real environment.

## Test new version
- test interesting commands. (from a to z)
- test interesting mixins.
- test the integration with carpet-fabric.
- test the integration with luckperms.

## Update to a new version
- update the version in "gradle.properties"
- update change log in "CHANGELOG.md"
- test the jar file in a real environment.
- publish the pdf file in "dev" branch.
- merge changes from "dev" branch into "release" branch.

## Some interesting tests
- Summon a fake player using `/player 1 spawn` and throw exp bottle to it. 
- See if a pickaxe gets the max power level in `/emchantment`.
- See an inventory display contains a shulker box.
- Try to move an afk player.
- See chunks using `/chunks` .
- Buy a new head in `/head`.
- Get a kit from `/kit`.
- Start a sample for a production work.
- Teleport to an offline player's location using `/tppos offline`.
- Enter afk using `/afk` and watch the player list.
