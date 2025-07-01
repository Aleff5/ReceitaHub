plugins {
    alias(libs.plugins.android.application) apply false

    id("org.jetbrains.kotlin.android") version "1.9.23" apply false

    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
}