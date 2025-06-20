package dev.sanmer.pki

import java.security.KeyFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class SSLContextCompat(
    val ctx: SSLContext,
    val kmf: KeyManagerFactory,
    val tmf: TrustManagerFactory
) {
    val socketFactory: SSLSocketFactory
        inline get() = ctx.socketFactory

    val trustManager: X509TrustManager
        inline get() = tmf.trustManagers.first { it is X509TrustManager } as X509TrustManager

    companion object Default {
        fun mTLS(
            caCert: ByteArray,
            clientCert: ByteArray,
            clientKey: ByteArray
        ): SSLContextCompat {
            val pkcs8Key = PkiTypes.loadToPkcs8(clientKey)
            val keySpec = PKCS8EncodedKeySpec(pkcs8Key.encoded)
            val key = KeyFactory.getInstance(pkcs8Key.algorithm).generatePrivate(keySpec)

            val certFactory = CertificateFactory.getInstance("X.509")
            val ca = certFactory.generateCertificate(caCert.inputStream()) as X509Certificate
            val cert = certFactory.generateCertificate(clientCert.inputStream()) as X509Certificate

            val keyStore = KeyStore.getInstance("PKCS12").apply { load(null, null) }
            keyStore.setKeyEntry("client", key, null, arrayOf(cert))

            val trustStore = KeyStore.getInstance("PKCS12").apply { load(null, null) }
            trustStore.setCertificateEntry("ca", ca)

            val kmf = KeyManagerFactory.getInstance("X509").apply { init(keyStore, null) }
            val tmf = TrustManagerFactory.getInstance("X509").apply { init(trustStore) }
            val ctx = SSLContext.getInstance("TLS")
                .apply { init(kmf.keyManagers, tmf.trustManagers, null) }

            return SSLContextCompat(
                ctx = ctx,
                kmf = kmf,
                tmf = tmf
            )
        }
    }
}