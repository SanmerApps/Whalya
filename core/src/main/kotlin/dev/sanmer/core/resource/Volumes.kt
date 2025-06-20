package dev.sanmer.core.resource

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Resource("volumes")
class Volumes(
    val filters: String? = null
) {
    @Serializable
    data class Filters(
        val dangling: List<String>? = null,
        val driver: List<String>? = null,
        val label: List<String>? = null,
        val name: List<String>? = null
    )

    @Resource("create")
    class Create(
        val parent: Volumes = Volumes()
    )

    @Resource("{name}")
    class Inspect(
        val parent: Volumes = Volumes(),
        val name: String
    )

    @Resource("{name}")
    class Remove(
        val parent: Volumes = Volumes(),
        val name: String,
        val force: Boolean? = null
    )

    @Resource("prune")
    class Prune(
        val parent: Volumes = Volumes(),
        val filters: String? = null
    ) {
        @Serializable
        data class Filters(
            val label: List<String>? = null,
            val all: List<String>? = null
        )
    }
}