package dev.sanmer.whalya.ktx

import android.content.Context

val Context.deviceProtectedContext: Context
    inline get() = createDeviceProtectedStorageContext()