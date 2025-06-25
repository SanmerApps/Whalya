package dev.sanmer.core.ktx

import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModuleBuilder

inline fun <reified T : Any> SerializersModuleBuilder.contextual(serializer: KSerializer<T>) {
    contextual(T::class, serializer)
}