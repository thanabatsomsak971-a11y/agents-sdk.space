package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AgentCyan
import com.example.ui.theme.FirebaseAmber

@Composable
fun AudioWaveVisualizer(
    isSpeaking: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 18,
    maxHeight: Dp = 38.dp,
    minHeight: Dp = 6.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave_transition")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )

    val pulse3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, delayMillis = 100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val scaleFactor = if (isSpeaking) {
                when (i % 3) {
                    0 -> pulse1
                    1 -> pulse2
                    else -> pulse3
                } * (0.5f + (0.5f * kotlin.math.sin(i * 0.45).toFloat().coerceIn(0.2f, 1f)))
            } else {
                0.15f
            }

            val currentHeight = minHeight + (maxHeight - minHeight) * scaleFactor

            val brush = Brush.verticalGradient(
                colors = listOf(
                    FirebaseAmber,
                    AgentCyan
                )
            )

            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(currentHeight)
                    .background(
                        brush = if (isSpeaking) brush else Brush.verticalGradient(listOf(Color(0xFF334155), Color(0xFF1E293B))),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
