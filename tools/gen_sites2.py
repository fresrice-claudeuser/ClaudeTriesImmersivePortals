#!/usr/bin/env python3
"""Sandbox-only. Turns the errors of a build.log into tools/port_sites2.json (same hunk format as port_sites.json)
for categories whose replacement was VERIFIED with `inspect` (see comments per rule). The repo tree must be the
state that produced the log (import + codemods + sites + patches, i.e. what import-wave leaves, BEFORE wave2).
    python3 tools/gen_sites2.py <repo> <build.log> tools/port_sites2.json
"""
import difflib, json, re, sys, pathlib, collections

ROOT = pathlib.Path(sys.argv[1]); LOG = sys.argv[2]; OUT = sys.argv[3]
BASE = ROOT / "src/main/java"
SKIP = ["/core/render/", "/core/compat/"]


def parse_log():
    L = open(LOG, errors="replace").read().split("\n")
    end = next((i for i, l in enumerate(L) if re.match(r"^\s*[\d,]+ errors?\s*$", l)), len(L))
    out = []
    for i, l in enumerate(L[:end]):
        m = re.match(r"^.*?/src/main/java/(.+?\.java):(\d+): error: (.*)$", l)
        if not m:
            continue
        sym = ""
        for j in range(i + 1, min(i + 8, end)):
            s = L[j].strip()
            if s.startswith("symbol:"):
                sym = s[7:].strip()
            if j > i and re.match(r".*: error:", L[j]):
                break
        out.append(dict(f=m.group(1), ln=int(m.group(2)), msg=m.group(3), sym=sym))
    return out


def match_paren(t, o):
    d, i, n = 0, o, len(t)
    while i < n:
        c = t[i]
        if c == '"':
            i += 1
            while t[i] != '"':
                i += 2 if t[i] == "\\" else 1
        elif c in "([{":
            d += 1
        elif c in ")]}":
            d -= 1
            if d == 0:
                return i
        i += 1
    return -1


OPT = {"getInt": ("getIntOr", "0"), "getDouble": ("getDoubleOr", "0.0"), "getLong": ("getLongOr", "0L"),
       "getFloat": ("getFloatOr", "0.0f"), "getString": ("getStringOr", '""'), "getBoolean": ("getBooleanOr", "false")}
PERM = {"0": "LEVEL_ALL", "1": "LEVEL_MODERATORS", "2": "LEVEL_GAMEMASTERS", "3": "LEVEL_ADMINS", "4": "LEVEL_OWNERS"}


def line_span(text, ln):
    lines = text.split("\n")
    a = sum(len(x) + 1 for x in lines[:ln - 1])
    return a, a + len(lines[ln - 1])


def fix_opt(text, ln):
    a, b = line_span(text, ln)
    seg = text[a:b]
    res = seg; changed = False
    for m in sorted(re.finditer(r"\.(getInt|getDouble|getLong|getFloat|getString|getBoolean)\(", seg), key=lambda m: -m.start()):
        name = m.group(1)
        # skip when this receiver is already an *Or call or not an NBT-looking receiver: caller checks the error type
        p = a + m.end() - 1
        q = match_paren(text, p)
        if q < 0:
            continue
        new, dflt = OPT[name]
        # operate on the whole text with absolute positions
        text = text[:a + m.start()] + "." + new + text[a + m.end() - 1:q] + ", " + dflt + text[q:]
        # lengths changed after the point; re-run needs fresh spans, so do one per pass
        return text, True
    return text, False


