package dev.sanmer.whalya.repository

import dev.sanmer.whalya.database.entity.ServerEntity
import io.ktor.client.HttpClient
import kotlin.reflect.KProperty

interface ClientRepository {
    val current: HttpClient
    fun getOrCreate(server: ServerEntity): HttpClient
    fun drop(serverId: Long): HttpClient?
    operator fun getValue(thisObj: Any?, property: KProperty<*>): ServerEntity
    operator fun setValue(thisObj: Any?, property: KProperty<*>, value: ServerEntity)
}