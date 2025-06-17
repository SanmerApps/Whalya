package dev.sanmer.docker.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun ActivePoint(
    modifier: Modifier,
    color: Color = Color(90, 206, 129)
) {
    val infiniteTransition = rememberInfiniteTransition()
    val value by infiniteTransition.animateValue(
        initialValue = 0.5f,
        targetValue = 1f,
        typeConverter = Float.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(
        modifier = modifier
    ) {
        val width = size.width * (1 / 2f)

        drawCircle(
            color = color,
            radius = width * (2 / 3f),
            center = center
        )

        drawCircle(
            color = color.copy(alpha = 1f - value),
            radius = width * value,
            center = center,
            style = Stroke(width = width * (1 / 2f))
        )
    }
}

@Composable
fun DeadPoint(
    modifier: Modifier,
    color: Color = MaterialTheme.colorScheme.outline
) {
    Canvas(
        modifier = modifier
    ) {
        val width = size.width * (1 / 2f)

        drawCircle(
            color = color,
            radius = width * (2 / 3f),
            center = center
        )
    }
}