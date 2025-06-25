// No seu arquivo build.gradle.kts (Project: ReceitaHub)

plugins {
    alias(libs.plugins.android.application) apply false

    // ADICIONADO: Define a versão do plugin Kotlin para todo o projeto
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false

    // ADICIONADO: Define a versão do plugin KSP para todo o projeto
    id("com.google.devtools.ksp") version "1.9.23-1.0.19" apply false
}