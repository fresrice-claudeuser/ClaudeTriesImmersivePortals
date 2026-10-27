#!/usr/bin/env bash
# Copies files from a source repo into THIS repo (same relative paths), then applies local overlays
# and mechanical codemods. It does not commit. Used by .github/workflows/import-wave.yml.
#
#   SRC_REPO=iPortalTeam/DimLib SRC_REF=1.21.3 \
#   PATHS="src/main/java/qouteall/dimlib src/main/resources/dimlib.mixins.json" \
#   DELETE="src/main/java/qouteall/dimlib/config/DimLibModmenuIntegration.java" \
#   bash tools/import_wave.sh
#
# Order: copy PATHS -> delete DELETE -> copy overlays/ on top -> tools/port_codemods.py -> tools/port_patches.py.
# overlays/ holds our hand-made fixes (same layout as the repo root); because they are re-applied on every
# import, re-importing upstream never wipes them out.
set -euo pipefail
cd "$(dirname "$0")/.."

: "${SRC_REPO:?set SRC_REPO (owner/name)}"
: "${SRC_REF:?set SRC_REF (branch or tag)}"
: "${PATHS:?set PATHS (space-separated)}"
DELETE="${DELETE:-}"
SRC_URL="${SRC_URL:-https://github.com/${SRC_REPO}.git}"

safe() {
  case "$1" in
    ""|/*|.github*|*..*) echo "refusing unsafe path: '$1'" >&2; exit 1 ;;
  esac
}

tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT
git clone --quiet --depth 1 --branch "$SRC_REF" "$SRC_URL" "$tmp/src"
sha="$(git -C "$tmp/src" rev-parse --short=10 HEAD)"
echo "Source: $SRC_REPO @ $SRC_REF ($sha)"

for p in $PATHS; do
  safe "$p"
  [ -e "$tmp/src/$p" ] || { echo "not found in source: $p" >&2; exit 1; }
  rm -rf "$p"
  mkdir -p "$(dirname "$p")"
  cp -R "$tmp/src/$p" "$p"
  echo "copied   $p"
done

for p in $DELETE; do
  safe "$p"
  rm -rf "$p"
  echo "deleted  $p"
done

if [ -d overlays ]; then
  cp -R overlays/. .
  echo "applied  overlays/"
fi

if [ -f tools/port_codemods.py ]; then
  python3 tools/port_codemods.py src
fi

if [ -f tools/port_patches.py ]; then
  python3 tools/port_patches.py
fi

if [ -n "${GITHUB_ENV:-}" ]; then echo "SRC_SHA=$sha" >> "$GITHUB_ENV"; fi
