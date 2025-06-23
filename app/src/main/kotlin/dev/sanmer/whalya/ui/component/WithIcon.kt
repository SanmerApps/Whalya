package dev.sanmer.whalya.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun WithIcon(
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(15.dp),
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    icon: @Composable RowScope.() -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        content()
        icon()
    }
}

@Composable
fun WithIcon(
    painter: Painter,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(15.dp),
    content: @Composable RowScope.() -> Unit
) {
    WithIcon(
        horizontalArrangement = horizontalArrangement,
        icon = {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        content = content
    )
}