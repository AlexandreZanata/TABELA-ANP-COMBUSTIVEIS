plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}

tasks.register<Exec>("scanSecrets") {
    group = "verification"
    description = "Scans tracked files for accidental secrets (Phase 9.4.2)"
    commandLine("bash", "scripts/scan-secrets.sh")
    isIgnoreExitValue = false
}

tasks.register("securityCheck") {
    group = "verification"
    description = "Runs security verification tasks (Phase 9.4)"
    dependsOn(
        "scanSecrets",
        ":app:testDebugUnitTest",
        ":data:testDebugUnitTest",
    )
}

tasks.register<Exec>("validatePlayStoreListing") {
    group = "verification"
    description = "Validates Play Store listing draft limits and disclaimers (Phase 10.4)"
    commandLine("bash", "scripts/validate-play-store-listing.sh")
    isIgnoreExitValue = false
}

tasks.register<Exec>("validateReleaseBuild") {
    group = "verification"
    description = "Builds and validates signed release APK/AAB (Phase 10.5)"
    commandLine("bash", "scripts/validate-release-build.sh")
    isIgnoreExitValue = false
}

tasks.register<Exec>("validateGithubReleaseNotes") {
    group = "verification"
    description = "Validates GitHub Release v1.0.0 draft notes and disclaimers (Phase 10.6)"
    commandLine("bash", "scripts/validate-github-release-notes.sh")
    isIgnoreExitValue = false
}
