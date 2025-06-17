package dev.sanmer.docker.ktx

val Throwable.messageOrName: String
    inline get(): String = message ?: javaClass.name