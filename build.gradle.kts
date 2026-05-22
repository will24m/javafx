plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.runtime") version "1.13.1"
}

group = "com.jfxtutor"
version = "0.1.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

sourceSets.configureEach {
    java.exclude(
        "**/*William*.java",
        "**/*MacBook*.java",
        "**/*conflict*.java",
        "**/*Conflict*.java"
    )
}

javafx {
    version = "21.0.5"
    // Snippets teach beyond controls: ImageView uses graphics, WebView uses web,
    // and MediaPlayer lessons need media available on the runtime module path.
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.media")
}

application {
    mainClass.set("com.jfxtutor.app.JavaFxTutorApp")
}

// Make ./gradlew run feel less opaque. Gradle prints task names, while the app
// prints its own boot timeline once JavaFX starts; these lines bridge the gap.
tasks.named<JavaExec>("run") {
    doFirst {
        println("[JavaFX Tutor] Gradle run task is launching ${application.mainClass.get()}.")
        println("[JavaFX Tutor] Next you should see app startup, curriculum loading, and snippet compile events.")
    }
}

dependencies {
    implementation("org.fxmisc.richtext:richtextfx:0.11.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2")
    implementation("org.commonmark:commonmark:0.22.0")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.testfx:testfx-core:4.0.18")
    testImplementation("org.testfx:testfx-junit5:4.0.18")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// Convenience: copy resolved runtime jars into build/dependencies so the VSCode
// Java extension can pick them up via java.project.referencedLibraries when it
// hasn't fully imported the Gradle model yet.
tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath)
    into(layout.buildDirectory.dir("dependencies"))
}

// ── jpackage installer scaffold ──────────────────────────────────────────────
// Run: ./gradlew jpackage
// Produces a platform-native installer under build/jpackage/.
// Requires: jdk.jpackage module (bundled with JDK 16+).
//
// Per-platform outputs:
//   macOS  → JavaFX Tutor-<version>.dmg  (requires Xcode CLI tools)
//   Windows → JavaFX Tutor-<version>.msi  (requires WiX Toolset 3.x)
//   Linux  → javafx-tutor-<version>.deb   (requires dpkg-deb)
//
// To enable code signing on macOS add:
//   signingOptions { mac.signBundle.set(true); mac.signingKeyUserName.set("Developer ID ...") }
runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    modules.set(listOf(
        "java.base", "java.desktop", "java.logging", "java.management",
        "java.naming", "java.net.http", "java.prefs", "java.sql",
        "java.xml", "jdk.unsupported",
        "javafx.base", "javafx.controls", "javafx.fxml",
        "javafx.graphics", "javafx.media", "javafx.web"
    ))
    jpackage {
        appVersion = project.version.toString()
        imageName  = "JavaFX Tutor"
        installerName = "JavaFX Tutor"
        // Icon paths: place your icons at src/main/resources/icons/
        //   app.icns  → macOS
        //   app.ico   → Windows
        //   app.png   → Linux (512×512 recommended)
        val iconsDir = project.file("src/main/resources/icons")
        if (iconsDir.exists()) {
            when {
                org.gradle.internal.os.OperatingSystem.current().isMacOsX  ->
                    imageOptions.addAll(listOf("--icon", iconsDir.resolve("app.icns").absolutePath))
                org.gradle.internal.os.OperatingSystem.current().isWindows ->
                    imageOptions.addAll(listOf("--icon", iconsDir.resolve("app.ico").absolutePath))
                else ->
                    imageOptions.addAll(listOf("--icon", iconsDir.resolve("app.png").absolutePath))
            }
        }
        installerOptions.addAll(listOf(
            "--vendor", "jfxtutor",
            "--description", "Interactive JavaFX learning environment"
        ))
    }
}
