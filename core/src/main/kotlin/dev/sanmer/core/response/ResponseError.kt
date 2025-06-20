package dev.sanmer.core.response

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable

@Serializable
data class ResponseError(
    val message: String
) {
    companion object Default {
        suspend fun HttpResponse.error() = IllegalStateException(
            try {
                body<ResponseError>().message
            } catch (e: Throwable) {
                status.toString()
            }
        )
    }
}