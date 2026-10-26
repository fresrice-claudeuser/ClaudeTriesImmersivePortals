# Bumping to a new Minecraft version

Minecraft moved to year-based versions: **26.1 is the first new-scheme release and it is not 1.21.11**.
26.1+ is also the first *unobfuscated* line, which changes the build in a small, fixed way.
The repo is set up so that part is one command.

```
python tools/bump_mc.py --list
python tools/bump_mc.py 26.1 --dry-run
python tools/bump_mc.py 26.1
gradle clean build
```

## What the script changes (and nothing else)

| Where | 1.21.11 | 26.1+ |
|---|---|---|
| Loom plugin id in `build.gradle` | `net.fabricmc.fabric-loom-remap` | `net.fabricmc.fabric-loom` |
| `minecraft_obfuscated` | `true` | `false` |
| Mappings line | `mappings loom.officialMojangMappings()` | none (build.gradle skips it automatically) |
| `modImplementation` / `modCompileOnly` / `modLocalRuntime` | used | `implementation` / `compileOnly` / `localRuntime` (build.gradle switches automatically) |
| Jar task | `remapJar` | `jar` (Loom handles it) |
| `java_version` | 21 | 25 (CI reads it from `gradle.properties`; use JDK 25 locally) |
| Fabric API, Mod Menu, loader | per preset | per preset |

Because the code uses Mojang names already, **there is no rename/migration pass** when crossing to 26.1.

## What the script cannot do

1. **Fix Java code.** Compile, read errors, fix. Authoritative lists of what changed in vanilla:
   NeoForge's primer chain (`docs.neoforged.net/primer/docs/26.1/`, then `/26.2/`), and Fabric's
   "Porting to 26.1" page plus the Fabric API 26.1 porting guide (renames in Fabric API).
2. **Bump Sodium / Iris / Distant Horizons.** Mods built for 1.21.11 do not work on 26.1, not even as
   compile-only dependencies. The 26.x presets ship with `dev_runtime=false`. To enable: open the
   mod's version page on Modrinth, copy the Maven coordinates, fill in `sodium_version` /
   `iris_version` in the preset, set `dev_runtime=true`.
3. **Decide that the jump is cheap.** 26.1 is mostly data-side churn (loot, recipes, villager trades,
   saved-data storage) which this mod barely touches. **26.2 is different**: it removes
   `MultiBufferSource` and reworks rendering and the GUI. For a rendering mod that is a real port.

## Presets

| Preset | MC | Java | Fabric API | Mod Menu | Dev-runtime Sodium/Iris |
|---|---|---|---|---|---|
| `1.21.11` | 1.21.11 | 21 | 0.141.4+1.21.11 | 17.0.0 | yes (Sodium 0.8.7, Iris 1.10.7) |
| `26.1` | 26.1.2 | 25 | 0.155.3+26.1.2 | 18.0.2 | no - coordinates to verify |
| `26.2` | 26.2 | 25 | 0.161.0+26.2 | 20.0.3 | no - coordinates to verify |

These numbers drift. Re-check before trusting a bump: Fabric loader/API on `meta.fabricmc.net` /
Modrinth, Mojang's version manifest for the Java major, and the Loom release list on
`maven.fabricmc.net`.

## `fabric.mod.json`

The Minecraft predicate comes from `minecraft_dependency` in the preset (`1.21.11`, then `>=26.1 <26.2`),
so the same file works across versions. Keep the upper bound tight while the mod depends on render internals.
