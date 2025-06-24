// O código para ler o local.properties que você já tinha está correto e deve ficar no topo
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
val apiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""


plugins {
    alias(libs.plugins.android.application)
    // Se você estiver usando Kotlin no seu projeto, adicione o plugin do Kotlin aqui
    // id("org.jetbrains.kotlin.android") version "1.9.23" apply false
}

android {
    namespace = "com.example.receitahub"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.receitahub"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Esta linha está correta e adiciona a chave de API
        buildConfigField("String", "GEMINI_API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // Recomendo usar 1.8 para maior compatibilidade, mas 11 também funciona
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    // Se você usa Kotlin, adicione as opções de compilação do Kotlin aqui
    // kotlinOptions {
    //     jvmTarget = "1.8"
    // }

    buildFeatures {
        // Habilita a geração da classe BuildConfig
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Gemini e dependência de Coroutines (apenas uma vez)
    implementation("com.google.ai.client.generativeai:generativeai:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.8.0")

    // Room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}