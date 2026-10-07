package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.data.model.WeatherCodeMapper
import com.example.data.model.WeatherIconType
import kotlin.random.Random

@Composable
fun AtmosphereBackground(
    weatherCode: Int,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val condition = remember(weatherCode, isDay) {
        WeatherCodeMapper.map(weatherCode, isDay)
    }

    val (topColorTarget, midColorTarget, bottomColorTarget) = when {
        condition.isThunderstorm -> Triple(
            Color(0xFF1E1035),
            Color(0xFF1E1B4B),
            Color(0xFF0F172A)
        )
        condition.isRaining -> Triple(
            Color(0xFF0C243B),
            Color(0xFF16324F),
            Color(0xFF0F172A)
        )
        condition.isSnowing -> Triple(
            Color(0xFF1A2E40),
            Color(0xFF2C3E50),
            Color(0xFF111827)
        )
        condition.isCloudy -> if (isDay) {
            Triple(
                Color(0xFF1E3A5F),
                Color(0xFF334E68),
                Color(0xFF1F2937)
            )
        } else {
            Triple(
                Color(0xFF0F172A),
                Color(0xFF1E293B),
                Color(0xFF0B1120)
            )
        }
        else -> if (isDay) {
            // Bright clear sky
            Triple(
                Color(0xFF0284C7), // Sky Blue
                Color(0xFF0EA5E9), // Light sky blue
                Color(0xFF0369A1)  // Ocean deep
            )
        } else {
            // Starry night
            Triple(
                Color(0xFF0B0F19),
                Color(0xFF111827),
                Color(0xFF030712)
            )
        }
    }

    val topColor by animateColorAsState(
        targetValue = topColorTarget,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "topColor"
    )
    val midColor by animateColorAsState(
        targetValue = midColorTarget,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "midColor"
    )
    val bottomColor by animateColorAsState(
        targetValue = bottomColorTarget,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "bottomColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "weatherAnim")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animOffset"
    )

    // Precompute random particles
    val particles = remember {
        List(40) {
            Triple(
                Random.nextFloat(), // x ratio
                Random.nextFloat(), // y ratio
                Random.nextFloat() * 0.7f + 0.3f // scale / alpha
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(topColor, midColor, bottomColor)
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            if (condition.isRaining) {
                // Rain streaks
                val streakLength = 32f
                val streakAngleX = -6f
                particles.forEach { (xRatio, yRatio, alpha) ->
                    val x = (xRatio * width + animOffset * 40f) % width
                    val y = ((yRatio + animOffset) % 1f) * height
                    drawLine(
                        color = Color.White.copy(alpha = 0.25f * alpha),
                        start = Offset(x, y),
                        end = Offset(x + streakAngleX, y + streakLength),
                        strokeWidth = 2.2f
                    )
                }
            } else if (condition.isSnowing) {
                // Snowflakes
                particles.forEach { (xRatio, yRatio, alpha) ->
                    val x = (xRatio * width + kotlin.math.sin((animOffset + yRatio) * 6.28) * 15f).toFloat() % width
                    val y = ((yRatio + animOffset * 0.5f) % 1f) * height
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f * alpha),
                        radius = 3.5f * alpha,
                        center = Offset(x, y)
                    )
                }
            } else if (!isDay && condition.isClear) {
                // Twinkling stars at night
                particles.forEachIndexed { index, (xRatio, yRatio, alpha) ->
                    val twinkle = ((kotlin.math.sin((animOffset * 6.28) + (index * 0.8)) + 1f) / 2f).toFloat()
                    val x = xRatio * width
                    val y = (yRatio * height) * 0.75f // Top 75% of sky
                    drawCircle(
                        color = Color.White.copy(alpha = (0.2f + 0.6f * twinkle) * alpha),
                        radius = 1.8f * alpha,
                        center = Offset(x, y)
                    )
                }
            } else if (isDay && condition.isClear) {
                // Ambient solar radiance
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x33FBBF24),
                            Color(0x11FBBF24),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.85f, height * 0.15f),
                        radius = width * 0.7f
                    ),
                    radius = width * 0.7f,
                    center = Offset(width * 0.85f, height * 0.15f)
                )
            }
        }

        content()
    }
}
