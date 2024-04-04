plugins {
   id("com.android.application")
   id("org.jetbrains.kotlin.android")
   id("com.google.devtools.ksp")
}

android {
   namespace = "cc.atomtech.planner"
   compileSdk = 34

   defaultConfig {
      applicationId = "cc.atomtech.planner"
      minSdk = 26
      targetSdk = 34
      versionCode = 9
      versionName = "0.2.5"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      vectorDrawables {
         useSupportLibrary = true
      }
   }

   buildTypes {
      release {
         isMinifyEnabled = true
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
   buildFeatures {
      compose = true
   }
   composeOptions {
      kotlinCompilerExtensionVersion = "1.5.3"
   }
   packaging {
      resources {
         excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
   }
}

dependencies {

   implementation("androidx.core:core-ktx:1.12.0")
   implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

   implementation("androidx.activity:activity-compose:1.8.2")
   implementation(platform("androidx.compose:compose-bom:2024.02.01"))
   implementation("androidx.compose.ui:ui")
   implementation("androidx.compose.ui:ui-graphics")
   implementation("androidx.compose.ui:ui-tooling-preview")
   implementation("androidx.compose.material3:material3:1.2.0")

   // navigation dependencies
   implementation("androidx.navigation:navigation-compose:2.7.7")
   // extended material icons, needs minify to avoid big App sizes
   implementation("androidx.compose.material:material-icons-extended:1.6.2")

   //room db plus KSP processor
   implementation("androidx.room:room-runtime:2.6.1")
   annotationProcessor("androidx.room:room-compiler:2.6.1")
   implementation("androidx.room:room-ktx:2.6.1")

   ksp("androidx.room:room-compiler:2.6.1")

   // datastore preferences
   implementation("androidx.datastore:datastore-preferences:1.0.0")
   implementation("io.insert-koin:koin-android:3.5.3")
   implementation("com.google.code.gson:gson:2.10.1")

   // coroutines
   implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
   implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

   //color picker
   implementation("com.github.skydoves:colorpicker-compose:1.0.7")

   testImplementation("junit:junit:4.13.2")
   androidTestImplementation("androidx.test.ext:junit:1.1.5")
   androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
   androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.01"))
   androidTestImplementation("androidx.compose.ui:ui-test-junit4")
   debugImplementation("androidx.compose.ui:ui-tooling")
   debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.2")
}