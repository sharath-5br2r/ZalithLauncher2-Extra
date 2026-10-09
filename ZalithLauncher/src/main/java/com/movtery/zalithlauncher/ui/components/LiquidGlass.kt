/*
 * Aerix Liquid Glass material layer.
 *
 * A lightweight Compose treatment for translucent surfaces: a cached spectral sheen,
 * a refractive rim, and restrained elevation. It deliberately does not blur the
 * whole screen, so the material remains usable with backgroundBlur=0.
 */
package com.movtery.zalithlauncher.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface

/**
 * Adds a restrained sheen and refractive rim so a translucent surface reads as glass
 * rather than a flat semi-transparent card. The draw cache keeps brushes and outlines
 * out of the per-frame path used by scrolling surfaces.
 *
 * Set [clipContent] to false when an ancestor already clips to [shape].
 */
fun Modifier.liquidGlass(
    shape: Shape,
    tint: Color = AerixSurface.glassTint,
    strength: Float = 1f,
    elevation: Dp = AerixMetrics.glassSurfaceElevation,
    clipContent: Boolean = true
): Modifier {
    val intensity = strength.coerceIn(0f, 1f)
    val shadowed = this.shadow(
        elevation = elevation * intensity,
        shape = shape,
        clip = false,
        ambientColor = AerixSurface.glassShadow,
        spotColor = AerixSurface.glassShadow
    )
    val glassModifier = if (clipContent) shadowed.clip(shape) else shadowed

    return glassModifier.drawWithCache {
        val width = size.width.coerceAtLeast(1f)
        val height = size.height.coerceAtLeast(1f)
        val sheen = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.15f * intensity),
                tint.copy(alpha = 0.10f * intensity),
                AerixSurface.glassBlue.copy(alpha = 0.075f * intensity),
                AerixSurface.glassViolet.copy(alpha = 0.045f * intensity),
                Color.Transparent
            ),
            start = Offset.Zero,
            end = Offset(width, height)
        )
        val rim = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.58f * intensity),
                AerixSurface.glassBlue.copy(alpha = 0.30f * intensity),
                AerixSurface.glassViolet.copy(alpha = 0.22f * intensity),
                Color.White.copy(alpha = 0.42f * intensity)
            ),
            start = Offset.Zero,
            end = Offset(width, height)
        )
        val outline = shape.createOutline(size, layoutDirection, this)
        val rimWidth = AerixSpacing.hairline.toPx() * intensity

        onDrawWithContent {
            drawRect(brush = sheen)
            drawContent()
            if (rimWidth > 0f) {
                drawOutline(
                    outline = outline,
                    brush = rim,
                    style = Stroke(width = rimWidth)
                )
            }
        }
    }
}
