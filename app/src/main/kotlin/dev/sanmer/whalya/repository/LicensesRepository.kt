package dev.sanmer.whalya.repository

import dev.sanmer.whalya.model.license.Artifact

interface LicensesRepository {
    fun fetch(): List<Artifact>
}