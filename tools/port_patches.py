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
