plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "lk.businessmanagement.staffcore"
    compileSdk = 36

    defaultConfig {
        applicationId = "lk.businessmanagement.staffcore"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Real-time Blur Effect
    implementation("com.github.Dimezis:BlurView:version-2.0.3")
    // lottie Animations
    implementation("com.airbnb.android:lottie:6.1.0")
    // Circular Image Views
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Material Design
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.github.yalantis:ucrop:2.2.8")


}