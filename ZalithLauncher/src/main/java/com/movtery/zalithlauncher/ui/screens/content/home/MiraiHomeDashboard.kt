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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 */

package com.movtery.zalithlauncher.ui.screens.content.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.coroutine.TaskSystem
import com.movtery.zalithlauncher.game.optimization.JvmGcAutoTunerDialog
import com.movtery.zalithlauncher.game.optimization.MobileFpsBoosterDialog
import com.movtery.zalithlauncher.game.optimization.SmartCrashDoctorDialog
import com.movtery.zalithlauncher.game.renderer.RendererPicker
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.components.liquidGlass
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.ui.theme.MiraiThemeManager

private val ModrinthEmerald = AerixSurface.modrinthBrand

private val DefaultAvailableRenderers = setOf(
    RendererPicker.LTW,
    RendererPicker.LTW_LEGACY,
    RendererPicker.VGPU,
    RendererPicker.VGPU_1368,
    RendererPicker.ZINK,
    RendererPicker.VIRGL,
    RendererPicker.GL4ES
)

fun resolveRendererShortLabel(version: Version?): String {
    val mcVer = version?.getVersionInfo()?.minecraftVersion.orEmpty()
    val manual = version?.getRenderer().orEmpty()
    val choice = RendererPicker.pick(mcVer, manual, DefaultAvailableRenderers)
    return when {
        choice.identifier == RendererPicker.VGPU -> "vgpu"
        choice.identifier == RendererPicker.VGPU_1368 -> "VGPU 1.3.6β"
        choice.identifier == RendererPicker.LTW_LEGACY || choice.identifier.contains("Legacy", ignoreCase = true) -> "LTW Legacy"
        choice.identifier == RendererPicker.LTW || choice.identifier.contains("LTW", ignoreCase = true) -> "LTW"
        choice.identifier == RendererPicker.ZINK || choice.identifier.contains("Zink", ignoreCase = true) -> "Kopper Zink"
        choice.identifier == RendererPicker.VIRGL || choice.identifier.contains("VirGL", ignoreCase = true) -> "VirGL"
        choice.identifier == RendererPicker.GL4ES || choice.identifier.contains("GL4ES", ignoreCase = true) -> "GL4ES"
        else -> "LTW"
    }
}

fun resolveRendererBadgeDetail(version: Version?): String {
    val mcVer = version?.getVersionInfo()?.minecraftVersion.orEmpty()
    val manual = version?.getRenderer().orEmpty()
    val choice = RendererPicker.pick(mcVer, manual, DefaultAvailableRenderers)
    return when {
        choice.identifier == RendererPicker.VGPU -> "vgpu - (up to 1.16.5, fast)"
        choice.identifier == RendererPicker.VGPU_1368 -> "VGPU 1.3.6β"
        choice.identifier == RendererPicker.LTW_LEGACY || choice.identifier.contains("Legacy", ignoreCase = true) -> "LTW Legacy 1.8–1.16.5"
        choice.identifier == RendererPicker.LTW || choice.identifier.contains("LTW", ignoreCase = true) -> "LTW 1.17+"
        choice.identifier == RendererPicker.ZINK || choice.identifier.contains("Zink", ignoreCase = true) -> "Kopper Zink"
        choice.identifier == RendererPicker.VIRGL || choice.identifier.contains("VirGL", ignoreCase = true) -> "VirGL"
        choice.identifier == RendererPicker.GL4ES || choice.identifier.contains("GL4ES", ignoreCase = true) -> "GL4ES"
        else -> "LTW 1.17+"
    }
}

