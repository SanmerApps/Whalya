package dev.sanmer.docker.ktx

inline fun <C> C.ifNotEmpty(block: (C) -> C): C? where C : CharSequence {
    return if (isNotEmpty()) block(this) else null
}