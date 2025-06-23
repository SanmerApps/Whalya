package dev.sanmer.whalya.model.ui.inspect

import dev.sanmer.core.Labels
import dev.sanmer.core.response.image.ImageHistory
import dev.sanmer.core.response.image.ImageLowLevel
import dev.sanmer.whalya.ktx.copy
import dev.sanmer.whalya.ktx.sizeBySI
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class UiImage(
    val original: ImageLowLevel
) {
    val command by lazy {
        with(original.config) { (entryPoint + cmd).joinToString(" ") }
    }

    val name by lazy {
        original.repoTags.firstOrNull()?.imageName().orEmpty()
    }

    val repoTags by lazy {
        original.repoTags.joinToString("\n") { it }
    }

    val repoDigests by lazy {
        original.repoDigests.joinToString("\n") { it }
    }

    val platform by lazy {
        listOf(original.os, original.architecture)
    }

    val createdAt by lazy {
        original.created.toLocalDateTime(TimeZone.currentSystemDefault())
            .copy(nanosecond = 0)
            .toString()
    }

    val size by lazy {
        original.size.sizeBySI()
    }

    val ociVersion by lazy {
        original.config.labels[Labels.OCI_VERSION]
    }

    val ociLicenses by lazy {
        original.config.labels[Labels.OCI_LICENSES]?.takeIf { it != Labels.OCI_NO_LICENSES }
    }

    val labels by lazy {
        original.config.labels.toList()
    }

    data class History(
        val original: ImageHistory
    ) {
        val createdBy: String
            inline get() = original.createdBy

        val createdAt by lazy {
            Instant.fromEpochSeconds(original.created, 0)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .toString()
        }

        val size by lazy {
            original.size.sizeBySI()
        }
    }

    companion object Default {
        fun String.imageName(): String {
            return "(?:.*/)?([^:/]+)(?::[^/]+)?$".toRegex()
                .find(this)?.groups?.get(1)?.value.orEmpty()
        }
    }
}