/** A glass-first launcher landing page with responsive, feature-complete navigation. */
@Composable
fun MiraiHomeDashboard(
    onLaunchVersion: (Version) -> Unit,
    onOpenVersionSettings: (Version) -> Unit,
    onExploreContent: () -> Unit,
    onCreateInstance: () -> Unit,
    onManageVersions: () -> Unit,
    onOpenFileManager: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()
    val currentVersion by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val tasks by TaskSystem.tasksFlow.collectAsStateWithLifecycle()
    val activeAccent = MiraiThemeManager.currentAccent()
    val selectedVersion = currentVersion ?: versions.firstOrNull()
    val recentVersions = remember(versions, selectedVersion) {
        versions.filterNot { it == selectedVersion }.take(8).ifEmpty { versions.take(8) }
    }

    var showFpsBooster by remember { mutableStateOf(false) }
    var showJreGcTuner by remember { mutableStateOf(false) }
    var showCrashDoctor by remember { mutableStateOf(false) }

    if (showFpsBooster) {
        MobileFpsBoosterDialog(version = selectedVersion, onDismiss = { showFpsBooster = false })
    }
    if (showJreGcTuner) {
        JvmGcAutoTunerDialog(version = selectedVersion, onDismiss = { showJreGcTuner = false })
    }
    if (showCrashDoctor) {
        SmartCrashDoctorDialog(version = selectedVersion, onDismiss = { showCrashDoctor = false })
    }

    val openTasks = {
        AllSettings.launcherTaskMenuExpanded.save(!AllSettings.launcherTaskMenuExpanded.state)
    }
    val launchSelected: () -> Unit = {
        selectedVersion?.let { version ->
            VersionsManager.saveVersion(version)
            onLaunchVersion(version)
        } ?: onCreateInstance()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = AerixSpacing.mdPlus,
                end = AerixSpacing.smCompact,
                top = AerixSpacing.smPlus
            )
    ) {
        if (maxWidth >= 720.dp && maxWidth > maxHeight) {
            WideHomeDashboard(
                selectedVersion = selectedVersion,
                recentVersions = recentVersions,
                versionCount = versions.size,
                activeAccent = activeAccent,
                taskCount = tasks.size,
                onLaunchSelected = launchSelected,
                onLaunchVersion = onLaunchVersion,
                onCreateInstance = onCreateInstance,
                onExploreContent = onExploreContent,
                onManageVersions = onManageVersions,
                onOpenVersionSettings = onOpenVersionSettings,
                onOpenFileManager = onOpenFileManager,
                onOpenTasks = openTasks,
                onBoostFps = { showFpsBooster = true },
                onTuneGc = { showJreGcTuner = true },
                onCrashDoctor = { showCrashDoctor = true }
            )
        } else {
            CompactHomeDashboard(
                selectedVersion = selectedVersion,
                recentVersions = recentVersions,
                versionCount = versions.size,
                activeAccent = activeAccent,
                taskCount = tasks.size,
                onLaunchSelected = launchSelected,
                onLaunchVersion = onLaunchVersion,
                onCreateInstance = onCreateInstance,
                onExploreContent = onExploreContent,
                onManageVersions = onManageVersions,
                onOpenVersionSettings = onOpenVersionSettings,
                onOpenFileManager = onOpenFileManager,
                onOpenTasks = openTasks,
                onBoostFps = { showFpsBooster = true },
                onTuneGc = { showJreGcTuner = true },
                onCrashDoctor = { showCrashDoctor = true }
            )
        }
    }
}

