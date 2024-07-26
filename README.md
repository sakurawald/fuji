# Fuji-Fabric
![github workflow build job](https://img.shields.io/github/actions/workflow/status/sakurawald/fuji-fabric/build.yml)
![github license](https://img.shields.io/github/license/sakurawald/fuji-fabric)
![github commits since latest release](https://img.shields.io/github/commits-since/sakurawald/fuji-fabric/latest)
![modrinth download counter](https://img.shields.io/modrinth/dt/1TowMm2v)

## Description
Fuji is a minecraft mod that provides many essential and useful modules for vanilla survival.

> **_If minecraft gets updated, you can mail sakurawald@gmail.com for fuji update reminder._**

> This is a **server-side only** mod, but you can use it in a **single-player world**. (Yes, the single-player world also includes a logic-server)
> - For a server-maintainer: You only need to install this mod at the server-side, and the players don't need to install this mod at their client-side
> - For a player: You only need to install this mod at the client-side, and then you can use the modules in your single-player world.

## Feature
1. **Vanilla-Respect**: all the modules are designed to be as light-way as possible, and do the least change to the vanilla game. (Never touch the game-logic.)
2. **Fully-Modular**: you can disable any module completely if you don't like it. (Code is data is code. Thanks to the power of meta-programming, the module loader will even not load and inject the module text into the game, if you disable the module, without any performance issue.)
3. **High-Performance**: Keep performance in mind while coding. (From data-structure, algorithm, lazy-evaluation and cache to improve performance greatly.)
4. **Easy-to-Use**: a text-based per-field-explained auto-generated always-up-to-date documentation is provided. (Our configuration use lots of mini-language and generators to provide a unified setup.)

## Documentation
See [configuration](https://github.com/sakurawald/fuji-fabric/wiki/Configuration)

See [permission](https://github.com/sakurawald/fuji-fabric/wiki/Permission)

See [faq](https://github.com/sakurawald/fuji-fabric/wiki/FAQ)

## Reference
See [reference](https://github.com/sakurawald/fuji-fabric/blob/dev/REFERENCE)

## License
```
Copyright (C) 2023  sakurawald@gmail.com

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```