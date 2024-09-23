> **WARNING**:
> 
> If you are using a version before `v1.5.9`, you should read the changelog of `v1.5.9`.
 
- feature: improve the performance of parsing text from language file, also avoid many un-necessary copy operation.
- feature: now use the placeholder provided by `spark mod` to display `/profiler` command. (profiler module)
- feature: report error in the console if failed to parse args for a language value.
- feature: add locale language keys for `deathlog module`.
- feature: now allow to enable `color module` in `single-player world`.
- fix: the `escape parser` can't escape the `fuji:random` placeholder. (e.g. `/run as console foreach give %fuji:escape player:name% minecraft:diamond %fuji:escape fuji:random 8 32 1%`)
