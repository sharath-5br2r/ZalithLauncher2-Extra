/*
 * Aerix Launcher shared navigation and page-heading components.
 */
package com.movtery.zalithlauncher.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.ui.theme.MiraiThemeManager
import com.movtery.zalithlauncher.ui.AndroidStringText
import com.movtery.zalithlauncher.ui.androidText

/** A horizontally scrollable, consistently styled tab track for every launcher page. */
@Composable
fun AerixPillTabRow(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = AerixSpacing.xs,
        vertical = AerixSpacing.xs
    ),
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .liquidGlass(
                shape = CircleShape,
                tint = AerixSurface.glassTint,
                strength = 0.72f,
                elevation = AerixMetrics.glassSelectedElevation
            )
            .selectableGroup()
            .horizontalScroll(rememberScrollState())
            .padding(contentPadding),
        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

/** One accessible pill item; its selected state is conveyed to both sighted and assistive users. */
@Composable
fun AerixPillTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val activeAccent = MiraiThemeManager.currentAccent()
    val outline by animateColorAsState(
        targetValue = if (selected) AerixSurface.borderHighlight else Color.Transparent,
        animationSpec = tween(durationMillis = 160),
        label = "aerixTabOutline"
    )
    val contentColor = if (selected) activeAccent else AerixSurface.textSecondary
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .defaultMinSize(minHeight = AerixMetrics.pillTabHeight)
            .then(
                if (selected) {
                    Modifier.liquidGlass(
                        shape = CircleShape,
                        tint = activeAccent,
                        strength = 0.95f,
                        elevation = AerixMetrics.glassSelectedElevation
                    )
                } else Modifier
            )
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = CircleShape,
        color = Color.Transparent,
        contentColor = if (enabled) contentColor else contentColor.copy(alpha = 0.48f),
        border = BorderStroke(AerixSpacing.hairline, outline),
        shadowElevation = if (selected) AerixSpacing.xs else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AerixSpacing.md, vertical = AerixSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

/**
 * Shared page-heading typography and spacing. All main and nested routes use this
 * component through the central launcher top bar.
 */
@Composable
fun AerixSectionHeader(
    title: AndroidStringText? = null,
    modifier: Modifier = Modifier,
    titleContent: (@Composable (TextStyle) -> Unit)? = null,
    subtitle: String? = null,
    maxLines: Int = 1,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.15).sp,
        color = AerixSurface.textPrimary
    )
) {
    Column(
        modifier = modifier.heightIn(min = AerixMetrics.pageHeaderHeight),
        verticalArrangement = Arrangement.Center
    ) {
        if (titleContent != null) {
            CompositionLocalProvider(LocalContentColor provides titleStyle.color) {
                titleContent(titleStyle)
            }
        } else if (title != null) {
            AndroidStringText(
                text = title,
                maxLines = maxLines,
                softWrap = false,
                style = titleStyle
            )
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AerixSurface.textSecondary
            )
        }
    }
}

@Composable
fun AerixSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    maxLines: Int = 1
) {
    AerixSectionHeader(
        title = androidText(title),
        modifier = modifier,
        subtitle = subtitle,
        maxLines = maxLines
    )
}
