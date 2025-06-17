package dev.sanmer.docker.ktx

import android.content.Context

val Context.deviceProtectedContext: Context
    inline get() = createDeviceProtectedStorageContext()