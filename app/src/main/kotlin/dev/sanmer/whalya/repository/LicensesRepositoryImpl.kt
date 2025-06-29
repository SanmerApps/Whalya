package dev.sanmer.whalya.repository

import android.content.Context
import dev.sanmer.whalya.Const
import dev.sanmer.whalya.model.license.Artifact
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class LicensesRepositoryImpl(
    private val context: Context
) : LicensesRepository {
    override fun fetch(): List<Artifact> {
        return context.assets.open(Const.LICENSEE_PATH).use { stream ->
            Json.decodeFromStream(stream)
        }
    }
}