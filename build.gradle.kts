// In C:\Users\aleff\AndroidStudioProjects\ReceitaHub\build.gradle.kts

// Make sure the version is NOT hardcoded here.
// Instead, use the alias and set 'apply false'.
plugins {
    // This line applies the plugin to sub-projects, referencing the version from libs.versions.toml
    alias(libs.plugins.android.application) apply false
    // You might have other plugins here, like for Kotlin
    // alias(libs.plugins.kotlin.android) apply false
}