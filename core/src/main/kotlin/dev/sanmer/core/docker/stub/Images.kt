package dev.sanmer.core.docker.stub

import dev.sanmer.core.docker.query.image.ListImagesFilters
import dev.sanmer.core.docker.query.image.OCIPlatform
import dev.sanmer.core.docker.query.image.PruneBuilderCachesFilters
import dev.sanmer.core.docker.query.image.PruneImagesFilters
import dev.sanmer.core.docker.query.image.SearchImagesFilters
import dev.sanmer.core.docker.response.container.ContainerLowLevel
import dev.sanmer.core.docker.response.image.BuilderCachePruned
import dev.sanmer.core.docker.response.image.Image
import dev.sanmer.core.docker.response.image.ImageCommited
import dev.sanmer.core.docker.response.image.ImageHistory
import dev.sanmer.core.docker.response.image.ImageLowLevel
import dev.sanmer.core.docker.response.image.ImagePruned
import dev.sanmer.core.docker.response.image.ImageRemoved
import dev.sanmer.core.docker.response.image.ImageSearched
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Images {
    @GET("images/json")
    suspend fun list(
        @Query("all") all: Boolean = false,
        @Query("filters") filters: ListImagesFilters,
        @Query("shared-size") sharedSize: Boolean = false,
        @Query("digests") digests: Boolean = false
    ): List<Image>

    @POST("build/prune")
    suspend fun pruneBuilderCache(
        @Query("reserved-space") reservedSpace: Long,
        @Query("max-used-space") maxUsedSpace: Long,
        @Query("min-free-space") minFreeSpace: Long,
        @Query("all") all: Boolean,
        @Query("filters") filters: PruneBuilderCachesFilters
    ): BuilderCachePruned

    @GET("images/{id}/json")
    suspend fun inspect(
        @Path("id") id: String
    ): ImageLowLevel

    @GET("images/{id}/history")
    suspend fun history(
        @Path("id") id: String,
        @Query("platform") platform: OCIPlatform
    ): List<ImageHistory>

    @POST("images/{id}/tag")
    suspend fun tag(
        @Path("id") id: String,
        @Query("repo") repo: String,
        @Query("tag") tag: String
    )

    @DELETE("images/{id}")
    suspend fun remove(
        @Path("id") id: String,
        @Query("force") force: Boolean = false,
        @Query("noprune") noprune: Boolean = false
    ): ImageRemoved

    @GET("images/search")
    suspend fun search(
        @Query("term") term: String,
        @Query("limit") limit: Int,
        @Query("filters") filters: SearchImagesFilters
    ): List<ImageSearched>

    @POST("images/prune")
    suspend fun prune(
        @Query("filters") filters: PruneImagesFilters
    ): ImagePruned

    @POST("commit")
    suspend fun commit(
        @Query("container") container: String,
        @Query("repo") repo: String,
        @Query("tag") tag: String,
        @Query("comment") comment: String,
        @Query("author") author: String,
        @Query("pause") pause: Boolean = false,
        @Query("changes") changes: String,
        @Body config: ContainerLowLevel.Config
    ): ImageCommited
}