def main():
    errs = parse_log()
    by = collections.defaultdict(list)
    for e in errs:
        if any(s in "/" + e["f"] for s in SKIP) or "/core/render/" in e["f"]:
            continue
        by[e["f"]].append(e)
    files = {}
    for rel, es in sorted(by.items()):
        p = BASE / rel
        if not p.exists():
            continue
        orig = p.read_text(encoding="utf-8")
        text = orig
        imports = set()
        # process bottom-up by line so earlier edits do not shift later line numbers (edits stay on their own lines, except sendSystemMessage)
        for e in sorted({(e["ln"], e["msg"], e["sym"]) for e in es}, key=lambda x: -x[0]):
            ln, msg, sym = e
            a, b = line_span(text, ln)
            seg = text[a:b]
            new = None
            if sym == "method getPosition()":
                new = seg.replace(".getPosition()", ".position()")          # Camera.position() verified
            elif msg.startswith("no suitable method found for getNearest(double,double,double)"):
                new = seg.replace("getNearest(", "getApproximateNearest(")   # Direction.getApproximateNearest(double,double,double) verified
            elif "isClientSide has private access" in msg:
                new = re.sub(r"\.isClientSide\b(?!\()", ".isClientSide()", seg)  # Level.isClientSide() verified
            elif re.search(r"incompatible types: Optional<(Double|Integer|Float|Long|String)>", msg) or "bad operand types" in msg or "no suitable method found for dimIdToKey(Optional<String>)" in msg:
                t2, ok = fix_opt(text, ln)                                   # *Or getters verified (handoff + inspect)
                if ok:
                    text = t2
                    continue
            elif sym.startswith("method hasUUID(") or sym.startswith("method getUUID(") or sym.startswith("method putUUID("):
                new = re.sub(r"(\w+)\.(hasUUID|getUUID|putUUID)\(", r"McCompat.\2(\1, ", seg)  # McCompat stores two longs
                if new != seg:
                    imports.add("dev.claudeportals.McCompat")
            elif "getMinBuildHeight()" in sym:
                new = seg.replace(".getMinBuildHeight()", ".getMinY()")      # LevelHeightAccessor.getMinY verified
            elif "getMaxBuildHeight()" in sym:
                new = re.sub(r"(\w+)\.getMaxBuildHeight\(\)", r"(\1.getMaxY() + 1)", seg)  # getMaxY is inclusive
            elif sym == "method getMinSection()":
                new = seg.replace(".getMinSection()", ".getMinSectionY()")     # verified
            elif sym == "method getMaxSection()":
                new = re.sub(r"(\w+)\.getMaxSection\(\)", r"(\1.getMaxSectionY() + 1)", seg)
            elif sym.startswith("method hasPermission(int)") and ".requires(" in seg:
                m = re.search(r"\.requires\((\w+) -> \1\.hasPermission\((\d)\)\)", seg)
                if m:   # Commands.hasPermission(PermissionCheck) + Commands.LEVEL_* verified
                    new = seg[:m.start()] + ".requires(Commands.hasPermission(Commands." + PERM[m.group(2)] + "))" + seg[m.end():]
                    imports.add("net.minecraft.commands.Commands")
            elif sym in ("method getName()", "method getId()") and re.search(r"rofile|getCurrentVersion", seg):
                # GameProfile is a record: id()/name(); WorldVersion.name() (both verified with inspect)
                new = seg.replace(".getName()", ".name()") if sym == "method getName()" else seg.replace(".getId()", ".id()")
            elif sym in ("method getAsByte()", "method getAsNumber()", "method getAsFloat()", "method getAsDouble()"):
                # NumericTag.byteValue()/box()/floatValue()/doubleValue() verified with inspect
                new = (seg.replace(".getAsByte()", ".byteValue()").replace(".getAsNumber()", ".box()")
                       .replace(".getAsFloat()", ".floatValue()").replace(".getAsDouble()", ".doubleValue()"))
            elif sym.startswith("method sendSystemMessage("):
                i = text.find(".sendSystemMessage(", a)
                if 0 <= i < b:
                    q = match_paren(text, i + len(".sendSystemMessage(") - 1)
                    text = text[:i] + ".displayClientMessage(" + text[i + len(".sendSystemMessage("):q] + ", false" + text[q:]  # Player.displayClientMessage(Component,boolean) verified
                    continue
            if new is not None and new != seg:
                text = text[:a] + new + text[b:]
        if text == orig:
            continue
        ol, nl = orig.split("\n"), text.split("\n")
        hunks = []
        for tag, i1, i2, j1, j2 in difflib.SequenceMatcher(None, ol, nl, autojunk=False).get_opcodes():
            if tag != "equal":
                hunks.append(dict(line=i1 + 1, old=ol[i1:i2], new=nl[j1:j2]))
        files[rel] = dict(hunks=hunks, imports=sorted(imports))
    json.dump(dict(files=files), open(OUT, "w"), indent=1)
    print(f"{len(files)} file(s), {sum(len(v['hunks']) for v in files.values())} hunk(s)")


main()
