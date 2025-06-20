plugins {
    alias(libs.plugins.self.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.sanmer.core"
}

dependencies {
    implementation(projects.pkiTypes)

    implementation(libs.androidx.annotation)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)

    api(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.resources)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
}
