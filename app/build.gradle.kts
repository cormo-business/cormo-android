plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)


}

android {
    namespace = "com.cormo.neulbeot"
    compileSdk = 36

    sourceSets{
        named("main"){
            res.srcDirs(
                "src/main/res",
                "src/home/res",
                "src/challenge/res",
                "src/login/res",
                "src/signup/res",
                "src/activity/res",
                "src/new_home/res"
            )
        }
    }


    defaultConfig {
        applicationId = "com.cormo.neulbeot"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 런타임 퍼미션 체크 위해
        vectorDrawables { useSupportLibrary = true }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    // AndroidX & Material (UI)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.2")
// Retrofit + Moshi + OkHttp (네트워크)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    // Moshi (필요시 코드젠 켜도 됨)
    implementation("com.squareup.moshi:moshi:1.15.1")
    // Moshi Kotlin 어댑터 (여기에 KotlinJsonAdapterFactory 들어있음)
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")
// kapt "com.squareup.moshi:moshi-kotlin-codegen:1.15.1"
// Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // (선택) 이미지 로딩을 더 편하게 하고 싶다면 Coil
    // implementation "io.coil-kt:coil:2.6.0"

    // (선택) Konfetti (Flutter의 confetti 유사 효과가 필요하다면)
    // implementation "nl.dionsegijn:konfetti-xml:2.0.4"

    // 하단 탭
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // fragment 모델뷰 사용
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}