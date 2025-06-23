package dev.sanmer.whalya.ktx

val Throwable.messageOrName: String
    inline get(): String = message ?: javaClass.name