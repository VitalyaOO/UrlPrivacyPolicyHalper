plugins {
    id("com.android.library")
    id("com.google.dagger.hilt.android")
    id("com.onesignal.androidsdk.onesignal-gradle-plugin")
    id ("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

apply(from= "${rootProject.projectDir}/scripts/publish-module.gradle.kts")

android {

    namespace = "pl.idreams.urlprivacyhalper"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    hilt {
        enableAggregatingTask = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.8.0")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    //Lifecycle
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

    //Hilt
    implementation ("com.google.dagger:hilt-android:2.45")
    kapt ("com.google.dagger:hilt-compiler:2.45")

    // Facebook
    implementation ("com.facebook.android:facebook-android-sdk:16.0.1")

    // Tools
    implementation ("com.onesignal:OneSignal:4.8.5")
    implementation ("com.appsflyer:af-android-sdk:6.9.4")
    implementation("com.android.installreferrer:installreferrer:2.2")

    implementation ("com.karumi:dexter:6.2.3")
}