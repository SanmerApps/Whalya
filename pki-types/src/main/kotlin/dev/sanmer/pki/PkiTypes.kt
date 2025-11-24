package dev.sanmer.pki

object PkiTypes {
    init {
        System.loadLibrary("pki_types")
    }

    class PrivatePkcs8KeyDer(
        val encoded: ByteArray,
        val algorithm: String
    )

    external fun loadToPkcs8(pem: ByteArray): PrivatePkcs8KeyDer
}