@Composable
private fun WideHomeDashboard(
    selectedVersion: Version?,
    recentVersions: List<Version>,
    versionCount: Int,
    activeAccent: Color,
    taskCount: Int,
    onLaunchSelected: () -> Unit,
    onLaunchVersion: (Version) -> Unit,
    onCreateInstance: () -> Unit,
    onExploreContent: () -> Unit,
    onManageVersions: () -> Unit,
    onOpenVersionSettings: (Version) -> Unit,
    onOpenFileManager: () -> Unit,
    onOpenTasks: () -> Unit,
    onBoostFps: () -> Unit,
    onTuneGc: () -> Unit,
    onCrashDoctor: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.57f),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
        ) {
            LiquidHomeHero(
                selectedVersion = selectedVersion,
                versionCount = versionCount,
                activeAccent = activeAccent,
                onLaunch = onLaunchSelected,
                onCreateInstance = onCreateInstance,
                onManageVersions = onManageVersions,
                modifier = Modifier
                    .weight(1.55f)
                    .fillMaxHeight()
            )
            Column(
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
            ) {
                ActiveWorldPanel(
                    version = selectedVersion,
                    activeAccent = activeAccent,
                    onLaunch = onLaunchSelected,
                    onSettings = {
                        selectedVersion?.let { version ->
                            VersionsManager.saveVersion(version)
                            onOpenVersionSettings(version)
                        }
                    },
                    onCreateInstance = onCreateInstance,
                    modifier = Modifier.weight(1f)
                )
                TaskSummaryPanel(
                    taskCount = taskCount,
                    activeAccent = activeAccent,
                    onClick = onOpenTasks,
                    modifier = Modifier.height(74.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
        ) {
            QuickActionTile(
                iconRes = R.drawable.ic_add,
                title = "Create instance",
                subtitle = "A fresh game profile",
                accent = activeAccent,
                onClick = onCreateInstance,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                iconRes = R.drawable.ic_search,
                title = "Explore content",
                subtitle = "Modpacks, mods & more",
                accent = AerixSurface.glassBlue,
                onClick = onExploreContent,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                iconRes = R.drawable.ic_dashboard_filled,
                title = "Your library",
                subtitle = "$versionCount installed profiles",
                accent = AerixSurface.glassViolet,
                onClick = onManageVersions,
                modifier = Modifier.weight(1f)
            )
            QuickActionTile(
                iconRes = R.drawable.ic_folder_outlined,
                title = "File space",
                subtitle = "Worlds, logs & exports",
                accent = AerixSurface.glassRose,
                onClick = onOpenFileManager,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.43f),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
        ) {
            RecentWorldsPanel(
                versions = recentVersions,
                selectedVersion = selectedVersion,
                activeAccent = activeAccent,
                onSelect = { version -> VersionsManager.saveVersion(version) },
                onSettings = { version ->
                    VersionsManager.saveVersion(version)
                    onOpenVersionSettings(version)
                },
                onLaunchVersion = onLaunchVersion,
                onViewAll = onManageVersions,
                modifier = Modifier
                    .weight(1.7f)
                    .fillMaxHeight()
            )
            QuickToolsPanel(
                activeAccent = activeAccent,
                onBoostFps = onBoostFps,
                onTuneGc = onTuneGc,
                onCrashDoctor = onCrashDoctor,
                onOpenFiles = onOpenFileManager,
                modifier = Modifier
                    .weight(0.72f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun CompactHomeDashboard(
    selectedVersion: Version?,
    recentVersions: List<Version>,
    versionCount: Int,
    activeAccent: Color,
    taskCount: Int,
    onLaunchSelected: () -> Unit,
    onLaunchVersion: (Version) -> Unit,
    onCreateInstance: () -> Unit,
    onExploreContent: () -> Unit,
    onManageVersions: () -> Unit,
    onOpenVersionSettings: (Version) -> Unit,
    onOpenFileManager: () -> Unit,
    onOpenTasks: () -> Unit,
    onBoostFps: () -> Unit,
    onTuneGc: () -> Unit,
    onCrashDoctor: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = AerixSpacing.xxl),
        verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)
    ) {
        item {
            LiquidHomeHero(
                selectedVersion = selectedVersion,
                versionCount = versionCount,
                activeAccent = activeAccent,
                onLaunch = onLaunchSelected,
                onCreateInstance = onCreateInstance,
                onManageVersions = onManageVersions,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(312.dp)
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)) {
                HomeSectionLabel(title = "YOUR SPACE", subtitle = "Everything you need, close at hand")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
                ) {
                    QuickActionTile(
                        iconRes = R.drawable.ic_add,
                        title = "Create instance",
                        subtitle = "New profile",
                        accent = activeAccent,
                        onClick = onCreateInstance,
                        modifier = Modifier.width(198.dp).height(78.dp)
                    )
                    QuickActionTile(
                        iconRes = R.drawable.ic_search,
                        title = "Explore content",
                        subtitle = "Mods & modpacks",
                        accent = AerixSurface.glassBlue,
                        onClick = onExploreContent,
                        modifier = Modifier.width(198.dp).height(78.dp)
                    )
                    QuickActionTile(
                        iconRes = R.drawable.ic_dashboard_filled,
                        title = "Your library",
                        subtitle = "$versionCount profiles",
                        accent = AerixSurface.glassViolet,
                        onClick = onManageVersions,
                        modifier = Modifier.width(198.dp).height(78.dp)
                    )
                    QuickActionTile(
                        iconRes = R.drawable.ic_folder_outlined,
                        title = "File space",
                        subtitle = "Worlds & exports",
                        accent = AerixSurface.glassRose,
                        onClick = onOpenFileManager,
                        modifier = Modifier.width(198.dp).height(78.dp)
                    )
                }
            }
        }
        item {
            ActiveWorldPanel(
                version = selectedVersion,
                activeAccent = activeAccent,
                onLaunch = onLaunchSelected,
                onSettings = {
                    selectedVersion?.let { version ->
                        VersionsManager.saveVersion(version)
                        onOpenVersionSettings(version)
                    }
                },
                onCreateInstance = onCreateInstance,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            TaskSummaryPanel(
                taskCount = taskCount,
                activeAccent = activeAccent,
                onClick = onOpenTasks,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HomeSectionLabel(
                    title = "RECENT WORLDS",
                    subtitle = "A quick return to your last sessions",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "VIEW ALL",
                    color = activeAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onManageVersions)
                        .padding(horizontal = AerixSpacing.smPlus, vertical = AerixSpacing.sm)
                )
            }
        }
        if (recentVersions.isEmpty()) {
            item {
                QuietEmptyState(
                    title = "Your next world starts here",
                    subtitle = "Create a profile or explore a ready-made pack.",
                    accent = activeAccent,
                    onCreateInstance = onCreateInstance,
                    onExploreContent = onExploreContent
                )
            }
        } else {
            items(recentVersions, key = { it.getVersionName() }) { version ->
                RecentWorldRow(
                    version = version,
                    isSelected = version == selectedVersion,
                    accent = activeAccent,
                    onSelect = { VersionsManager.saveVersion(version) },
                    onLaunch = {
                        VersionsManager.saveVersion(version)
                        onLaunchVersion(version)
                    },
                    onSettings = {
                        VersionsManager.saveVersion(version)
                        onOpenVersionSettings(version)
                    }
                )
            }
        }
        item {
            QuickToolsPanel(
                activeAccent = activeAccent,
                onBoostFps = onBoostFps,
                onTuneGc = onTuneGc,
                onCrashDoctor = onCrashDoctor,
                onOpenFiles = onOpenFileManager,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun LiquidHomeHero(
    selectedVersion: Version?,
    versionCount: Int,
    activeAccent: Color,
    onLaunch: () -> Unit,
    onCreateInstance: () -> Unit,
    onManageVersions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AerixRadii.hero)
    Box(
        modifier = modifier
            .clip(shape)
            .border(BorderStroke(AerixSpacing.hairline, AerixSurface.borderHighlight.copy(alpha = 0.62f)), shape)
    ) {
        Image(
            painter = painterResource(R.drawable.mirai_hero_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val width = size.width.coerceAtLeast(1f)
                    val height = size.height.coerceAtLeast(1f)
                    val longEdge = maxOf(width, height)
                    val center = androidx.compose.ui.geometry.Offset(width * 0.92f, height * 0.1f)
                    val inset = 30.dp.toPx()
                    val rimWidth = 1.dp.toPx()
                    val wash = Brush.horizontalGradient(
                        colors = listOf(
                            AerixSurface.canvas.copy(alpha = 0.90f),
                            AerixSurface.canvas.copy(alpha = 0.82f),
                            AerixSurface.canvas.copy(alpha = 0.30f),
                            AerixSurface.canvas.copy(alpha = 0.07f)
                        ),
                        startX = 0f,
                        endX = width
                    )
                    val lens = Brush.radialGradient(
                        colors = listOf(
                            AerixSurface.glassBlue.copy(alpha = 0.36f),
                            AerixSurface.glassViolet.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = longEdge * 0.72f
                    )
                    onDrawBehind {
                        drawRect(brush = wash)
                        drawCircle(brush = lens, radius = longEdge * 0.72f, center = center)
                        drawLine(
                            color = Color.White.copy(alpha = 0.5f),
                            start = androidx.compose.ui.geometry.Offset(inset, rimWidth * 0.5f),
                            end = androidx.compose.ui.geometry.Offset(width - inset, rimWidth * 0.5f),
                            strokeWidth = rimWidth
                        )
                    }
                }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AerixSpacing.xxl, vertical = AerixSpacing.xl),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AerixSurface.success)
                )
                Text(
                    text = if (selectedVersion != null) "READY WHEN YOU ARE" else "YOUR NEXT WORLD STARTS HERE",
                    color = AerixSurface.textPrimary.copy(alpha = 0.88f),
                    fontSize = 10.sp,
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(0.76f),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                Text(
                    text = if (selectedVersion != null) "A world of\nyour own." else "Make room\nfor wonder.",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    lineHeight = 42.sp,
                    maxLines = 2
                )
                Text(
                    text = if (selectedVersion != null) {
                        "Your next chapter is only one touch away."
                    } else {
                        "Build a world, choose your style, and make it yours."
                    },
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = AerixSurface.textPrimary.copy(alpha = 0.78f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.padding(top = AerixSpacing.xs),
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeroMetaPill(
                        text = selectedVersion?.getVersionName() ?: "$versionCount profiles",
                        iconRes = R.drawable.ic_dashboard_filled
                    )
                    selectedVersion?.let { version ->
                        HeroMetaPill(
                            text = version.getVersionInfo()?.loaderInfo?.loader?.displayName ?: "Vanilla",
                            iconRes = R.drawable.ic_videogame_asset_outlined
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .height(46.dp)
                        .liquidGlass(
                            shape = CircleShape,
                            tint = activeAccent,
                            strength = 0.92f,
                            elevation = AerixMetrics.glassSelectedElevation
                        ),
                    shape = CircleShape,
                    color = activeAccent,
                    contentColor = AerixSurface.onAccent,
                    shadowElevation = 0.dp,
                    onClick = if (selectedVersion != null) onLaunch else onCreateInstance
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = AerixSpacing.lg),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                    ) {
                        Icon(
                            painter = painterResource(if (selectedVersion != null) R.drawable.ic_play_arrow_filled else R.drawable.ic_add),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (selectedVersion != null) "PLAY NOW" else "CREATE A WORLD",
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Text(
                    text = "OPEN LIBRARY  ↗",
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onManageVersions)
                        .padding(horizontal = AerixSpacing.md, vertical = AerixSpacing.smPlus)
                )
            }
        }
    }
}

@Composable
private fun HeroMetaPill(text: String, iconRes: Int) {
    Surface(
        shape = CircleShape,
        color = AerixSurface.panelRaised.copy(alpha = 0.54f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AerixSpacing.smPlus, vertical = AerixSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
        ) {
            Icon(painter = painterResource(iconRes), contentDescription = null, modifier = Modifier.size(13.dp))
            Text(text = text, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@Composable
private fun ActiveWorldPanel(
    version: Version?,
    activeAccent: Color,
    onLaunch: () -> Unit,
    onSettings: () -> Unit,
    onCreateInstance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AerixRadii.panel)
    Surface(
        modifier = modifier.liquidGlass(
            shape = shape,
            tint = activeAccent,
            strength = 0.82f,
            elevation = AerixMetrics.glassSubtleElevation
        ),
        shape = shape,
        color = Color.Transparent,
        contentColor = AerixSurface.textPrimary,
        border = BorderStroke(1.dp, AerixSurface.borderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AerixSpacing.mdPlus),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HomeSectionLabel(title = "ACTIVE PROFILE", subtitle = "Your selected game instance")
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(AerixRadii.card))
                        .background(AerixSurface.panelRaised)
                        .padding(AerixSpacing.xs),
                    contentAlignment = Alignment.Center
                ) {
                    if (version != null) {
                        VersionIconImage(version = version, modifier = Modifier.fillMaxSize())
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.ic_videogame_asset_outlined),
                            contentDescription = null,
                            tint = activeAccent,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
                ) {
                    Text(
                        text = version?.getVersionName() ?: "No world selected",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = version?.let {
                            val gameVersion = it.getVersionInfo()?.minecraftVersion ?: "Unknown"
                            val loader = it.getVersionInfo()?.loaderInfo?.loader?.displayName ?: "Vanilla"
                            "$loader  ·  Minecraft $gameVersion"
                        } ?: "Create your first profile to begin",
                        color = AerixSurface.textSecondary,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .liquidGlass(
                            shape = CircleShape,
                            tint = activeAccent,
                            strength = 0.9f,
                            elevation = AerixMetrics.glassSubtleElevation
                        ),
                    shape = CircleShape,
                    color = activeAccent,
                    contentColor = AerixSurface.onAccent,
                    onClick = if (version != null) onLaunch else onCreateInstance
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = AerixSpacing.md),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(if (version != null) R.drawable.ic_play_arrow_filled else R.drawable.ic_add),
                            contentDescription = null,
                            modifier = Modifier.size(17.dp)
                        )
                        Spacer(Modifier.width(AerixSpacing.xs))
                        Text(text = if (version != null) "Play" else "Create", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                IconButton(
                    onClick = onSettings,
                    enabled = version != null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AerixSurface.panelTrack)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings_filled),
                        contentDescription = "Profile settings",
                        tint = if (version != null) AerixSurface.textPrimary else AerixSurface.textMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskSummaryPanel(
    taskCount: Int,
    activeAccent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AerixRadii.panelSmall)
    Surface(
        modifier = modifier.liquidGlass(
            shape = shape,
            tint = if (taskCount > 0) activeAccent else AerixSurface.glassBlue,
            strength = 0.5f,
            elevation = AerixMetrics.glassSubtleElevation
        ),
        shape = shape,
        color = Color.Transparent,
        contentColor = AerixSurface.textPrimary,
        border = BorderStroke(1.dp, AerixSurface.borderSoft),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AerixSpacing.mdPlus),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (taskCount > 0) activeAccent.copy(alpha = 0.22f) else AerixSurface.panelTrack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_download_2_filled),
                    contentDescription = null,
                    tint = if (taskCount > 0) activeAccent else AerixSurface.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (taskCount == 0) "All clear" else "$taskCount active ${if (taskCount == 1) "task" else "tasks"}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = if (taskCount == 0) "Downloads and updates" else "Tap to see progress",
                    color = AerixSurface.textSecondary,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right_rounded),
                contentDescription = null,
                tint = AerixSurface.textSecondary,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Composable
private fun RecentWorldsPanel(
    versions: List<Version>,
    selectedVersion: Version?,
    activeAccent: Color,
    onSelect: (Version) -> Unit,
    onSettings: (Version) -> Unit,
    onLaunchVersion: (Version) -> Unit,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AerixRadii.panel)
    Surface(
        modifier = modifier.liquidGlass(
            shape = shape,
            tint = AerixSurface.glassBlue,
            strength = 0.55f,
            elevation = AerixMetrics.glassSubtleElevation
        ),
        shape = shape,
        color = Color.Transparent,
        contentColor = AerixSurface.textPrimary,
        border = BorderStroke(1.dp, AerixSurface.borderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AerixSpacing.mdPlus, vertical = AerixSpacing.smPlus),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HomeSectionLabel(
                    title = "RECENT WORLDS",
                    subtitle = "Pick up right where you left off",
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "ALL",
                    color = activeAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.7.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onViewAll)
                        .padding(horizontal = AerixSpacing.smPlus, vertical = AerixSpacing.sm)
                )
            }
            if (versions.isEmpty()) {
                Text(
                    text = "Your recent worlds will appear here.",
                    color = AerixSurface.textSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(vertical = AerixSpacing.sm)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.xs),
                    contentPadding = PaddingValues(bottom = AerixSpacing.xs)
                ) {
                    items(versions.take(5), key = { it.getVersionName() }) { version ->
                        RecentWorldRow(
                            version = version,
                            isSelected = version == selectedVersion,
                            accent = activeAccent,
                            onSelect = { onSelect(version) },
                            onLaunch = {
                                onSelect(version)
                                onLaunchVersion(version)
                            },
                            onSettings = { onSettings(version) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentWorldRow(
    version: Version,
    isSelected: Boolean,
    accent: Color,
    onSelect: () -> Unit,
    onLaunch: () -> Unit,
    onSettings: () -> Unit
) {
    val shape = RoundedCornerShape(AerixRadii.card)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = shape,
                tint = if (isSelected) accent else AerixSurface.glassTint,
                strength = if (isSelected) 0.56f else 0.24f,
                elevation = AerixSpacing.zero
            ),
        shape = shape,
        color = if (isSelected) accent.copy(alpha = 0.1f) else Color.Transparent,
        contentColor = AerixSurface.textPrimary,
        border = BorderStroke(
            1.dp,
            if (isSelected) accent.copy(alpha = 0.5f) else AerixSurface.borderSoft.copy(alpha = 0.55f)
        ),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AerixSpacing.smPlus, vertical = AerixSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
        ) {
            VersionIconImage(
                version = version,
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(AerixRadii.compact))
                    .background(AerixSurface.panelRaised)
                    .padding(AerixSpacing.xxs)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)
            ) {
                Text(
                    text = version.getVersionName(),
                    color = AerixSurface.textPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = version.getVersionInfo()?.let { info ->
                        "${info.loaderInfo?.loader?.displayName ?: "Vanilla"} · ${info.minecraftVersion ?: "Unknown"}"
                    } ?: "Installed instance",
                    color = AerixSurface.textSecondary,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onLaunch, modifier = Modifier.size(30.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_play_arrow_filled),
                    contentDescription = stringResource(R.string.main_launch_game),
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onSettings, modifier = Modifier.size(30.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings_filled),
                    contentDescription = "Instance settings",
                    tint = AerixSurface.textSecondary,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickActionTile(
    iconRes: Int,
    title: String,
    subtitle: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AerixRadii.panelSmall)
    Surface(
        modifier = modifier.liquidGlass(
            shape = shape,
            tint = accent,
            strength = 0.55f,
            elevation = AerixMetrics.glassSubtleElevation
        ),
        shape = shape,
        color = Color.Transparent,
        contentColor = AerixSurface.textPrimary,
        border = BorderStroke(1.dp, AerixSurface.borderSoft),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AerixSpacing.mdPlus, vertical = AerixSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.19f))
                    .border(1.dp, accent.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)
            ) {
                Text(
                    text = title,
                    color = AerixSurface.textPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = AerixSurface.textSecondary,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right_rounded),
                contentDescription = null,
                tint = AerixSurface.textMuted,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
private fun QuickToolsPanel(
    activeAccent: Color,
    onBoostFps: () -> Unit,
    onTuneGc: () -> Unit,
    onCrashDoctor: () -> Unit,
    onOpenFiles: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AerixRadii.panel)
    Surface(
        modifier = modifier.liquidGlass(
            shape = shape,
            tint = AerixSurface.glassViolet,
            strength = 0.5f,
            elevation = AerixMetrics.glassSubtleElevation
        ),
        shape = shape,
        color = Color.Transparent,
        contentColor = AerixSurface.textPrimary,
        border = BorderStroke(1.dp, AerixSurface.borderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AerixSpacing.md, vertical = AerixSpacing.sm),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            HomeToolAction("FPS tune-up", R.drawable.ic_rocket_launch_filled, activeAccent, onBoostFps)
            HomeToolAction("JRE & memory", R.drawable.ic_settings_filled, AerixSurface.glassBlue, onTuneGc)
            HomeToolAction("Crash doctor", R.drawable.ic_warning_outlined, AerixSurface.warning, onCrashDoctor)
            HomeToolAction("Open files", R.drawable.ic_folder_outlined, AerixSurface.glassRose, onOpenFiles)
        }
    }
}

@Composable
private fun HomeToolAction(label: String, iconRes: Int, accent: Color, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .liquidGlass(
                shape = CircleShape,
                tint = accent,
                strength = 0.42f,
                elevation = AerixSpacing.zero
            ),
        shape = CircleShape,
        color = Color.Transparent,
        contentColor = AerixSurface.textPrimary,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AerixSpacing.smPlus),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
        ) {
            Icon(painter = painterResource(iconRes), contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            Text(
                text = label,
                color = AerixSurface.textPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right_rounded),
                contentDescription = null,
                tint = AerixSurface.textMuted,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
private fun HomeSectionLabel(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            color = AerixSurface.textPrimary,
            fontSize = 10.sp,
            letterSpacing = 1.15.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = subtitle,
            color = AerixSurface.textSecondary,
            fontSize = 9.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun QuietEmptyState(
    title: String,
    subtitle: String,
    accent: Color,
    onCreateInstance: () -> Unit,
    onExploreContent: () -> Unit
) {
    val shape = RoundedCornerShape(AerixRadii.panel)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .liquidGlass(shape, tint = accent, strength = 0.64f, elevation = AerixMetrics.glassSubtleElevation),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, AerixSurface.borderSoft)
    ) {
        Column(
            modifier = Modifier.padding(AerixSpacing.xl),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
        ) {
            Text(title, color = AerixSurface.textPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = AerixSurface.textSecondary, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)) {
                Button(
                    onClick = onCreateInstance,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = AerixSurface.onAccent)
                ) { Text("Create instance", fontWeight = FontWeight.Bold) }
                Button(
                    onClick = onExploreContent,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = AerixSurface.panelRaised, contentColor = AerixSurface.textPrimary)
                ) { Text("Explore content", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
fun ModrinthMetaPill(
    text: String,
    highlighted: Boolean = false,
    backgroundColor: Color = if (highlighted) AerixSurface.accentContainer else AerixSurface.panelRaised,
    textColor: Color = if (highlighted) ModrinthEmerald else AerixSurface.textSecondary,
    borderColor: Color? = if (highlighted) ModrinthEmerald.copy(alpha = 0.45f) else null
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(AerixRadii.compact))
            .background(backgroundColor)
            .then(
                if (borderColor != null) {
                    Modifier.border(AerixSpacing.hairline, borderColor, RoundedCornerShape(AerixRadii.compact))
                } else Modifier
            )
            .padding(horizontal = AerixSpacing.smTight, vertical = AerixSpacing.xxs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1
        )
    }
}

@Composable
fun ModrinthCompactSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(AerixRadii.cardLarge))
            .background(AerixSurface.panelRaised)
            .border(AerixSpacing.hairline, AerixSurface.borderSoft, RoundedCornerShape(AerixRadii.cardLarge))
            .padding(horizontal = AerixSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = null,
            tint = AerixSurface.textSecondary,
            modifier = Modifier.size(16.dp)
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodySmall,
                    color = AerixSurface.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium),
                cursorBrush = SolidColor(ModrinthEmerald),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (value.isNotEmpty()) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.generic_clear),
                tint = AerixSurface.textSecondary,
                modifier = Modifier.size(15.dp).clickable { onValueChange("") }
            )
        }
    }
}
