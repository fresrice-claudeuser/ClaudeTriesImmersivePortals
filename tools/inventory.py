#!/usr/bin/env python3
"""
Scan a Java source tree and write a Markdown "heat map" of where the hard porting surface is.

    python tools/inventory.py upstream/src docs/upstream-inventory.md

Reports: size per package, mixin counts and their targets, and which files touch rendering,
Iris, Sodium, Distant Horizons, DimLib, networking. Pure stdlib; safe to run anywhere.
"""
import collections
import json
import pathlib
import re
import sys

SURFACES = {
    "Rendering (blaze3d / client.renderer / LWJGL / GL)": r"com\.mojang\.blaze3d|net\.minecraft\.client\.render|org\.lwjgl|RenderSystem|GlStateManager|\bShaderInstance\b|\bRenderTarget\b",
    "Iris": r"net\.irisshaders|net\.coderbot\.iris|\bIrisApi\b",
    "Sodium": r"me\.jellysquid|net\.caffeinemc|\bSodiumWorldRenderer\b",
    "Distant Horizons": r"com\.seibel|\bDhApi\b",
    "DimLib / dynamic dimensions": r"qouteall\.dimlib|\bDimensionAPI\b|\bDimLib\b",
    "Networking (payloads / Fabric networking)": r"CustomPacketPayload|CustomPayload|PayloadTypeRegistry|ClientPlayNetworking|ServerPlayNetworking",
    "Mixin plumbing (accessors/invokers/shadows)": r"@Accessor|@Invoker|@Shadow",
}
MIXIN_TARGET = re.compile(r"@Mixin\s*\(\s*(?:value\s*=\s*)?\{?\s*([A-Za-z0-9_.$]+)\.class|targets\s*=\s*\"([^\"]+)\"")


def main() -> int:
    if len(sys.argv) != 3:
        print(__doc__)
        return 2
    src, out = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2])
    java = sorted(src.rglob("*.java"))
    if not java:
        print(f"No .java files under {src}", file=sys.stderr)
        return 1

    pkg_files, pkg_loc = collections.Counter(), collections.Counter()
    hits = {name: [] for name in SURFACES}
    mixin_files, mixin_targets = [], collections.Counter()
    total_loc = 0
    for path in java:
        text = path.read_text(encoding="utf-8", errors="replace")
        loc = sum(1 for line in text.splitlines() if line.strip())
        total_loc += loc
        rel = path.relative_to(src)
        parts = rel.parts
        # group by the first 4 directories under java/ (e.g. qouteall/imm_ptl/core/render)
        try:
            base = parts.index("java") + 1
        except ValueError:
            base = 0
        pkg = "/".join(parts[base:base + 4][:-1] if len(parts[base:base + 4]) == len(parts[base:]) else parts[base:base + 4])
        pkg_files[pkg] += 1
        pkg_loc[pkg] += loc
        for name, rx in SURFACES.items():
            if re.search(rx, text):
                hits[name].append((str(rel), loc))
        if "@Mixin" in text:
            mixin_files.append(str(rel))
            for m in MIXIN_TARGET.finditer(text):
                mixin_targets[(m.group(1) or m.group(2)).split(".")[-1]] += 1

    mixin_cfgs = []
    for cfg in sorted(src.rglob("*.mixins.json")):
        try:
            data = json.loads(cfg.read_text(encoding="utf-8"))
            n = {k: len(data.get(k, [])) for k in ("mixins", "client", "server")}
            mixin_cfgs.append((str(cfg.relative_to(src)), n))
        except Exception as e:  # noqa: BLE001
            mixin_cfgs.append((str(cfg.relative_to(src)), f"unreadable: {e}"))

    L = ["# Upstream inventory", "", f"- Java files: **{len(java)}**, non-blank lines: **{total_loc:,}**",
         f"- Classes containing `@Mixin`: **{len(mixin_files)}**", ""]
    L += ["## Size by package", "", "| package | files | lines |", "|---|---:|---:|"]
    for pkg, n in pkg_loc.most_common():
        L.append(f"| `{pkg or '.'}` | {pkg_files[pkg]} | {n:,} |")
    L += ["", "## Mixin configs", ""]
    L += [f"- `{name}`: {n}" for name, n in mixin_cfgs] or ["- (none found)"]
    L += ["", "## Most-targeted classes by mixins (names as written in source)", ""]
    L += [f"- `{cls}` x{n}" for cls, n in mixin_targets.most_common(40)] or ["- (none)"]
    L += ["", "## Where each surface appears", ""]
    for name, files in hits.items():
        L.append(f"### {name} - {len(files)} files")
        for rel, loc in sorted(files, key=lambda x: -x[1])[:30]:
            L.append(f"- `{rel}` ({loc} lines)")
        if len(files) > 30:
            L.append(f"- ... and {len(files) - 30} more")
        L.append("")
    out.parent.mkdir(parents=True, exist_ok=True)
    out.write_text("\n".join(L) + "\n", encoding="utf-8")
    print(f"Wrote {out}  ({len(java)} files, {total_loc:,} lines, {len(mixin_files)} mixin classes)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
