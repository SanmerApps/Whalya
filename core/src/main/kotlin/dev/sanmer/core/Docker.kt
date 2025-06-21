package dev.sanmer.core

import android.util.Log
import dev.sanmer.core.ConverterFactory.containerLog
import dev.sanmer.core.response.ResponseError.Default.error
import dev.sanmer.core.response.container.ContainerLog
import dev.sanmer.pki.SSLContextCompat
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.LoggingFormat
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.prepareRequest
import io.ktor.client.plugins.resources.request
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpMethod
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import okhttp3.OkHttpClient

object Docker {
    const val API_VERSION = "1.50"

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
            containerLog(ContainerLog.RAW)
            containerLog(ContainerLog.MULTIPLEXED)
        }
        install(Resources)
        install(UserAgent) {
            agent = "Docker/$API_VERSION"
        }
    }

    suspend inline fun <reified T : Any> HttpClient.get(
        resource: T,
        builder: HttpRequestBuilder.() -> Unit = {}
    ) = request(resource) {
        method = HttpMethod.Get
        builder()
    }

    suspend inline fun <reified T : Any> HttpClient.prepareGet(
        resource: T,
        builder: HttpRequestBuilder.() -> Unit = {}
    ) = prepareRequest(resource) {
        method = HttpMethod.Get
        builder()
    }

    suspend inline fun <reified T : Any> HttpClient.post(
        resource: T,
        builder: HttpRequestBuilder.() -> Unit = {}
    ) = request(resource) {
        method = HttpMethod.Post
        builder()
    }

    suspend inline fun <reified T : Any> HttpClient.delete(
        resource: T,
        builder: HttpRequestBuilder.() -> Unit = {}
    ) = request(resource) {
        method = HttpMethod.Delete
        builder()
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