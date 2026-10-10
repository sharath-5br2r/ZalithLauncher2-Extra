/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 * Copyright (C) 2026 Aerix Launcher contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.ui.theme.AERIX_COMPACT_HEIGHT_THRESHOLD_DP
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.ui.components.liquidGlass
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.Account
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.ui.screens.content.elements.PlayerFace
import com.movtery.zalithlauncher.ui.theme.MiraiThemeManager

/** Primary destinations kept visible while nested launcher screens are open. */
enum class LauncherSection {
    HOME,
    LIBRARY,
    DISCOVER,
    WALLPAPERS,
    SETTINGS,
    ACCOUNTS
}

private val AerixRailDivider = AerixSurface.borderSoft

@Composable
fun MiraiNavigationRail(
    selectedSection: LauncherSection?,
    onNavigate: (LauncherSection) -> Unit,
    onCreateInstance: () -> Unit,
    onAccountClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val activeAccent = MiraiThemeManager.currentAccent()
    //横屏手机等矮屏：导航栏收紧，保证所有入口都露得出来
    val compactRail = LocalConfiguration.current.screenHeightDp < AERIX_COMPACT_HEIGHT_THRESHOLD_DP
    val expandedRail = LocalConfiguration.current.screenWidthDp >= 900 && !compactRail

    Row(modifier = modifier.fillMaxHeight()) {
        Surface(
            modifier = Modifier
                .width(if (expandedRail) AerixMetrics.expandedNavigationRailWidth else AerixMetrics.navigationRailWidth)
                .fillMaxHeight()
                .padding(horizontal = AerixSpacing.xs, vertical = AerixSpacing.smPlus)
                .liquidGlass(
                    shape = CircleShape,
                    tint = AerixSurface.glassTint,
                    strength = 0.94f,
                    elevation = AerixMetrics.glassRailElevation
                ),
            shape = CircleShape,
            color = Color.Transparent,
            contentColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
                    .padding(vertical = if (compactRail) AerixSpacing.xs else AerixSpacing.smPlus),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    if (compactRail) AerixSpacing.xs else AerixSpacing.sm
                )
            ) {
                // Aerix monogram and wordmark establish a persistent brand anchor.
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(AerixRadii.panelSmall))
                        .clickable(role = Role.Button) { onNavigate(LauncherSection.HOME) }
                        .semantics { contentDescription = "Aerix Home" }
                        .padding(vertical = AerixSpacing.xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (compactRail) 32.dp else 38.dp)
                            .clip(RoundedCornerShape(AerixRadii.control))
                            .background(activeAccent.copy(alpha = 0.12f))
                            .liquidGlass(
                                shape = RoundedCornerShape(AerixRadii.control),
                                tint = activeAccent,
                                strength = 0.82f,
                                elevation = AerixMetrics.glassSubtleElevation
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = "Zalith",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(if (compactRail) 20.dp else 24.dp)
                        )
                    }
                    if (expandedRail) {
                        androidx.compose.material3.Text(
                            text = "ZALITH",
                            fontSize = 8.sp,
                            letterSpacing = 1.1.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold,
                            color = AerixSurface.textSecondary
                        )
                    }
                }

                Spacer(Modifier.height(if (compactRail) AerixSpacing.zero else AerixSpacing.xs))

                RailIconItem(
                    iconRes = R.drawable.ic_home_filled,
                    label = "Home",
                    selected = selectedSection == LauncherSection.HOME,
                    accentColor = activeAccent,
                    expanded = expandedRail,
                    compact = compactRail,
                    onClick = { onNavigate(LauncherSection.HOME) }
                )

                RailIconItem(
                    iconRes = R.drawable.ic_add,
                    label = "New Instance",
                    selected = false,
                    accentColor = activeAccent,
                    expanded = expandedRail,
                    compact = compactRail,
                    onClick = onCreateInstance
                )

                RailIconItem(
                    iconRes = R.drawable.ic_dashboard_filled,
                    label = "Library",
                    selected = selectedSection == LauncherSection.LIBRARY,
                    accentColor = activeAccent,
                    expanded = expandedRail,
                    compact = compactRail,
                    onClick = { onNavigate(LauncherSection.LIBRARY) }
                )

                RailIconItem(
                    iconRes = R.drawable.ic_public,
                    label = "Discover",
                    selected = selectedSection == LauncherSection.DISCOVER,
                    accentColor = activeAccent,
                    expanded = expandedRail,
                    compact = compactRail,
                    onClick = { onNavigate(LauncherSection.DISCOVER) }
                )

                RailIconItem(
                    iconRes = R.drawable.ic_format_paint_outlined,
                    label = "Wallpapers",
                    selected = selectedSection == LauncherSection.WALLPAPERS,
                    accentColor = activeAccent,
                    expanded = expandedRail,
                    compact = compactRail,
                    onClick = { onNavigate(LauncherSection.WALLPAPERS) }
                )

                HorizontalDivider(
                    modifier = Modifier
                        .width(28.dp)
                        .padding(vertical = AerixSpacing.xxs),
                    color = AerixRailDivider
                )

                RailIconItem(
                    iconRes = R.drawable.ic_settings_filled,
                    label = "Settings",
                    selected = selectedSection == LauncherSection.SETTINGS,
                    accentColor = activeAccent,
                    expanded = expandedRail,
                    compact = compactRail,
                    onClick = { onNavigate(LauncherSection.SETTINGS) }
                )

                AccountAvatarRailButton(
                    account = account,
                    selected = selectedSection == LauncherSection.ACCOUNTS,
                    expanded = expandedRail,
                    compact = compactRail,
                    onClick = onAccountClick
                )
            }
        }
    }
}

