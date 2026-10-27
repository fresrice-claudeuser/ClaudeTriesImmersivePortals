# ClaudeTriesImmersivePortals

A port of [Immersive Portals](https://github.com/iPortalTeam/ImmersivePortalsMod) (see-through portals,
seamless teleportation) to **Minecraft 1.21.11 on Fabric**.

| Goal | Priority | Status |
|---|---|---|
| Works with **Iris** (shader packs) | **MUST** | not started - needs Sodium compat first, see [docs/COMPAT.md](docs/COMPAT.md) |
| Mod Menu entry with graphical options | wanted | **options screen done**; settings not wired to portals yet |
| Distant Horizons: portal shows the other dimension's LODs | wanted | research - see [docs/COMPAT.md](docs/COMPAT.md) |
| Easy bump to 26.1 / 26.2 | requirement | **done for the build**, see [docs/BUMPING.md](docs/BUMPING.md) |

> **Honest status: this is the foundation, not a working port yet.** Right now the jar loads on 1.21.11,
> shows an options screen in Mod Menu, and reports which of Sodium / Iris / Distant Horizons it found.
> There are no portals in it. The plan to get there is in [docs/PORTING_PLAN.md](docs/PORTING_PLAN.md).

## Getting started (no local setup needed)

1. Every push runs the **build** workflow automatically.
   - Green: download the jar from the run's *Artifacts*.
   - Red: open the run, copy the "Build failed - first problems" block from the summary, and send it to Claude.
2. Optional, one click: **Actions -> add-gradle-wrapper -> Run workflow**. Commits `gradlew` so you can build locally.
3. Optional, one click: **Actions -> import-upstream -> Run workflow**. Pulls a reference copy of upstream into `upstream/`
   and writes `docs/upstream-inventory.md` (no local tools needed; this is the Windows-friendly route).

## How the port proceeds

Upstream code is copied in verbatim by **Actions -> import-wave**, then fixed against real compile errors. Hand fixes live in `overlays/` so they survive re-imports. Details: [docs/PORTING_PLAN.md](docs/PORTING_PLAN.md).

## Running it locally

Needs a JDK matching `java_version` in `gradle.properties` (21 now, 25 for 26.x).

```
./gradlew runClient     # Minecraft + Fabric API + Mod Menu + Sodium + Iris (dev_runtime=true)
./gradlew build         # jar in build/libs/
./gradlew mcInfo        # prints the Minecraft target the build is currently set to
```

Open **Mods -> Immersive Portals (Claude port) -> config button** to see the options screen.
Settings are saved to `config/claude_immersive_portals.json`.

## Bumping Minecraft

```
python tools/bump_mc.py 26.1      # rewrites gradle.properties + the one Loom plugin line
```

Then compile and follow the errors. Details and what to expect: [docs/BUMPING.md](docs/BUMPING.md).

## Layout

| Path | What |
|---|---|
| `gradle.properties` | The single source of truth for versions |
| `versions/*.properties` | Presets used by `tools/bump_mc.py` |
| `src/main/java/dev/claudeportals/` | Glue code that is new in this port (config, compat probes, Mod Menu) |
| `upstream/` | *(after import)* pristine upstream source, reference only, never compiled |
| `tools/` | `bump_mc.py`, `import_upstream.sh`, `inventory.py` |
| `docs/` | Plan, bump guide, compat notes |

## Notes

- **Java only for now.** Upstream and every mixin are Java, so Kotlin would add a runtime dependency
  (Fabric Language Kotlin) for no benefit yet. It can be wired in later for new non-mixin code.
- Source is written against **Mojang names** from the start, so moving to 26.1 (unobfuscated) needs no rename pass.
- Licensed Apache-2.0, derived from upstream. See [NOTICE.md](NOTICE.md).
