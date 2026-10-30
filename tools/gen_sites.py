#!/usr/bin/env python3
"""
Turns a javac build.log (first compile pass) into tools/port_sites.json: exact, position-driven fixes for the
API changes whose replacement was VERIFIED with `inspect`. Not run in CI; run it by hand after a build:

    python3 tools/gen_sites.py <repo-root> <build.log> tools/port_sites.json [--skip dir-substring ...]

The repo tree must be the one that produced the log (imports + codemods applied, port_sites NOT applied).
Errors it does not understand are left alone and printed so they can be handled by hand.
"""
import difflib
import json
import re
import sys
import pathlib
import collections

ROOT = pathlib.Path(sys.argv[1])
LOG = sys.argv[2]
OUT = sys.argv[3]
SKIP = ["/core/render/", "/core/compat/"]
if "--skip" in sys.argv:
    SKIP = sys.argv[sys.argv.index("--skip") + 1:]
BASE = ROOT / "src/main/java"
OVERLAYS = {str(p.relative_to(ROOT / "overlays/src/main/java")) for p in (ROOT / "overlays/src/main/java").rglob("*.java")} \
    if (ROOT / "overlays/src/main/java").exists() else set()

OPT = {  # method -> (new name, default literal or None)
    "getInt": ("getIntOr", "0"), "getDouble": ("getDoubleOr", "0.0"), "getLong": ("getLongOr", "0L"),
    "getBoolean": ("getBooleanOr", "false"), "getString": ("getStringOr", '""'), "getFloat": ("getFloatOr", "0.0f"),
    "getShort": ("getShortOr", "(short) 0"), "getByte": ("getByteOr", "(byte) 0"),
    "getCompound": ("getCompoundOrEmpty", None), "getList": ("getListOrEmpty", None),
}
IDENT = re.compile(r"[A-Za-z0-9_$]")


def parse_log():
    L = open(LOG, errors="replace").read().split("\n")
    end = next((i for i, l in enumerate(L) if re.match(r"^\s*[\d,]+ errors?\s*$", l)), len(L))
    out = []
    for i, l in enumerate(L[:end]):
        m = re.match(r"^.*?/src/main/java/(.+?\.java):(\d+): error: (.*)$", l)
        if not m:
            continue
        sym = loc = ""
        for j in range(i + 1, min(i + 8, end)):
            s = L[j].strip()
            if s.startswith("symbol:"):
                sym = s[7:].strip()
            if s.startswith("location:"):
                loc = s[9:].strip()
            if j > i and re.match(r".*: error:", L[j]):
                break
        caret = L[i + 2] if i + 2 < len(L) else ""
        col = caret.index("^") if "^" in caret else -1
        out.append(dict(f=m.group(1), ln=int(m.group(2)), msg=m.group(3), sym=sym, loc=loc, col=col))
    return out


def match_paren(t, o):
    d, i, n = 0, o, len(t)
    while i < n:
        c = t[i]
        if c == '"':
            i += 1
            while t[i] != '"':
                i += 2 if t[i] == "\\" else 1
        elif c == "'":
            i += 1
            while t[i] != "'":
                i += 2 if t[i] == "\\" else 1
        elif c in "([{":
            d += 1
        elif c in ")]}":
            d -= 1
            if d == 0:
                return i
        i += 1
    return -1


def top_commas(t, o, c):
    out, d, i = [], 0, o
    while i < c:
        ch = t[i]
        if ch == '"':
            i += 1
            while t[i] != '"':
                i += 2 if t[i] == "\\" else 1
        elif ch in "([{":
            d += 1
        elif ch in ")]}":
            d -= 1
        elif ch == "," and d == 1:
            out.append(i)
        i += 1
    return out


def recv_start(t, dot):
    i = dot
    while i > 0:
        c = t[i - 1]
        if c == ")":
            d = 0
            j = i - 1
            while j >= 0:
                if t[j] == ")":
                    d += 1
                elif t[j] == "(":
                    d -= 1
                    if d == 0:
                        break
                j -= 1
            i = j
        elif IDENT.match(c) or c == ".":
            i -= 1
        else:
            break
    return i


