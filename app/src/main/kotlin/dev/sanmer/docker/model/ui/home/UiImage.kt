package dev.sanmer.docker.model.ui.home

import dev.sanmer.core.docker.Labels
import dev.sanmer.core.docker.response.image.Image
import dev.sanmer.docker.ktx.sizeBySI
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class UiImage(
    val original: Image
) {
    val name by lazy {
        original.repoTags.firstOrNull()
    }

    val id by lazy {
        original.id.digest()
    }

    val createdAt by lazy {
        Instant.fromEpochSeconds(original.created, 0)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .toString()
    }

    val size by lazy {
        original.size.sizeBySI()
    }

    val repoDigests by lazy {
        original.repoDigests.joinToString("\n") { it }
    }

    val ociVersion by lazy {
        original.labels[Labels.OCI_VERSION]
    }

    val ociLicenses by lazy {
        original.labels[Labels.OCI_LICENSES]?.takeIf { it != Labels.OCI_NO_LICENSES }
    }

    companion object Default {
        fun String.digest() = removePrefix("sha256:")
    }
}
