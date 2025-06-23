package dev.sanmer.whalya.ui.ktx

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource

fun NestedScrollConnection.asReversed() = object : NestedScrollConnection by this {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val result = this@asReversed.onPreScroll(available.copy(y = -available.y), source)
        return result.copy(y = -result.y)
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        val result = this@asReversed.onPostScroll(
            consumed.copy(y = -consumed.y),
            available.copy(y = -available.y),
            source
        )
        return result.copy(y = -result.y)
    }
}
