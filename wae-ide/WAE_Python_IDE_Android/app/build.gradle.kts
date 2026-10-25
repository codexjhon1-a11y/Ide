plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.chaquo.python")
}

android {
    namespace = "com.weareengineers.waeide"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.weareengineers.waeide"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }
}

chaquopy {
    defaultConfig {
        version = "3.13"
    }
}
