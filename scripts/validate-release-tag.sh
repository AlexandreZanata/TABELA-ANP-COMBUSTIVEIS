#!/usr/bin/env bash
# Phase 10.7 — validates repository readiness for v1.0.0 release tag (Gate 10).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

TAG="v1.0.0"
GRADLE="$ROOT/app/build.gradle.kts"
LICENSE="$ROOT/LICENSE"
README="$ROOT/README.md"
RELEASE_NOTES="$ROOT/docs/releases/v1.0.0.md"

assert_file() {
    local path="$1"
    local label="$2"
    if [[ ! -f "$path" ]]; then
        echo "ERROR: missing $label at $path"
        exit 1
    fi
    echo "OK: $label present"
}

assert_file "$LICENSE" "LICENSE"
assert_file "$GRADLE" "app/build.gradle.kts"
assert_file "$README" "README.md"
assert_file "$RELEASE_NOTES" "release notes"

if [[ "$README" != *"LICENSE"* ]]; then
    echo "ERROR: README must reference LICENSE (Gate 10)"
    exit 1
fi
echo "OK: README references LICENSE"

VERSION_NAME="$(grep 'versionName' "$GRADLE" | head -n 1 | sed -E 's/.*"([^"]+)".*/\1/')"
VERSION_CODE="$(grep 'versionCode' "$GRADLE" | head -n 1 | sed -E 's/.*= ([0-9]+).*/\1/')"

if [[ "$VERSION_NAME" != "1.0.0" ]]; then
    echo "ERROR: expected versionName 1.0.0, found '$VERSION_NAME'"
    exit 1
fi
echo "OK: versionName is 1.0.0"

if [[ -z "$VERSION_CODE" || "$VERSION_CODE" -lt 1 ]]; then
    echo "ERROR: invalid versionCode '$VERSION_CODE'"
    exit 1
fi
echo "OK: versionCode is $VERSION_CODE"

bash "$ROOT/scripts/validate-github-release-notes.sh"
bash "$ROOT/scripts/validate-play-store-listing.sh"

if [[ -n "$(git status --porcelain)" ]]; then
    echo "ERROR: working tree is not clean — commit changes before tagging"
    git status --short
    exit 1
fi
echo "OK: working tree clean"

if git rev-parse "$TAG" >/dev/null 2>&1; then
    echo "ERROR: tag $TAG already exists at $(git rev-parse "$TAG")"
    exit 1
fi
echo "OK: tag $TAG not yet created"

echo "Release tag readiness validation passed."
