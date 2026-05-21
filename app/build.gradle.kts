plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.applicationlogin"
    compileSdk = 36 // Correction mineure de syntaxe ici si besoin, sinon garde ton ancienne ligne

    defaultConfig {
        applicationId = "com.example.applicationlogin"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // NOUVEAU BLOC INDISPENSABLE POUR LES TESTS :
    testOptions {
        unitTests.isReturnDefaultValues = true // Empêche l'erreur "Method not mocked" pour l'API Android
        unitTests.all {
            // Autorise Mockito à manipuler les classes sous Java 21
            it.jvmArgs("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")

    testImplementation("junit:junit:4.13.2")
    // On garde uniquement Mockito Core en version 5+
    testImplementation("org.mockito:mockito-core:5.11.0")
}