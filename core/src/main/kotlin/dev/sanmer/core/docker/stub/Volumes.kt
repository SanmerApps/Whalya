package dev.sanmer.core.docker.stub

import dev.sanmer.core.docker.query.volume.ListVolumesFilters
import dev.sanmer.core.docker.query.volume.PruneVolumesFilters
import dev.sanmer.core.docker.request.volume.VolumeConfig
import dev.sanmer.core.docker.response.volume.Volume
import dev.sanmer.core.docker.response.volume.VolumeList
import dev.sanmer.core.docker.response.volume.VolumePruned
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Volumes {
    @GET("volumes")
    suspend fun list(
        @Query("filters") filters: ListVolumesFilters
    ): VolumeList

    @POST("volumes/create")
    suspend fun create(
        @Body config: VolumeConfig
    ): Volume

    @GET("volumes/{name}")
    suspend fun inspect(
        @Path("name") name: String
    ): Volume

    @DELETE("volumes/{name}")
    suspend fun remove(
        @Path("name") name: String,
        @Query("force") force: Boolean = false
    )

    @POST("volumes/prune")
    suspend fun prune(
        @Query("filters") filters: PruneVolumesFilters
    ): VolumePruned
}