import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
val apiKey = localProperties.getProperty("GEMINI_API_KEY") ?: ""


plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Gemini e dependência de Coroutines
    implementation("com.google.ai.client.generativeai:generativeai:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.8.0")

    // Room
    implementation(libs.room.runtime)
    // ALTERADO: Usando o nome completo da dependência para evitar erros de referência
    implementation("androidx.room:room-ktx:2.6.1") // Adicionando a dependência KTX para LiveData
    ksp(libs.room.compiler)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// Configuração do Room Schema Location para KSP
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}