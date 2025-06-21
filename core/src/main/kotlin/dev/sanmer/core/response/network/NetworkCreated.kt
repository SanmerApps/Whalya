package dev.sanmer.core.response.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCreated(
    @SerialName("Id")
    val id: String,
    @SerialName("Warnings")
    val warnings: String
)
