package dev.sanmer.whalya.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "server")
data class ServerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val baseUrl: String,
    val caCert: String,
    val clientCert: String,
    val clientKey: String,
    val name: String,
    val version: String,
    val os: String,
    val arch: String
)
