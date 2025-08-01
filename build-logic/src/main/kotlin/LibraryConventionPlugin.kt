import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class LibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.library")
        apply(plugin = "org.jetbrains.kotlin.android")

        extensions.configure<LibraryExtension> {
            compileSdk = 35
            buildToolsVersion = "35.0.1"

            defaultConfig {
                minSdk = 30
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
            }
        }

        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        extensions.configure<KotlinAndroidProjectExtension> {
            jvmToolchain(21)

            sourceSets.all {
                languageSettings {
                    optIn("kotlin.time.ExperimentalTime")
                }
            }
        }
    }
}
