package dev.sanmer.core.resource

import io.ktor.resources.Resource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Resource("/containers")
class Containers {
    @Resource("json")
    class All(
        val parent: Containers = Containers(),
        val all: Boolean? = null,
        val limit: Int? = null,
        val size: Boolean? = null,
        val filters: String? = null
    ) {
        @Serializable
        data class Filters(
            val ancestor: List<String>? = null,
            val before: List<String>? = null,
            val expose: List<String>? = null,
            val exited: List<String>? = null,
            val health: List<String>? = null,
            val id: List<String>? = null,
            val isolation: List<String>? = null,
            val isTask: List<String>? = null,
            val label: List<String>? = null,
            val name: List<String>? = null,
            val network: List<String>? = null,
            val publish: List<String>? = null,
            val since: List<String>? = null,
            val status: List<String>? = null,
            val volume: List<String>? = null
        )
    }

    @Resource("create")
    class Create(
        val parent: Containers = Containers(),
        val name: String,
        val platform: String? = null
    )

    @Resource("{id}/json")
    class Inspect(
        val parent: Containers = Containers(),
        val id: String,
        val size: Boolean? = null
    )

    @Resource("{id}/logs")
    class Logs(
        val parent: Containers = Containers(),
        val id: String,
        val follow: Boolean? = null,
        val stdout: Boolean? = null,
        val stderr: Boolean? = null,
        val since: Long? = null,
        val until: Long? = null,
        val timestamps: Boolean? = null,
        val tail: String? = null
    )

    @Resource("{id}/top")
    class Top(
        val parent: Containers = Containers(),
        val id: String,
        @SerialName("ps_args")
        val psArgs: String? = null
    )

    @Resource("{id}/changes")
    class Changes(
        val parent: Containers = Containers(),
        val id: String
    )

    @Resource("{id}/stats")
    class Stats(
        val parent: Containers = Containers(),
        val id: String,
        val stream: Boolean? = null,
        @SerialName("one-shot")
        val oneShot: Boolean? = null
    )

    @Resource("{id}/start")
    class Start(
        val parent: Containers = Containers(),
        val id: String,
        val detachKeys: String? = null
    )

    @Resource("{id}/stop")
    class Stop(
        val parent: Containers = Containers(),
        val id: String,
        val signal: String? = null,
        val t: Int? = null
    )

    @Resource("{id}/restart")
    class Restart(
        val parent: Containers = Containers(),
        val id: String,
        val signal: String? = null,
        val t: Int? = null
    )

    @Resource("{id}/kill")
    class Kill(
        val parent: Containers = Containers(),
        val id: String,
        val signal: String? = null
    )

    @Resource("{id}/update")
    class Update(
        val parent: Containers = Containers(),
        val id: String
    )

    @Resource("{id}/rename")
    class Rename(
        val parent: Containers = Containers(),
        val id: String,
        val name: String
    )

    @Resource("{id}/pause")
    class Pause(
        val parent: Containers = Containers(),
        val id: String
    )

    @Resource("{id}/unpause")
    class Unpause(
        val parent: Containers = Containers(),
        val id: String
    )

    @Resource("{id}")
    class Remove(
        val parent: Containers = Containers(),
        val id: String,
        val v: Boolean? = null,
        val force: Boolean? = null,
        val link: Boolean? = null
    )

    @Resource("prune")
    class Prune(
        val parent: Containers = Containers(),
        val filters: String? = null
    ) {
        @Serializable
        data class PruneContainersFilters(
            val until: List<String>? = null,
            val label: List<String>? = null
        )
    }
}