@Composable
private fun RailIconItem(
    iconRes: Int,
    label: String,
    selected: Boolean,
    accentColor: Color,
    expanded: Boolean,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    //选中项使用当前主题的强调色；切换主题时图标、底色与描边一起更新。
    val selectedContentColor = MaterialTheme.colorScheme.onPrimary
    val containerColor by animateColorAsState(
        targetValue = if (selected) accentColor else AerixSurface.panel,
        animationSpec = tween(140),
        label = "railContainerColor"
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) selectedContentColor else AerixSurface.textSecondary,
        animationSpec = tween(140),
        label = "railIconTint"
    )

    val itemWidth = when {
        expanded -> 64.dp
        compact -> 36.dp
        else -> 44.dp
    }
    val itemHeight = when {
        expanded -> 54.dp
        compact -> 36.dp
        else -> 42.dp
    }

    val itemShape = RoundedCornerShape(AerixRadii.control)
    Box(
        modifier = Modifier
            .size(width = itemWidth, height = itemHeight)
            .clip(itemShape)
            .background(containerColor)
            .border(
                BorderStroke(
                    AerixSpacing.hairline,
                    if (selected) accentColor else AerixSurface.borderSoft
                ),
                itemShape
            )
            .clickable(role = Role.Tab, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center
    ) {
        if (expanded) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
                androidx.compose.material3.Text(
                    text = label,
                    color = if (selected) iconTint else AerixSurface.textSecondary,
                    fontSize = 8.sp,
                    lineHeight = 9.sp,
                    fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Medium,
                    maxLines = 1
                )
            }
        } else {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(if (compact) 18.dp else 21.dp)
            )
        }
    }
}

@Composable
private fun AccountAvatarRailButton(
    account: Account?,
    selected: Boolean,
    expanded: Boolean,
    compact: Boolean = false,
    onClick: () -> Unit
) {
    val activeAccent = MiraiThemeManager.currentAccent()
    val ringColor by animateColorAsState(
        targetValue = if (selected) activeAccent else AerixSurface.borderSoft,
        animationSpec = tween(150),
        label = "accountAvatarRing"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(AerixRadii.panelSmall))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = account?.username ?: "Accounts" }
            .padding(horizontal = AerixSpacing.xs, vertical = AerixSpacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 32.dp else 36.dp)
                .clip(CircleShape)
                .background(AerixSurface.panel)
                .liquidGlass(
                    shape = CircleShape,
                    tint = activeAccent,
                    strength = if (selected) 0.82f else 0.34f,
                    elevation = AerixMetrics.glassSelectedElevation
                )
                .border(
                    width = if (selected) 2.dp else AerixSpacing.hairline,
                    color = ringColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            PlayerFace(
                modifier = Modifier
                    .size(if (compact) 26.dp else 30.dp)
                    .clip(CircleShape),
                account = account
            )
        }
        if (expanded) {
            androidx.compose.material3.Text(
                text = "Profile",
                color = if (selected) activeAccent else AerixSurface.textSecondary,
                fontSize = 8.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}
