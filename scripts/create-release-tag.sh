#!/usr/bin/env bash
# Phase 10.7 — runs Gate 10 checks and creates annotated tag v1.0.0 locally.
# Does not push to remote; maintainer publishes GitHub Release separately.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

TAG="v1.0.0"
RELEASE_NOTES="$ROOT/docs/releases/v1.0.0.md"

bash "$ROOT/scripts/validate-release-tag.sh"
bash "$ROOT/scripts/validate-release-build.sh"

TAG_MESSAGE="$(awk -v start='## Release title' '
    $0 ~ start { capture = 1; next }
    capture && /^```$/ { if (!seen) { seen = 1; next }; capture = 0; next }
    capture && seen { print; exit }
' "$RELEASE_NOTES" | sed '/^[[:space:]]*$/d')"

if [[ -z "$TAG_MESSAGE" ]]; then
    TAG_MESSAGE="ANP Fuel Prices v1.0.0 — MVP"
fi

git tag -a "$TAG" -m "$(cat <<EOF
$TAG_MESSAGE

First public MVP release (UC-001…UC-008). Sideload via GitHub Releases.
Fuel price data © ANP. This app is not affiliated with ANP.

Refs: Phase 10.7, BR-009
EOF
)"

echo "Created annotated tag $TAG at $(git rev-parse --short HEAD)"
echo "Verify: git show $TAG"
echo "Publish when ready: git push origin main && git push origin $TAG"
