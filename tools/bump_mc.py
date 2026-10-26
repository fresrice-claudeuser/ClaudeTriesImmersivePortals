#!/usr/bin/env python3
"""
Retarget the build at a different Minecraft version.

    python tools/bump_mc.py 26.1            # apply versions/26.1.properties
    python tools/bump_mc.py 26.1 --dry-run  # show what would change
    python tools/bump_mc.py --list          # list presets

What it does (and all it does):
  1. copies every key from versions/<preset>.properties into gradle.properties
  2. flips the Loom plugin id in build.gradle
       net.fabricmc.fabric-loom-remap  (obfuscated, <= 1.21.11)
       net.fabricmc.fabric-loom        (unobfuscated, >= 26.1)
It does NOT touch Java code: Minecraft API changes are fixed by compiling and following the
errors (see docs/BUMPING.md). Standard library only.
"""
import argparse
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
PLUGIN_RE = re.compile(r"id\s+'net\.fabricmc\.fabric-loom(?:-remap)?'")


def read_props(path: pathlib.Path) -> dict:
    props = {}
    for raw in path.read_text(encoding="utf-8").splitlines():
        line = raw.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        props[key.strip()] = value.strip()
    return props


def apply_props(text: str, new: dict) -> str:
    for key, value in new.items():
        pattern = re.compile(rf"^{re.escape(key)}=.*$", re.MULTILINE)
        if pattern.search(text):
            text = pattern.sub(lambda _m, k=key, v=value: f"{k}={v}", text)
        else:
            text = text.rstrip("\n") + f"\n{key}={value}\n"
    return text


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("preset", nargs="?", help="preset name, e.g. 1.21.11 or 26.1")
    ap.add_argument("--dry-run", action="store_true")
    ap.add_argument("--list", action="store_true")
    args = ap.parse_args()

    presets = sorted(p.stem for p in (ROOT / "versions").glob("*.properties"))
    if args.list or not args.preset:
        print("Available presets:", ", ".join(presets) or "(none)")
        return 0 if args.list else 2
    if args.preset not in presets:
        print(f"Unknown preset '{args.preset}'. Available: {', '.join(presets)}", file=sys.stderr)
        return 2

    new = read_props(ROOT / "versions" / f"{args.preset}.properties")
    new["mc_preset"] = args.preset
    obfuscated = new.get("minecraft_obfuscated", "").lower() == "true"
    plugin = "net.fabricmc.fabric-loom-remap" if obfuscated else "net.fabricmc.fabric-loom"

    gp = ROOT / "gradle.properties"
    bg = ROOT / "build.gradle"
    new_gp = apply_props(gp.read_text(encoding="utf-8"), new)
    old_bg = bg.read_text(encoding="utf-8")
    if not PLUGIN_RE.search(old_bg):
        print("Could not find the Loom plugin id line in build.gradle (expected: id 'net.fabricmc.fabric-loom[-remap]').",
              file=sys.stderr)
        return 1
    new_bg = PLUGIN_RE.sub(f"id '{plugin}'", old_bg, count=1)

    print(f"Target: Minecraft {new['minecraft_version']}  | Loom plugin: {plugin}  | Java {new['java_version']}")
    print(f"gradle.properties: {'would change' if new_gp != gp.read_text(encoding='utf-8') else 'already up to date'}")
    print(f"build.gradle:      {'would change' if new_bg != old_bg else 'already up to date'}")

    blanks = [k for k, v in new.items() if v == ""]
    if blanks:
        print(f"NOTE: unset in preset: {', '.join(blanks)} (fine while dev_runtime=false; fill in before enabling it)")
    if new.get("dev_runtime", "").lower() == "true" and blanks:
        print("ERROR: dev_runtime=true but sodium/iris versions are blank.", file=sys.stderr)
        return 1

    if args.dry_run:
        print("(dry run - nothing written)")
        return 0
    gp.write_text(new_gp, encoding="utf-8")
    bg.write_text(new_bg, encoding="utf-8")
    print("\nWritten. Next:")
    print("  1. Use JDK", new["java_version"], "locally (CI reads java_version from gradle.properties automatically).")
    print("  2. gradle clean build   -> fix compile errors (read docs/BUMPING.md for what to expect).")
    print("  3. If dev_runtime=false, look up Sodium/Iris coordinates on Modrinth and fill them in.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
