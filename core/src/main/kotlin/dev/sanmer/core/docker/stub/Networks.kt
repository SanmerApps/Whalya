package dev.sanmer.core.docker.stub

import dev.sanmer.core.docker.query.network.ListNetworksFilters
import dev.sanmer.core.docker.query.network.PruneNetworksFilters
import dev.sanmer.core.docker.request.network.NetworkConfig
import dev.sanmer.core.docker.request.network.NetworkConnectConfig
import dev.sanmer.core.docker.request.network.NetworkDisconnectConfig
import dev.sanmer.core.docker.response.network.Network
import dev.sanmer.core.docker.response.network.NetworkCreated
import dev.sanmer.core.docker.response.network.NetworkPruned
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Networks {
    @GET("networks")
    suspend fun list(
        @Query("filters") filters: ListNetworksFilters
    ): List<Network>

    @GET("networks/{id}")
    suspend fun inspect(
        @Path("id") id: String,
        @Query("verbose") verbose: Boolean = false,
        @Query("scope") scope: String
    ): Network

    @DELETE("networks/{id}")
    suspend fun remove(
        @Path("id") id: String
    )

    @POST("networks/create")
    suspend fun create(
        @Body config: NetworkConfig
    ): NetworkCreated

    @POST("networks/{id}/connect")
    suspend fun connect(
        @Path("id") id: String,
        @Body config: NetworkConnectConfig
    )

    @POST("networks/{id}/disconnect")
    suspend fun disconnect(
        @Path("id") id: String,
        @Body config: NetworkDisconnectConfig
    )

    @POST("networks/prune")
    suspend fun prune(
        @Query("filters") filters: PruneNetworksFilters
    ): NetworkPruned
}