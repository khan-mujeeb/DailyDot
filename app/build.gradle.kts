plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "2.0.0-1.0.21"
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.dailydot"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.dailydot"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // splash screen
    implementation("androidx.core:core-splashscreen:1.2.0")

    // glide library
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.15.1")


    // work manager
    implementation ("androidx.work:work-runtime-ktx:2.11.0")


    // onboarding library
    implementation ("com.github.ErrorxCode:ModernOnboarding:v1.5")


    // calender library
    implementation("com.kizitonwose.calendar:view:2.6.0")
    implementation("com.kizitonwose.calendar:core:2.6.0")

    // firebase authentication
    implementation("com.google.firebase:firebase-auth:24.0.1")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Room database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version") // Add this line
    ksp("androidx.room:room-compiler:$room_version")

    // viewmodel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")

    //gson
    implementation("com.google.code.gson:gson:2.10.1")



    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}