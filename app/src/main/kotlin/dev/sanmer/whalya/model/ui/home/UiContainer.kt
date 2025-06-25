package dev.sanmer.whalya.model.ui.home

import dev.sanmer.core.Labels
import dev.sanmer.core.response.container.Container
import dev.sanmer.whalya.ktx.ifNotEmpty
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

data class UiContainer(
    val original: Container
) {
    val isRunning: Boolean
        inline get() = original.state.isRunning

    val id: String
        inline get() = original.id

    val name by lazy {
        original.names.firstOrNull()?.name() ?: id.shortId()
    }

    val exposedPorts by lazy {
        original.ports.mapNotNull { port ->
            port.ip.ifNotEmpty { "${port.publicPort}:${port.privatePort}" }
        }.distinct()
    }

    val networks by lazy {
        original.networkSettings.networks.keys.toList()
    }

    val createdAt by lazy {
        Instant.fromEpochSeconds(original.created, 0)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .toString()
    }

    val composeProject by lazy {
        original.labels[Labels.COMPOSE_PROJECT]
    }

    val composeVersion by lazy {
        original.labels[Labels.COMPOSE_VERSION]
    }

    companion object Default {
        fun String.name() = removePrefix("/")
        fun String.shortId() = substring(0, 12)
    }
}
