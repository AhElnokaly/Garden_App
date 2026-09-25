import java.util.Base64

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.gardencompanion.vxqzt"
    minSdk = 24
    targetSdk = 36
    versionCode = 5
    versionName = "0.2.3"

    buildConfigField("boolean", "AI_ENABLED", "false")

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    // Only used once a release keystore + secrets exist (KEYSTORE_PATH / STORE_PASSWORD /
    // KEY_PASSWORD). Not wired into a buildType yet — see notes in the CI workflow.
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      val keystoreFile = file("${rootDir}/debug.keystore")
      if (!keystoreFile.exists()) {
        val b64 = "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"
        keystoreFile.writeBytes(Base64.getDecoder().decode(b64))
      }
      signingConfig = signingConfigs.create("debugConfig") {
        storeFile = keystoreFile
        storePassword = "android"
        keyAlias = "androiddebugkey"
        keyPassword = "android"
      }
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  // implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  // implementation(libs.firebase.firestore)

  // Uncomment ALL FOUR of the following dependencies together to use Firebase Auth and Google
  // Sign-In via Credential Manager:
  // implementation(libs.firebase.auth)
  // implementation(libs.androidx.credentials)
  // implementation(libs.androidx.credentials.play.services)
  // implementation(libs.googleid)
  // implementation(libs.firebase.appcheck.recaptcha)
  // implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
