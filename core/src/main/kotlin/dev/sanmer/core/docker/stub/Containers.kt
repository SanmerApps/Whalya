package dev.sanmer.core.docker.stub

import dev.sanmer.core.docker.query.container.ListContainersFilters
import dev.sanmer.core.docker.query.container.LogsContainerTail
import dev.sanmer.core.docker.query.container.PruneContainersFilters
import dev.sanmer.core.docker.query.container.WaitContainerCondition
import dev.sanmer.core.docker.request.container.ContainerConfig
import dev.sanmer.core.docker.request.container.HostConfig
import dev.sanmer.core.docker.response.container.Container
import dev.sanmer.core.docker.response.container.ContainerChanges
import dev.sanmer.core.docker.response.container.ContainerCreated
import dev.sanmer.core.docker.response.container.ContainerLog
import dev.sanmer.core.docker.response.container.ContainerLowLevel
import dev.sanmer.core.docker.response.container.ContainerPruned
import dev.sanmer.core.docker.response.container.ContainerStats
import dev.sanmer.core.docker.response.container.ContainerTop
import dev.sanmer.core.docker.response.container.ContainerWaited
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Containers {
    @GET("containers/json")
    suspend fun list(
        @Query("all") all: Boolean = false,
        @Query("limit") limit: Int,
        @Query("size") size: Boolean = false,
        @Query("filters") filters: ListContainersFilters
    ): List<Container>

    @POST("containers/create")
    suspend fun create(
        @Body config: ContainerConfig
    ): ContainerCreated

    @GET("containers/{id}/json")
    suspend fun inspect(
        @Path("id") id: String,
        @Query("size") size: Boolean = false
    ): ContainerLowLevel

    @GET("containers/{id}/logs?follow=false")
    suspend fun logs(
        @Path("id") id: String,
        @Query("stdout") stdout: Boolean = false,
        @Query("stderr") stderr: Boolean = false,
        @Query("since") since: Long = 0,
        @Query("until") until: Long = 0,
        @Query("timestamps") timestamps: Boolean = false,
        @Query("tail") tail: LogsContainerTail = LogsContainerTail.All
    ): List<ContainerLog>

    @GET("containers/{id}/top")
    suspend fun top(
        @Path("id") id: String,
        @Query("ps_args") size: String = "-ef"
    ): ContainerTop

    @GET("containers/{id}/changes")
    suspend fun changes(
        @Path("id") id: String
    ): List<ContainerChanges>

    @GET("containers/{id}/stats?stream=false&one-shot=false")
    suspend fun stats(
        @Path("id") id: String
    ): ContainerStats

    @POST("containers/{id}/resize")
    suspend fun resizeTTY(
        @Path("id") id: String,
        @Query("h") h: Int,
        @Query("w") w: Int
    )

    @POST("containers/{id}/start")
    suspend fun start(
        @Path("id") id: String,
        @Query("detachKeys") detachKeys: String
    )

    @POST("containers/{id}/stop")
    suspend fun stop(
        @Path("id") id: String,
        @Query("signal") signal: String,
        @Query("t") t: Int
    )

    @POST("containers/{id}/restart")
    suspend fun restart(
        @Path("id") id: String,
        @Query("signal") signal: String,
        @Query("t") t: Int
    )

    @POST("containers/{id}/kill")
    suspend fun kill(
        @Path("id") id: String,
        @Query("signal") signal: String
    )

    @POST("containers/{id}/update")
    suspend fun update(
        @Path("id") id: String,
        @Body config: HostConfig
    )

    @POST("containers/{id}/rename")
    suspend fun rename(
        @Path("id") id: String,
        @Query("name") name: String
    )

    @POST("containers/{id}/pause")
    suspend fun pause(
        @Path("id") id: String
    )

    @POST("containers/{id}/unpause")
    suspend fun unpause(
        @Path("id") id: String
    )

    @POST("containers/{id}/wait")
    suspend fun wait(
        @Path("id") id: String,
        @Query("condition") condition: WaitContainerCondition
    ): ContainerWaited

    @DELETE("containers/{id}")
    suspend fun remove(
        @Path("id") id: String,
        @Query("v") v: Boolean = false,
        @Query("force") force: Boolean = false,
        @Query("link") link: Boolean = false
    )

    @POST("containers/prune")
    suspend fun prune(
        @Query("filters") filters: PruneContainersFilters
    ): ContainerPruned
}