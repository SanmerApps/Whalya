package dev.sanmer.core

import android.util.Log
import dev.sanmer.core.ConverterFactory.multiplexedContainerLog
import dev.sanmer.core.ConverterFactory.rawContainerLog
import dev.sanmer.core.ktx.error
import dev.sanmer.pki.SSLContextCompat
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.HttpTimeoutConfig
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import okhttp3.OkHttpClient

object Docker {
    const val API_VERSION = "1.52"

    fun client(
        baseUrl: String,
        mTLS: MutualTLS
    ) = HttpClient(OkHttp) {
        engine {
            config {
                addMutualTLS(mTLS)
            }
        }
        defaultRequest {
            url(baseUrl)
        }
        HttpResponseValidator {
            validateResponse { response ->
                if (!response.status.isSuccess()) {
                    throw response.error()
                }
            }
        }
        Logging {
            logger = AndroidLogger("Docker")
            format = LoggingFormat.OkHttp
            level = LogLevel.INFO
        }
        install(ContentNegotiation) {
            json(JsonCompat.default)
            rawContainerLog()
            multiplexedContainerLog()
        }
        install(Resources)
        install(UserAgent) {
            agent = "Docker/$API_VERSION"
        }
        install(HttpTimeout)
    }

    fun HttpRequestBuilder.infiniteTimeout() {
        timeout {
            requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            socketTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
        }
    }

    class MutualTLS(
        val caCert: ByteArray,
        val clientCert: ByteArray,
        val clientKey: ByteArray
    ) {
        constructor(
            caCert: String,
            clientCert: String,
            clientKey: String
        ) : this(
            caCert = caCert.toByteArray(),
            clientCert = clientCert.toByteArray(),
            clientKey = clientKey.toByteArray()
        )
    }

    private class AndroidLogger(
        private val tag: String
    ) : Logger {
        override fun log(message: String) {
            Log.d(tag, message)
        }
    }

    private fun OkHttpClient.Builder.addMutualTLS(mTLS: MutualTLS): OkHttpClient.Builder {
        if (mTLS.clientKey.isEmpty()
            || mTLS.clientCert.isEmpty()
            || mTLS.caCert.isEmpty()
        ) return this

        val ctx = SSLContextCompat.mTLS(
            caCert = mTLS.caCert,
            clientCert = mTLS.clientCert,
            clientKey = mTLS.clientKey
        )

        return sslSocketFactory(
            sslSocketFactory = ctx.socketFactory,
            trustManager = ctx.trustManager
        )
    }
}