package dev.sanmer.core.ktx

import dev.sanmer.core.response.ResponseError
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

suspend fun HttpResponse.error() = IllegalStateException(
    try {
        body<ResponseError>().message
    } catch (_: Throwable) {
        status.toString()
    }
)