plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")

    // firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.a503.onjeong"
    compileSdk = 34
    // id는 식별자
    defaultConfig {
        applicationId = "com.a503.onjeong"
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

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Lombok
    // https://mvnrepository.com/artifact/org.projectlombok/lombok
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

// RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.2.1")
// Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.squareup.retrofit2:converter-gson:2.5.0")
// Gson
    implementation("com.google.code.gson:gson:2.8.5")
// jpa
    implementation("javax.persistence:javax.persistence-api:2.2")
// 사진 불러오기 위한 glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    // okhttp3
//    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // WebRTC
    implementation("com.github.webrtc-sdk:android:104.+")
    // Websocket
    implementation("com.neovisionaries:nv-websocket-client:2.9")
    // butterknife
    implementation("com.jakewharton:butterknife:10.2.3")
    annotationProcessor("com.jakewharton:butterknife-compiler:10.2.3")
    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-messaging:23.4.0")

    implementation ("com.google.android.gms:play-services-location:21.0.1")

    // youtube
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:11.1.0")

    // JSoup
    implementation("org.jsoup:jsoup:1.15.3")
    //이미지 둥글게
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //cardView
    implementation("androidx.cardview:cardview:1.0.0")
}