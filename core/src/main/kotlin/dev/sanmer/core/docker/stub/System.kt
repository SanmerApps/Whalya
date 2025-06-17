package dev.sanmer.core.docker.stub

import dev.sanmer.core.docker.response.system.SystemDataUsage
import dev.sanmer.core.docker.response.system.SystemInfo
import dev.sanmer.core.docker.response.system.SystemVersion
import retrofit2.http.GET

interface System {
    @GET("info")
    suspend fun info(): SystemInfo

    @GET("version")
    suspend fun version(): SystemVersion

    @GET("system/df")
    suspend fun dataUsage(): SystemDataUsage
}