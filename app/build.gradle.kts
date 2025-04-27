plugins {
    id("com.android.application")

    // id("com.google.gms.google-services") // Uncomment if you are using Firebase
}

android {
    namespace = "com.example.simplenotes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.simplenotes"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

    }
    afterEvaluate {
        tasks.withType(JavaCompile::class) {
            options.compilerArgs.addAll(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
        }
    }
    ndkVersion = "25.1.8937393"
}

dependencies {
    // Firebase (Uncomment the following if you intend to use Firebase)
    // implementation platform('com.google.firebase:firebase-bom:33.12.0')
    // implementation 'com.google.firebase:firebase-analytics'

    // AppCompat
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // UI and Layout
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.fragment:fragment:1.8.6") // Using -ktx for Kotlin extensions

    // Room Persistence Library
    implementation("androidx.room:room-runtime:2.7.1")
    implementation("androidx.core:core-splashscreen:1.2.0-beta02")
    implementation("androidx.work:work-runtime:2.10.1")
    annotationProcessor("androidx.room:room-compiler:2.7.1")
    implementation("androidx.lifecycle:lifecycle-livedata:2.9.0-rc01")
    // Using -ktx for Kotlin extensions
//    kapt("androidx.room:room-compiler:2.7.0") // Use kapt for Kotlin annotation processing

    // Gson
    implementation("com.google.code.gson:gson:2.13.0")

    // Lottie Animation
    implementation("com.airbnb.android:lottie:6.6.0")

    // Authentication (Uncomment if you need Google Sign-In)
    // implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment:2.8.9") // Using -ktx for Kotlin extensions
    implementation("androidx.navigation:navigation-ui:2.8.9") // Using -ktx for Kotlin extensions

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}