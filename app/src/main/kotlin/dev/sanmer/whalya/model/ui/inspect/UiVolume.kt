package dev.sanmer.whalya.model.ui.inspect

import dev.sanmer.core.Labels
import dev.sanmer.core.response.volume.Volume
import dev.sanmer.whalya.ktx.copy
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class UiVolume(
    val original: Volume
) {
    val driver: String
        inline get() = original.driver

    val mountPoint: String
        inline get() = original.mountPoint

    val createdAt by lazy {
        original.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
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