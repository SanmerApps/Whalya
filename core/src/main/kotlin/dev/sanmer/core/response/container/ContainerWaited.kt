package dev.sanmer.core.response.container

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContainerWaited(
    @SerialName("StatusCode")
    val statusCode: Long,
    @SerialName("Error")
    val error: ExitError,
) {
    @Serializable
    data class ExitError(
        @SerialName("Message")
        val message: String
    )
}
