#!/usr/bin/env python3
"""
Exact-text patches applied to imported files after overlays and codemods.

    python tools/port_patches.py

Each patch is a dict: file, kind ("text" or "regex"), old, new [, count].
  text  - `old` must occur exactly `count` times (default 1) and is replaced by `new`
  regex - re.sub with DOTALL; must match at least once
Rules:
  * Patches for one file are applied together, in order, in memory. If any fails, that file is left untouched.
  * After a successful apply the file gets a marker comment on its last line. A file that has the marker is
    skipped, so running this script twice never applies anything twice. A fresh import (which re-copies the
    pristine upstream file) removes the marker, so patches apply again.
  * If a patch cannot be applied the script exits non-zero and lists the failures (upstream text changed, or an
    earlier patch altered it). Every patch must be backed by a real compile error or an inspect result.
"""
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
D = "src/main/java/qouteall/dimlib/"
MARKER = "// [tools/port_patches.py: patches applied]"

PATCHES = [
    # --- 1.21.11: ChunkProgressListener is gone; createLevels() takes no arguments; ServerLevel's constructor
    #     lost its listener parameter (verified with `inspect` on the real jar) ---
    dict(file=D + "DynamicDimensionsImpl.java", kind="text",
         old="import net.minecraft.server.level.progress.ChunkProgressListener;\n", new=""),
    dict(file=D + "DynamicDimensionsImpl.java", kind="text",
         old="MinecraftServer#createLevels(ChunkProgressListener)", new="MinecraftServer#createLevels()"),
    dict(file=D + "DynamicDimensionsImpl.java", kind="text",
         old="            new DummyProgressListener(),\n", new=""),
    dict(file=D + "DynamicDimensionsImpl.java", kind="regex",
         old=r"\n    private static class DummyProgressListener implements ChunkProgressListener \{.*?\n    \}[ \t]*\n",
         new="\n"),
    dict(file=D + "mixin/common/MixinMinecraftServer.java", kind="text",
         old="import net.minecraft.server.level.progress.ChunkProgressListener;\n", new=""),
    dict(file=D + "mixin/common/MixinMinecraftServer.java", kind="text", count=2,
         old="createLevels(Lnet/minecraft/server/level/progress/ChunkProgressListener;)V", new="createLevels()V"),
    dict(file=D + "mixin/common/MixinMinecraftServer.java", kind="text",
         old="ChunkProgressListener worldGenerationProgressListener, CallbackInfo ci", new="CallbackInfo ci"),

    # --- 1.21.5+: CompoundTag.getString returns Optional<String>; getStringOr gives the old behaviour ---
    dict(file=D + "DimLibNetworking.java", kind="text",
         old="String dimTypeId = tag.getString(key);", new='String dimTypeId = tag.getStringOr(key, "");'),

    # --- ServerPlayer.server is private now; PlayerList has its own private `server` field ---
    dict(file=D + "mixin/common/MixinPlayerList.java", kind="text",
         old="import org.spongepowered.asm.mixin.Mixin;\n",
         new=("import net.minecraft.server.MinecraftServer;\n"
              "import org.spongepowered.asm.mixin.Final;\n"
              "import org.spongepowered.asm.mixin.Mixin;\n"
              "import org.spongepowered.asm.mixin.Shadow;\n")),
    dict(file=D + "mixin/common/MixinPlayerList.java", kind="text",
         old="public class MixinPlayerList {\n",
         new="public class MixinPlayerList {\n    @Shadow\n    @Final\n    private MinecraftServer server;\n\n"),
    dict(file=D + "mixin/common/MixinPlayerList.java", kind="text",
         old="createPacket(player.server)", new="createPacket(this.server)"),

    # --- 1.21.11 world border: every dimension has its own WorldBorder (SavedData); BorderChangeListener.Delegate...
    #     and ServerLevelData.getWorldBorder() are gone. Verified against decompiled MinecraftServer.createLevels:
    #     per level it only calls setAbsoluteMaxSize(...) and PlayerList.addWorldborderListener(level). ---
    dict(file=D + "DynamicDimensionsImpl.java", kind="regex",
         old=r"[ \t]*worldBorder\.addListener\(\s*new BorderChangeListener\.DelegateBorderChangeListener\(newWorld\.getWorldBorder\(\)\)\s*\);[ \t]*\n",
         new=("        // 1.21.11: world borders are per dimension now (SavedData). Like vanilla createLevels, a new level only needs its\n"
              "        // absolute max size set and the packet-broadcasting listener registered; nothing is mirrored from the overworld.\n"
              "        newWorld.getWorldBorder().setAbsoluteMaxSize(server.getAbsoluteMaxWorldSize());\n"
              "        server.getPlayerList().addWorldborderListener(newWorld);\n")),
    dict(file=D + "DynamicDimensionsImpl.java", kind="text",
         old="        worldBorder.applySettings(serverLevelData.getWorldBorder());\n", new=""),
    dict(file=D + "DynamicDimensionsImpl.java", kind="regex",
         old=r"[ \t]*for \(ServerLevel serverWorld : server\.getAllLevels\(\)\) \{.*?DelegateBorderChangeListener\(serverWorld\.getWorldBorder\(\)\)\s*\);\s*\}\s*\}[ \t]*\n",
         new="        // 1.21.11: no per-dimension border listeners to rebuild (DelegateBorderChangeListener is gone).\n"),

    dict(file=D + "DynamicDimensionsImpl.java", kind="text",
         old="            resetWorldBorderListener(server);\n", new=""),

    # --- ServerChunkCache.removeTicketsOnClosing() was renamed deactivateTicketsOnClosing(); vanilla stopServer uses it.
    #     It matters: ChunkMap.hasWork() includes distanceManager.hasTickets(), so without this call the wait loop
    #     below always runs into its 15 second timeout. ---
    dict(file=D + "DynamicDimensionsImpl.java", kind="text",
         old="world.getChunkSource().removeTicketsOnClosing();", new="world.getChunkSource().deactivateTicketsOnClosing();"),

    # --- ServerLevel.getSharedSpawnPos() is gone; ServerLevel.getRespawnData() returns LevelData.RespawnData.
    #     `.pos()` is a guess that the next build confirms or rejects. ---
    dict(file=D + "DynamicDimensionsImpl.java", kind="text",
         old="BlockPos sharedSpawnPos = overworld.getSharedSpawnPos();",
         new="BlockPos sharedSpawnPos = overworld.getRespawnData().pos();"),

    # --- 1.21.11 command permissions: hasPermission(int) -> Commands.hasPermission(Commands.LEVEL_GAMEMASTERS) (level 2) ---
    dict(file=D + "DimsCommand.java", kind="text",
         old="source -> source.hasPermission(2)",
         new="net.minecraft.commands.Commands.hasPermission(net.minecraft.commands.Commands.LEVEL_GAMEMASTERS)"),

    # --- Mixin targets that would crash at game start: Minecraft.updateLevelInEngines now has two overloads ---
    dict(file=D + "mixin/client/MixinMinecraft.java", kind="text",
         old='method = "updateLevelInEngines"',
         new='method = "updateLevelInEngines(Lnet/minecraft/client/multiplayer/ClientLevel;Z)V"'),
    dict(file=D + "mixin/client/MixinMinecraft.java", kind="text",
         old="onClientReset(ClientLevel level, CallbackInfo ci)",
         new="onClientReset(ClientLevel level, boolean clearSounds, CallbackInfo ci)"),
]


