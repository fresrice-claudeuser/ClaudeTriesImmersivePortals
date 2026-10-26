#!/usr/bin/env bash
# Copies a pristine upstream Immersive Portals source tree into ./upstream/ as a READ-ONLY reference.
# Nothing in upstream/ is compiled. Code moves from upstream/ into src/ one wave at a time as it is
# ported (see docs/PORTING_PLAN.md), so `git diff` always shows exactly what the port changed.
#
#   ./tools/import_upstream.sh                      # default: iPortalTeam/ImmersivePortalsMod, branch 1.21
#   UPSTREAM_BRANCH=1.20.4 ./tools/import_upstream.sh
set -euo pipefail
cd "$(dirname "$0")/.."

URL="${UPSTREAM_URL:-https://github.com/iPortalTeam/ImmersivePortalsMod.git}"
BRANCH="${UPSTREAM_BRANCH:-1.21}"
DEST="upstream"

if [ -e "$DEST" ]; then
  echo "'$DEST' already exists. Delete it first if you want a fresh copy." >&2
  exit 1
fi

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

echo ">> Cloning $URL ($BRANCH)"
git clone --depth 1 --branch "$BRANCH" "$URL" "$tmp/up"
sha="$(git -C "$tmp/up" rev-parse HEAD)"

mkdir -p "$DEST"
cp -R "$tmp/up/src" "$DEST/src"
for f in build.gradle gradle.properties settings.gradle changelog.md README.md; do
  if [ -f "$tmp/up/$f" ]; then cp "$tmp/up/$f" "$DEST/$f"; fi
done
if [ -f "$tmp/up/LICENSE" ]; then
  cp "$tmp/up/LICENSE" "$DEST/LICENSE"
  if [ ! -f LICENSE ]; then cp "$tmp/up/LICENSE" LICENSE; echo ">> Copied upstream Apache-2.0 LICENSE to repo root."; fi
fi
echo "$URL @ $BRANCH $sha" > "$DEST/UPSTREAM_COMMIT"

if command -v python3 >/dev/null 2>&1; then
  python3 tools/inventory.py "$DEST/src" docs/upstream-inventory.md
else
  echo "python3 not found - skipped inventory (run tools/inventory.py later)."
fi

cat <<MSG

>> Imported upstream @ $sha into ./$DEST
Next:
  git add upstream LICENSE docs/upstream-inventory.md
  git commit -m "Import upstream Immersive Portals @ ${sha:0:10} (reference only)"
  git push
Then give Claude: docs/upstream-inventory.md and upstream/build.gradle (paste or upload).
MSG
