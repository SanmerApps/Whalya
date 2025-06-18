package dev.sanmer.core.docker

import android.util.Log
import dev.sanmer.core.Auth
import dev.sanmer.core.Auth.Default.addAuth
import dev.sanmer.core.Auth.Default.addMutualTLS
import dev.sanmer.core.JsonCompat
import dev.sanmer.core.docker.stub.Containers
import dev.sanmer.core.docker.stub.Images
import dev.sanmer.core.docker.stub.Networks
import dev.sanmer.core.docker.stub.System
import dev.sanmer.core.docker.stub.Volumes
import okhttp3.ConnectionSpec
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.util.Locale
import java.util.concurrent.TimeUnit

class Docker(
    private val baseUrl: String,
    private val auth: Auth
) {
    private val okhttp by lazy {
        createOkHttpClient {
            addHeader("User-Agent", "Docker/$API_VERSION")
            addAuth(auth)
        }.apply {
            addMutualTLS(auth)
        }.build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(ConverterFactory)
            .addConverterFactory(
                JsonCompat.asConverterFactory("application/json; charset=UTF8".toMediaType())
            )
            .client(okhttp)
            .baseUrl(baseUrl)
            .build()
    }

    val system by lazy { retrofit.create<System>() }
    val containers by lazy { retrofit.create<Containers>() }
    val images by lazy { retrofit.create<Images>() }
    val networks by lazy { retrofit.create<Networks>() }
    val volumes by lazy { retrofit.create<Volumes>() }

    companion object Default {
        const val API_VERSION = "1.50"

        private fun createOkHttpClient(
            header: Request.Builder.() -> Request.Builder = { this }
        ) = OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(
                HttpLoggingInterceptor {
                    Log.d("Docker", it)
                }.apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                request.header("Accept-Language", Locale.getDefault().toLanguageTag())
                chain.proceed(request.header().build())
            }
    }
}