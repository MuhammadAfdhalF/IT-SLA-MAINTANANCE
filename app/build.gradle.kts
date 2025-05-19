plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

}

android {
    namespace = "com.acuy.sla_maintenance"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.acuy.sla_maintenance"
        minSdk = 26 //28
        targetSdk = 33
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
 buildFeatures{
     viewBinding = true
 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("androidx.compose.ui:ui-text-android:1.6.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.github.bumptech.glide:glide:4.11.0")


//    toolbar
    implementation ("androidx.appcompat:appcompat:1.5.1")

    implementation ("com.google.android.material:material:1.2.1")

    implementation("com.android.volley:volley:1.2.1")
    implementation ("com.github.bumptech.glide:glide:4.11.0")

    implementation ("androidx.constraintlayout:constraintlayout:2.0.0-alpha4")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation ("androidx.appcompat:appcompat-resources:1.5.1")
    implementation ("androidx.lifecycle:lifecycle-process:2.5.1")

//Deploy
    implementation("com.google.code.gson:gson:2.10.1")

    implementation ("com.google.android.material:material:1.4.0")
    implementation ("androidx.appcompat:appcompat:1.3.0")


//    retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

//    json Converter
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

//    okhttp
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")

//    define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.11.0"))

//    define any required Okhttp artifacts without version
    implementation ("com.squareup.okhttp3:okhttp")
    implementation ("com.squareup.okhttp3:logging-interceptor")

//    load image
    implementation("com.github.bumptech.glide:glide:4.13.2")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

//    token
    implementation ("com.google.dagger:hilt-android:2.38.1")

    // grafik
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")


}