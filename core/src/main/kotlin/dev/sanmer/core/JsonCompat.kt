package dev.sanmer.core

import dev.sanmer.core.ktx.contextual
import dev.sanmer.core.serializer.ChangeTypeSerializer
import dev.sanmer.core.serializer.InstantSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

object JsonCompat {
    val default = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        serializersModule = SerializersModule {
            contextual(InstantSerializer)
            contextual(ChangeTypeSerializer)
        }
    }

    val printer = Json(default) {
        prettyPrint = true
    }

    inline fun <reified T> String.decodeJson(): T =
        default.decodeFromString(this)

    inline fun <reified T> T.encodeJson(pretty: Boolean = false) = if (pretty) {
        printer.encodeToString(this)
    } else {
        default.encodeToString(this)
    }
}