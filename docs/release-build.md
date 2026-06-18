# Release build — signed APK and AAB

Phase **10.5** deliverable: reproducible signed release artifacts for sideloading or Play Store upload.

## Prerequisites

- JDK 17
- Android SDK (see `local.properties` / `ANDROID_HOME`)
- Release keystore (create once, store securely — **never commit**)

## 1. Create a release keystore (one-time)

```bash
./scripts/create-release-keystore.sh
```

This writes `.local/anpfuel-release.keystore` (gitignored via `*.keystore`).

For production, use your own strong passwords and back up the keystore offline. Losing the keystore prevents updating the same Play Store listing.

## 2. Configure signing

Copy the example file and edit passwords:

```bash
cp signing.properties.example signing.properties
```

Or export environment variables (CI / local shell):

| Variable | Purpose |
|----------|---------|
| `ANPFUEL_KEYSTORE_PATH` | Path to `.keystore` or `.jks` file |
| `ANPFUEL_KEYSTORE_PASSWORD` | Keystore password |
| `ANPFUEL_KEY_ALIAS` | Key alias inside the keystore |
| `ANPFUEL_KEY_PASSWORD` | Key password |

`signing.properties` takes precedence when both file and env vars are set.

## 3. Build signed artifacts

```bash
./gradlew assembleRelease bundleRelease
```

Outputs:

| Artifact | Path |
|----------|------|
| Signed APK | `app/build/outputs/apk/release/app-release.apk` |
| Signed AAB | `app/build/outputs/bundle/release/app-release.aab` |

Release builds enable R8 minification and resource shrinking (`app/proguard-rules.pro`).

## 4. Validate release build

Runs build + signature verification + 15 MB APK size gate:

```bash
./gradlew validateReleaseBuild
```

Or run the shell script directly:

```bash
./scripts/validate-release-build.sh
```

Validation checks:

- Signing config present (`signing.properties` or env vars)
- APK and AAB exist after build
- APK signature verified with `apksigner`
- AAB signature verified with `jarsigner`
- Uncompressed APK size ≤ 15 MB (Phase 9.2.2)

## CI note

GitHub Actions CI runs unit tests only. Signed release builds are **maintainer-local** or run in a protected workflow with secrets — never commit keystores or passwords.

## Related docs

- [play-store-listing.md](play-store-listing.md) — store copy draft (Phase 10.4)
- [privacy-policy.md](privacy-policy.md) — required for Play Store
- [CONTRIBUTING.md](../CONTRIBUTING.md) — secrets policy
