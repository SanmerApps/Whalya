package dev.sanmer.whalya.ui.ktx

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.unit.Dp

fun CornerBasedShape.bottom(size: Dp) =
    copy(bottomStart = CornerSize(size), bottomEnd = CornerSize(size))