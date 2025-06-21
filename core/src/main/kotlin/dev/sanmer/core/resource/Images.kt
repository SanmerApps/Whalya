package dev.sanmer.core.resource

import io.ktor.resources.Resource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Resource("/images")
class Images {
    @Resource("json")
    class All(
        val parent: Images = Images(),
        val all: Boolean? = null,
        val filters: String? = null,
        @SerialName("shared-size")
        val sharedSize: Boolean? = null,
        val digests: Boolean? = null
    ) {
        @Serializable
        data class Filters(
            val before: List<String>? = null,
            val dangling: List<String>? = null,
            val label: List<String>? = null,
            val reference: List<String>? = null,
            val since: List<String>? = null,
            val until: List<String>? = null
        )
    }

    @Resource("create")
    class Create(
        val parent: Images = Images(),
        val fromImage: String,
        val fromSrc: String? = null,
        val repo: String? = null,
        val tag: String? = null,
        val message: String? = null,
        val changes: List<String>? = null,
        val platform: String? = null
    )

    @Resource("{id}/json")
    class Inspect(
        val parent: Images = Images(),
        val id: String
    )

    @Resource("{id}/history")
    class History(
        val parent: Images = Images(),
        val id: String,
        val platform: String
    )

    @Resource("{id}")
    class Remove(
        val parent: Images = Images(),
        val id: String,
        val force: Boolean? = null,
        @SerialName("noprune")
        val noPrune: Boolean? = null,
        val platforms: List<String>? = null
    )

    @Resource("prune")
    class Prune(
        val parent: Images = Images(),
        val filters: String? = null
    ) {
        @Serializable
        data class Filters(
            val dangling: List<String>? = null,
            val until: List<String>? = null,
            val label: List<String>? = null
        )
    }
}