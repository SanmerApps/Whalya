plugins {
    alias(libs.plugins.self.library)
}

android {
    namespace = "android.app"
}

dependencies {
    annotationProcessor(libs.rikka.refine.compiler)
    compileOnly(libs.rikka.refine.annotation)
    compileOnly(libs.androidx.annotation)
}