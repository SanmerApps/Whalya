package dev.sanmer.docker.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun AnimatedIcon(
    isActive: Boolean,
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedValue by infiniteTransition.animateValue(
        initialValue = if (isActive) 0.5f else 1f,
        targetValue = 1f,
        typeConverter = Float.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Icon(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier
            .alpha(animatedValue)
            .scale(animatedValue)
    )
}