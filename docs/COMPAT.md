# Compatibility notes: Iris, Sodium, Distant Horizons

Items marked **(verified)** were checked against a public source on 2026-10-26.
Items marked **(hypothesis)** are my expectations and must be confirmed against the real code in the wave named.

## Pinned for development

| Mod | Version | Note |
|---|---|---|
| Sodium | `mc1.21.11-0.8.7-fabric` | version page exists on Modrinth **(verified)** |
| Iris | `1.10.7+1.21.11-fabric` | version page exists **(verified)**. 1.10.8 also exists for 1.21.11 per a download listing. Sodium 0.8.13 is declared incompatible with Iris <= 1.10.7, so pin them as a pair |
| Mod Menu | `17.0.0` | used by several published 1.21.11 mods **(verified)** |

## Iris (MUST) - Wave 6

**Verified**
- Iris on 1.21.11 depends on Sodium `0.8.x`. Sodium compat comes first.
- Iris's default git branch is `26.1`; the 1.21.11 source is on a different branch. Read that one.
- Upstream Immersive Portals supported Iris 1.8.0 on 1.21.1 only "experimentally" and documents what shader
  rendering cannot do (see [PORTING_PLAN.md](PORTING_PLAN.md)). We should state the same limits rather than claim more.

**Verified from upstream source (1.21 branch)**
- The Iris glue is small: `compat/iris_compatibility/` has 8 files (`IrisPortalRenderer`, `ExperimentalIrisPortalRenderer`, `IrisCompatibilityPortalRenderer`, `ShadowMapSwapper`, `IrisInterface`, `IPIrisHelper`, two duck interfaces) plus 7 small mixins into Iris (`ClearPass`, `FinalPassRenderer`, `Iris`, `RenderingPipeline`, `ShadowRenderTargets`, `SodiumShader`, `TransformPatcher`). Sodium has its own separate compat package.
- `IrisPortalRenderer` draws portals **after** Iris's main pass into per-layer "deferred" framebuffers using **raw OpenGL** (`glBlitFramebuffer`, stencil ops, `GlStateManager`) and `RenderTarget.frameBufferId` / `bindWrite`. Expect this to need a rewrite on 1.21.11, not a rename.
- It falls back to a "compatibility" render mode (no portal-in-portal) when GL reports an error.
- Upstream's dev setup adds `jcpp`, `glsl-transformer` and `antlr4-runtime` to the classpath "for iris". If `runClient` with `dev_runtime=true` throws `NoClassDefFoundError` for those, that's why; add matching versions for Iris 1.10.x.

**Design rules (my decisions, not upstream's)**
1. Iris is a soft dependency. All Iris-touching code lives in `dev.claudeportals.compat.iris`, behind
   `ModCompat.iris()`. Iris-targeting mixins go in a **separate mixin config applied conditionally** through a
   `IMixinConfigPlugin`, so the mod still loads without Iris.
2. Prefer Iris's public API (`IrisApi`). Patch internals only where the API has no hook, and isolate each patch.
3. Fail soft: if the Iris bridge throws while drawing a portal, disable portal rendering for that session and show one
   chat warning. A shader-pack problem must never crash the game.
4. `shader_compat` option: `AUTO` (on whenever a pack is active), `ON`, `OFF`.

**Hypotheses to confirm in Wave 6**
- Iris keeps a separate pipeline per dimension, so drawing another dimension's view needs that dimension's pipeline.
- Shadow and depth passes must not run (or must be reused) inside nested portal views.
- Anything keyed on "the current level" (uniforms, previous-frame buffers for TAA/motion blur) breaks inside a portal view.

**Acceptance checklist (Wave 6 exit)**
1. Sodium + Iris, *no* pack: portals render.
2. BSL and Complementary packs: portals render; no crash; no black or garbled portal surface.
3. Toggle the pack on and off while looking at a portal.
4. Walk through a portal repeatedly in both directions (pipeline switching).
5. Portal-in-portal at depth 2.
6. F3+T and window resize near a portal.
7. A pack that fails to compile does not crash the game.
8. The documented limitations (translucents, TAA, motion blur) behave as documented, not worse.

## Sodium - Wave 5

Required by Iris on 1.21.11. Upstream's Sodium glue targets Sodium 0.6.0 internals and will not carry over.
Same approach as Iris: separate conditional mixin config, isolated package, fail-soft.

## Distant Horizons (wanted) - Wave 8

**Verified**
- DH supports MC 1.21.10-1.21.11 on Modrinth; its public API (`DhApi`, events, a generic rendering API) is published
  separately as `distanthorizonsapi` (version 4.1.0 seen).
- Iris already ships DH-API integration (its repo carries `DHApi.jar`).

**The core difficulty (hypothesis)**: DH draws LODs for the *active* level. Showing the destination dimension's LODs
through a portal needs a second LOD render context for another `ClientLevel`, plus loading that level's data around
the portal's exit. I have not found a public API for that.

**Research tasks, in order**
1. Read the DH API for level-load events and any way to bind an extra level wrapper.
2. Read Digitalwolf13's Apache-2.0 fork (1.21.1 / 1.20.1) and the "Sodium/Iris/DH Compat Fork" CurseForge listing for how they avoid conflicts; confirm first whether they are the same project and whether they really handle DH.
3. If the API cannot do it: choose between mixing into DH internals (fragile, LGPL-compatible) or asking the DH team
   for an API.
4. **Minimum acceptable result**: no crash with DH installed, and DH disabled inside portal views.

Options `dh_compat` and `dh_radius` already exist in the config screen; they stay inert until this wave.
