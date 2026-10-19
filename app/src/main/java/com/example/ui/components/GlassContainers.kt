package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Minimalist background surface.
 */
@Composable
fun GlassMorphicBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

/**
 * Minimalist Panel Container.
 */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    elevation: Dp = 0.dp,
    borderWidth: Dp = 0.dp,
    containerColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor ?: MaterialTheme.colorScheme.surface,
        tonalElevation = elevation
    ) {
        Box {
            content()
        }
    }
}

/**
 * Clickable Minimalist Card container.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RoundedCornerShape(8.dp),
    elevation: Dp = 0.dp,
    borderWidth: Dp = 0.dp,
    containerColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        ),
        shape = shape,
        color = containerColor ?: MaterialTheme.colorScheme.surface,
        tonalElevation = elevation
    ) {
        Box {
            content()
        }
    }
}

/**
 * Minimalist Badge / Pill.
 */
@Composable
fun GlassBadge(
    modifier: Modifier = Modifier,
    tintColor: Color = MaterialTheme.colorScheme.primary,
    shape: Shape = RoundedCornerShape(4.dp),
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = tintColor.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

/**
 * Utility for border stroke compatibility.
 */
@Composable
fun glassBorderStroke(width: Dp = 0.dp): BorderStroke {
    return BorderStroke(width, Color.Transparent)
}

/**
 * Utility for surface brush compatibility.
 */
@Composable
fun glassSurfaceBrush() = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.surface)
