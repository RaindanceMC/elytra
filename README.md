# RaindanceMC/elytra

- **`elytra-module`** — Rain's Flexible Modules — Elytra: armour trims on elytra at the smithing table, and chestplate looks for elytra (`/elytra`, and a door on the cosmetics page) — a chestplate drawn with the wings, only as a look.
- **`elytra-standalone`** — Rain's Elytra: the elytra module and the standard wrapper, shaded together.

Part of [RaindanceMC](https://github.com/RaindanceMC); built on [RainsCore](https://github.com/RaindanceMC/RainsCore) and the shared parent in [framework](https://github.com/RaindanceMC/framework).

## How it works

- **Trims:** every trim recipe the server has gets a twin with the elytra as base; the game's trim recipe never checks the base, so the smithing table works as for armour. Vanilla 26.3 already draws trims on the `wings` layer from `textures/trims/entity/wings/<pattern>.png` — the pack brings those.
- **Looks:** an equipment asset per chestplate (`rainselytra:<material>_chestplate`: the vanilla chestplate's humanoid layers plus the elytra's wings); `/elytra` points the elytra's `equippable` asset at it. Armour points are untouched.
- **Pack:** contributed to Core's resource pack via `JarPack`. Built by `art/build-pack.py`: shaded wing trims from the *Elytra Trims* mod by KikuGie (LGPL-3.0-or-later, licence texts in the pack), the item overlay from *Bare Bones X Elytra Trims* by rando3214 (BSD-2-Clause), and the game's own equipment JSONs.

## Build

```
mvn install
```

Needs Java 25 and read access to `packages.tstieh.de`. Real-server scenario: `ElytraScenarioTest` in `RaindanceMC/e2e`.

## Plugins

- `elytra-standalone/target/RainsElytra-0.3.0.jar` — also published as `de.raindancer.modules:elytra-standalone:0.3.0`
