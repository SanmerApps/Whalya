package dev.sanmer.docker.ktx

import android.text.format.FormatterHidden
import dev.sanmer.docker.compat.ContextCompat

fun Long.sizeBySI(): String {
    val context = ContextCompat.getContext()
    return FormatterHidden.formatFileSize(context, this, FormatterHidden.FLAG_SI_UNITS)
}

fun Long.sizeByIEC(): String {
    val context = ContextCompat.getContext()
    return FormatterHidden.formatFileSize(context, this, FormatterHidden.FLAG_IEC_UNITS)
}