package dev.sanmer.whalya.model.ui.home

import dev.sanmer.core.Labels
import dev.sanmer.core.response.network.Network
import dev.sanmer.whalya.ktx.copy
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class UiNetwork(
    val original: Network
) {
    val name: String
        inline get() = "${original.name} <${original.driver}>"

    val id: String
        inline get() = original.id

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
}
