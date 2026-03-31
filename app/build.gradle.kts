plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.wukongstarter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.wukongstarter"
        minSdk = 24
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        jniLibs {
            pickFirsts += setOf(
                "lib/armeabi-v7a/libtvad.so",
                "lib/arm64-v8a/libtvad.so"
            )
        }
    }
}

dependencies {
    // Local libs
    compileOnly(files("libs/mini-outer-sdk-2.4.2.jar"))
    implementation(files("libs/mini-outer-sdk-lite.jar"))
    implementation(files("libs/speechFramework-oversea-release.aar"))
    implementation(files("libs/sal-speech-1.0.0.aar"))
    implementation(files("libs/tencent-vad-1.0.0.aar"))
    implementation(files("libs/impl-wakeup-dingdang-1.0.0.aar"))
    implementation(files("libs/wakeup-5.0.0.aar"))
    implementation(files("libs/weinalib-5.0.1.aar"))
    implementation(files("libs/Msc.jar"))
    implementation(files("libs/eventbus-3.1.1.jar"))
    implementation(files("libs/protobuf-java-3.4.0.jar"))
    implementation(files("libs/phonecall-sdk-release.aar"))
    implementation(files("libs/utillib-1.2.8.aar"))
    
    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    
    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
