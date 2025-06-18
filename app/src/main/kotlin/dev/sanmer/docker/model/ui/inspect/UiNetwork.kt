package dev.sanmer.docker.model.ui.inspect

import dev.sanmer.core.docker.Labels
import dev.sanmer.core.docker.response.network.Network
import dev.sanmer.docker.ktx.copy
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class UiNetwork(
    val original: Network
) {
    val containers by lazy {
        original.containers.map {
            NetworkContainer(
                id = it.key,
                original = it.value
            )
        }
    }

    val name: String
        inline get() = original.name

    val driver: String
        inline get() = original.driver

    val scope: String
        inline get() = original.scope

    val subnet by lazy {
        original.ipAM.config.joinToString("\n") { it.subnet }
    }

    val gateway by lazy {
        original.ipAM.config.joinToString("\n") { it.gateway }
    }

    val createdAt by lazy {
        original.created.toLocalDateTime(TimeZone.currentSystemDefault())
            .copy(nanosecond = 0)
            .toString()
    }

    val composeProject by lazy {
        original.labels[Labels.COMPOSE_PROJECT]
    }

    val composeVersion by lazy {
        original.labels[Labels.COMPOSE_VERSION]
    }

    data class NetworkContainer(
        val id: String,
        val original: Network.NetworkContainer
    ) {
        val name: String
            inline get() = original.name

        val ipv4Address: String
            inline get() = original.ipv4Address

        val ipv6Address: String
            inline get() = original.ipv6Address

        val macAddress: String
            inline get() = original.macAddress
    }
}
