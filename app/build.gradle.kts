plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.food_order_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.food_order_app"
        minSdk = 29
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
        dataBinding = true
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.android.car.ui:car-ui-lib:2.6.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // gson
    implementation("com.google.code.gson:gson:2.10.1")

    // lottie animation
    implementation("com.airbnb.android:lottie:6.3.0")

    // java mail
    implementation("com.sun.mail:android-mail:1.6.2")
    implementation("com.sun.mail:android-activation:1.6.2")

    // Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    // responsive size
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    //swipe refresh layout
    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // circular image view
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.browser:browser:1.8.0")

    implementation("com.google.android.material:material:1.11.0")
    // flex box
    implementation("com.google.android.flexbox:flexbox:3.0.0")

    // implementation("com.ismaeldivita.chipnavigation:chip-navigation-bar:1.3.4")

    // otp view
    implementation("io.github.chaosleung:pinview:1.4.4")

    // country code picker
    implementation("com.hbb20:ccp:2.7.3")

    // google play location access library
    implementation("com.google.android.gms:play-services-location:21.2.0")

    // razor pay implementation
    implementation("com.razorpay:checkout:1.6.19")

}