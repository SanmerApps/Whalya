package dev.sanmer.core

import okhttp3.OkHttpClient
import okhttp3.Request
import javax.net.ssl.X509TrustManager

sealed class Auth {
    data object None : Auth()
    class Bearer(val token: String) : Auth()
    class MutualTLS(
        val caCert: ByteArray,
        val clientCert: ByteArray,
        val clientKey: ByteArray
    ) : Auth() {
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

    companion object Default {
        internal fun Request.Builder.addAuth(auth: Auth): Request.Builder {
            return when (auth) {
                is Bearer -> if (auth.token.isNotBlank()) {
                    header("Authorization", "Bearer ${auth.token}")
                } else {
                    this
                }

                else -> this
            }
        }

        internal fun OkHttpClient.Builder.addMutualTLS(auth: Auth): OkHttpClient.Builder {
            if (auth !is MutualTLS) return this
            if (auth.clientKey.isEmpty()
                || auth.clientCert.isEmpty()
                || auth.caCert.isEmpty()
            ) return this

            val compat = SSLContextCompat.mTLS(
                caCert = auth.caCert,
                clientCert = auth.clientCert,
                clientKey = auth.clientKey
            )

            return sslSocketFactory(
                sslSocketFactory = compat.ctx.socketFactory,
                trustManager = compat.tmf.trustManagers[0] as X509TrustManager
            )
        }
    }
}