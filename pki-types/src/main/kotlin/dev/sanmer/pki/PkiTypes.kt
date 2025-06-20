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

    external fun pkcs8ToSec1(der: ByteArray): ByteArray
    external fun sec1ToPkcs8(der: ByteArray): ByteArray

    external fun pkcs8ToPkcs1(der: ByteArray): ByteArray
    external fun pkcs1ToPkcs8(der: ByteArray): ByteArray
}