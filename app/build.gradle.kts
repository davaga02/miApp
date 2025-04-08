plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.daniela.miapp"

    compileSdk = 34
    defaultConfig {
        applicationId = "com.daniela.miapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation (platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.android.gms:play-services-auth")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}