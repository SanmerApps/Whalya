plugins {
    alias(libs.plugins.self.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.sanmer.core"

    defaultConfig {
        ndk.abiFilters += listOf("arm64-v8a", "x86_64")

        consumerProguardFile("proguard-rules.pro")
    }

    sourceSets.all {
        jniLibs.srcDir("src/main/libs")
    }
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.square.retrofit)
    implementation(libs.square.retrofit.serialization)
    implementation(libs.square.okhttp)
    implementation(libs.square.okhttp.logging)
}
