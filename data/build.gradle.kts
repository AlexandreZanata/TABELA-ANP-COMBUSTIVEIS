plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.anpfuel.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(project(":domain"))

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kxml2)
    testImplementation(libs.poi.ooxml)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

afterEvaluate {
    tasks.register<Test>("parserMemoryTest") {
        description = "Runs parser memory POC with a 64MB heap"
        group = "verification"

        val unitTest = tasks.named<Test>("testDebugUnitTest").get()
        testClassesDirs = unitTest.testClassesDirs
        classpath = unitTest.classpath

        useJUnitPlatform {
            includeTags("parser-memory")
        }

        maxHeapSize = "64m"
        jvmArgs("-XX:+HeapDumpOnOutOfMemoryError")
    }
}