def main():
    errs = parse_log()
    by_file = collections.defaultdict(list)
    for e in errs:
        by_file[e["f"]].append(e)
    result, unhandled, stats = {}, collections.Counter(), collections.Counter()
    for rel, es in sorted(by_file.items()):
        if any(s in "/" + rel for s in SKIP):
            stats["skipped(render/compat)"] += len(es)
            continue
        if rel in OVERLAYS:
            stats["skipped(overlay)"] += len(es)
            continue
        text = (BASE / rel).read_text(encoding="utf-8")
        starts = [0]
        for m in re.finditer("\n", text):
            starts.append(m.end())
        edits, imports = [], set()   # edits: (start, end, replacement)
        for e in es:
            if e["col"] < 0:
                unhandled[e["msg"][:60]] += 1
                continue
            pos = starts[e["ln"] - 1] + e["col"]
            msg, sym, loc = e["msg"], e["sym"], e["loc"]
            done = None
            if msg == "cannot find symbol" and sym.startswith("method getNormal(") and "Direction" in loc:
                if text.startswith(".getNormal()", pos):
                    done = (pos + 1, pos + 1 + len("getNormal"), "getUnitVec3i"); stats["getNormal"] += 1
            elif msg == "cannot find symbol" and sym.startswith("method getAsString(") and "StringTag" in loc:
                if text.startswith(".getAsString()", pos):
                    done = (pos + 1, pos + 1 + len("getAsString"), "value"); stats["getAsString"] += 1
            elif msg == "server has private access in ServerPlayer":
                if text.startswith(".server", pos) and not IDENT.match(text[pos + 7]):
                    done = (pos, pos + 7, ".level().getServer()"); stats["ServerPlayer.server"] += 1
            elif msg == "cannot find symbol" and sym.startswith("method fromDelta(") and "Direction" in loc:
                if text.startswith(".fromDelta(", pos):
                    rs = recv_start(text, pos)
                    if text[rs:pos] == "Direction":
                        done = (rs, pos + len(".fromDelta"), "McCompat.fromDelta"); imports.add("dev.claudeportals.McCompat"); stats["fromDelta"] += 1
            elif msg == "cannot find symbol" and sym.startswith("method getProfiler("):
                if text.startswith(".getProfiler()", pos):
                    rs = recv_start(text, pos)
                    done = (rs, pos + len(".getProfiler()"), "Profiler.get()")
                elif text.startswith("getProfiler()", pos):
                    done = (pos, pos + len("getProfiler()"), "Profiler.get()")
                if done:
                    imports.add("net.minecraft.util.profiling.Profiler"); stats["getProfiler"] += 1
            elif msg == "cannot find symbol" and sym.startswith("method getServer()"):
                if "of type ServerPlayer" in loc and text.startswith(".getServer()", pos):
                    done = (pos, pos + len(".getServer()"), ".level().getServer()"); stats["getServer(ServerPlayer)"] += 1
                elif text.startswith(".getServer()", pos):
                    rs = recv_start(text, pos)
                    done = (rs, pos + len(".getServer()"), f"McCompat.getServer({text[rs:pos]})")
                    imports.add("dev.claudeportals.McCompat"); stats["getServer(recv)"] += 1
                elif text.startswith("getServer()", pos) and not text[pos - 1] == ".":
                    done = (pos, pos + len("getServer()"), "McCompat.getServer(this)")
                    imports.add("dev.claudeportals.McCompat"); stats["getServer(this)"] += 1
            elif msg.startswith("method getList in class CompoundTag cannot be applied"):
                m = re.match(r"\.getList\(", text[pos:])
                if m:
                    o = pos + m.end() - 1
                    c = match_paren(text, o)
                    cm = top_commas(text, o, c)
                    if len(cm) == 1:
                        done = (pos + 1, c + 1, "getListOrEmpty(" + text[o + 1:cm[0]].strip() + ")"); stats["getList(2)"] += 1
            else:
                m = re.match(r"incompatible types: Optional<(\w+)> cannot be converted to", msg)
                if m:
                    # javac puts the caret on the '(' of a method call in "incompatible types" errors
                    o = pos if text[pos] == "(" else -1
                    ns = o
                    while o > 0 and ns > 0 and IDENT.match(text[ns - 1]):
                        ns -= 1
                    name = text[ns:o] if o > 0 else ""
                    if o > 0 and name in OPT:
                        new, dflt = OPT[name]
                        c = match_paren(text, o)
                        inner = text[o + 1:c].rstrip()
                        if name == "getList" and len(top_commas(text, o, c)) == 1:
                            pass
                        else:
                            body = inner + (", " + dflt if dflt else "")
                            done = (ns, c + 1, f"{new}({body})"); stats["Optional->" + new] += 1
            if done:
                edits.append(done)
            else:
                unhandled[(msg[:50] + " | " + sym[:40]) if msg == "cannot find symbol" else msg[:70]] += 1
        # resolve overlaps: keep the earliest, drop edits that overlap a kept one (reported)
        edits.sort()
        kept = []
        for s, t_, r in edits:
            if kept and s < kept[-1][1]:
                stats["overlap-deferred"] += 1
                continue
            if kept and (s, t_, r) == kept[-1]:
                continue
            kept.append((s, t_, r))
        if not kept:
            continue
        new = text
        for s, t_, r in sorted(kept, reverse=True):
            new = new[:s] + r + new[t_:]
        a, b = text.split("\n"), new.split("\n")
        hunks = []
        for tag, i1, i2, j1, j2 in difflib.SequenceMatcher(None, a, b, autojunk=False).get_opcodes():
            if tag != "equal":
                hunks.append({"line": i1 + 1, "old": a[i1:i2], "new": b[j1:j2]})
        result[rel] = {"hunks": hunks, "imports": sorted(i for i in imports if f"import {i};" not in text)}
    json.dump({"generated_from": pathlib.Path(LOG).name, "files": result}, open(OUT, "w"), indent=1)
    print("files:", len(result), "hunks:", sum(len(v["hunks"]) for v in result.values()))
    print("handled:", dict(stats))
    print("UNHANDLED:", sum(unhandled.values()))
    for k, v in unhandled.most_common(25):
        print("  ", v, k)


main()
