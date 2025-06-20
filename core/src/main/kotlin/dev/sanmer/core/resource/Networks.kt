package dev.sanmer.core.resource

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Resource("networks")
class Networks(
    val filters: String? = null
) {
    @Serializable
    data class Filters(
        val dangling: List<String>? = null,
        val driver: List<String>? = null,
        val id: List<String>? = null,
        val label: List<String>? = null,
        val name: List<String>? = null,
        val scope: List<String>? = null,
        val type: List<String>? = null
    )

    @Resource("{id}")
    class Inspect(
        val parent: Networks = Networks(),
        val id: String,
        val verbose: Boolean? = null,
        val scope: String? = null
    )

    @Resource("{id}")
    class Remove(
        val parent: Networks = Networks(),
        val id: String
    )

    @Resource("create")
    class Create(
        val parent: Networks = Networks()
    )

    @Resource("{id}/connect")
    class Connect(
        val parent: Networks = Networks(),
        val id: String
    )

    @Resource("{id}/disconnect")
    class Disconnect(
        val parent: Networks = Networks(),
        val id: String
    )

    @Resource("prune")
    class Prune(
        val parent: Networks = Networks(),
        val filters: String? = null
    ) {
        @Serializable
        data class Filters(
            val until: List<String>? = null,
            val label: List<String>? = null
        )
    }
}