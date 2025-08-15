package dev.sanmer.core.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseError(
    val message: String
)