def apply_one(text: str, patch: dict) -> tuple:
    kind, old, new = patch["kind"], patch["old"], patch["new"]
    if kind == "text":
        expected = patch.get("count", 1)
        found = text.count(old)
        if found != expected:
            return text, f"expected {expected} match(es) of {old[:70]!r}, found {found}"
        return text.replace(old, new), ""
    if kind == "regex":
        new_text, n = re.subn(old, new, text, flags=re.DOTALL)
        if n == 0:
            return text, f"regex matched nothing: {old[:70]!r}"
        return new_text, ""
    return text, f"unknown patch kind {kind!r}"


def main() -> int:
    by_file = {}
    for patch in PATCHES:
        by_file.setdefault(patch["file"], []).append(patch)

    applied, skipped, failures = 0, 0, []
    for rel, patches in by_file.items():
        path = ROOT / rel
        if not path.exists():
            failures.append(f"{rel}: file not found")
            continue
        text = path.read_text(encoding="utf-8")
        if MARKER in text:
            skipped += len(patches)
            continue
        problems = []
        for patch in patches:
            text, problem = apply_one(text, patch)
            if problem:
                problems.append(problem)
        if problems:
            failures.extend(f"{rel}: {p}" for p in problems)
            continue  # leave the file untouched
        path.write_text(text.rstrip("\n") + "\n" + MARKER + "\n", encoding="utf-8")
        applied += len(patches)

    print(f"patches: {applied} applied, {skipped} already applied, {len(failures)} failed")
    for msg in failures:
        print("  FAILED:", msg, file=sys.stderr)
    return 1 if failures else 0


if __name__ == "__main__":
    sys.exit(main())
