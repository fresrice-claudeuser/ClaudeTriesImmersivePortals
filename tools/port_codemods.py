#!/usr/bin/env python3
"""
Deterministic renames applied to every .java file under a directory after each import.

    python tools/port_codemods.py src

Rules must be idempotent. Add a rule only once a real build has shown it is needed, and note which
Minecraft version introduced the change, so the list doubles as a changelog of API moves.
"""
import pathlib
import re
import sys

RULES = [
    # (introduced in, description, regex, replacement)
    ("1.21.11", "ResourceLocation was renamed Identifier (fully-qualified name first, then the simple name)",
     r"\bnet\.minecraft\.resources\.ResourceLocation\b", "net.minecraft.resources.Identifier"),
    ("1.21.11", "ResourceLocation was renamed Identifier",
     r"\bResourceLocation\b", "Identifier"),
    ("1.21.11", "ResourceKey.location() was renamed identifier() (verified with `inspect`; every .location() in DimLib was a ResourceKey)",
     r"\.location\(\)", ".identifier()"),
    ("1.21.5", "CompoundTag.getAllKeys() was replaced by keySet() (verified with `inspect`)",
     r"\.getAllKeys\(\)", ".keySet()"),
]


def main() -> int:
    root = pathlib.Path(sys.argv[1] if len(sys.argv) > 1 else "src")
    changed = {}
    for path in sorted(root.rglob("*.java")):
        text = path.read_text(encoding="utf-8")
        new = text
        for _ver, desc, rx, repl in RULES:
            new = re.sub(rx, repl, new)
        if new != text:
            path.write_text(new, encoding="utf-8")
            changed[str(path)] = changed.get(str(path), 0) + 1
    print(f"codemods: {len(changed)} file(s) changed under {root}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
