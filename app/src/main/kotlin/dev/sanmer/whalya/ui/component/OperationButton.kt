package dev.sanmer.whalya.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun OperationButton(
    onClick: () -> Unit,
    painter: Painter,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            enabled = enabled
        ) {
            Icon(
                painter = painter,
                contentDescription = contentDescription
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge
        )
    }
}