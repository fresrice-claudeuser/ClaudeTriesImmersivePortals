# Porting plan

Checked on 2026-10-26. Numbers and availability drift; re-verify before relying on them.

## 1. What the research found

| Fact | Consequence |
|---|---|
| No official 1.21.11 build exists. Upstream `iPortalTeam/ImmersivePortalsMod` was **archived 2026-04-21**; its newest code (`1.21` branch, v6.0.6) targets **MC 1.21.1** (Fabric API 0.109, Sodium 0.6.0, Iris 1.8.0). Its `build.gradle` uses **Mojang mappings + Parchment** (`loom.layered`), *not* Yarn - the `yarn_mappings` property in `gradle.properties` is an unused leftover **(verified by reading build.gradle through the GitHub connector)** | A source port across ten Minecraft versions. **No Yarn->Mojang migration is needed**: names, mixin targets and the access widener are already Mojang-named |
| An upstream discussion calls a 1.21.4/1.21.5 port "very difficult" because rendering was rewritten, and another user asks for 1.21.11 | The rendering rewrite is the wall (Wave 4) |
| Upstream depends on **DimLib** (`v1.1.0-mc1.21.1`). DimLib has published builds only for 1.21.1 and 1.20.4 | DimLib has to be ported too (Wave 1) |
| Upstream layout **(verified)**: one Gradle project; packages `qouteall.imm_ptl.core`, `qouteall.imm_ptl.peripheral`, `qouteall.q_misc_util`. Dependencies: Cloth Config (bundled), **DimLib** from `iPortalTeam/DimLib` via JitPack (bundled), Mod Menu / Sodium / Iris / GravityChanger optional | Port scope = core + peripheral + q_misc_util + DimLib. Cloth Config is replaced by our vanilla options screen |
| **Iris 1.10.x on 1.21.11 hard-requires Sodium 0.8.x** (Fabric's resolver says so in user crash reports) | "Works with Iris" implies "works with Sodium 0.8" first (Wave 5) |
| Upstream's own Iris support patches Iris internals ("hack the shader rendering process") and lists limits: translucents in front of portals, TAA, motion blur, portal overlay, cross-portal entity rendering | Upstream's Iris glue is pinned to Iris 1.8.0 and cannot be copied. It must be re-derived against Iris 1.10.x |
| Other efforts exist: an alpha for 1.21.2-1.21.5 on CurseForge ("Immersive Portals (UPDATED)", tested with Sodium 0.6.13 + Iris 1.8.11; source not located), Digitalwolf13's Apache-2.0 fork for 1.21.1/1.20.1 (compat/bug-fix focused; says it contains some AI-generated code), a CurseForge listing titled "Sodium/Iris/DH Compat Fork" for 1.20.1 (possibly the same project - unconfirmed), and a separate 26.2 port | Useful prior art to read. None is a 1.21.11 build |
| Loom `1.17.21` exists with plugin id `net.fabricmc.fabric-loom-remap` (obfuscated, <= 1.21.11); `net.fabricmc.fabric-loom` is for 26.1+ | Drives the bump design in [BUMPING.md](BUMPING.md) |

## 2. Strategy

Decision-tree result: no official build, no suitable refork, no usable newer-version branch -> **source port**
of upstream `1.21`. Ordered so every wave ends in something that boots and can be tested, and so the
risky rendering work comes only after everything underneath it is stable.

## 3. How we work (the loop)

I cannot compile or run Minecraft in my environment, and I cannot see your repo. So:

1. I produce changes as a zip / patch you commit.
2. CI builds. On failure the run summary has a paste-ready error block.
3. You paste that block (or upload `build.log`) and I fix the next layer of errors.
4. For rendering questions, run `./gradlew genSources` and send me the specific decompiled 1.21.11 class
   (e.g. `LevelRenderer`) - that is far more reliable than my memory of its API.

Each wave has a **gate**. Compile-green is never a gate on its own (mixins fail at load, not compile).

## 4. Waves

| # | Wave | Gate (all must pass) |
|---|---|---|
| 0 | **Scaffold** - build, CI, bump tool, config, Mod Menu screen, compat probes. *This commit.* | CI green; `runClient` shows the options screen; log prints detected Sodium/Iris/DH versions |
| 0.5 | **Import.** Bring upstream files in (GitHub connector, `tools/import_upstream.sh`, or the `import-upstream` workflow) as a pristine reference in `upstream/`, then move packages into `src/` wave by wave. **No mapping migration** (already Mojang names). Replace Cloth Config usage with the existing options screen. | Inventory reviewed; `upstream/` committed |
| 1 | **Base libraries**: upstream's misc utils + a DimLib port (dynamic dimensions) | Dedicated server boots to `Done`; log has no `FAILED during APPLY` / `was not applied` |
| 2 | **Portal core, no rendering**: portal entity, networking payloads, teleportation, chunk tracking, commands. Render hooks stubbed | Server boot; client connects; `/portal` commands work; teleport through a portal works (no see-through) |
| 3 | **Client multi-world**: remote `ClientLevel`s and per-dimension level renderers | Client logs remote-chunk sync; no leaks on dimension change |
| 4 | **Vanilla portal rendering** (no Sodium, no shaders): off-screen view render, front clipping, culling, portal-in-portal. **The wall.** | See through a portal in vanilla; depth 2 works; F3+T and resizing do not crash |
| 5 | **Sodium 0.8.x compat** (prerequisite for Iris) | Same visual checks as Wave 4 with Sodium installed |
| 6 | **Iris compat - MUST** | Checklist in [COMPAT.md](COMPAT.md) passes |
| 7 | **Wire the options screen** to real behaviour | Every option visibly does something or is removed |
| 8 | **Distant Horizons** (wanted) | See [COMPAT.md](COMPAT.md); minimum: no crash and DH disabled inside portal views |

Validation order inside every wave (each step catches what the previous cannot): clean compile -> dedicated-server
boot, grepping the log for mixin APPLY failures -> real client boot (client mixins are invisible to a
server boot) -> in-client play-through.

## 5. Known unknowns

- Exact upstream layout and size (the inventory answers this).
- Whether upstream's rendering tricks (off-screen buffers, clip planes injected into shaders, global GL state)
  have direct equivalents in 1.21.11's render code. Expect to redesign, not translate.
- Which of Sodium 0.8's internals upstream's Sodium glue relied on.
- DimLib's size and mixin surface.
- 1.21.11-specific vanilla changes that hit unrelated code (e.g. `ResourceLocation` is now `Identifier`;
  command permission checks changed).
