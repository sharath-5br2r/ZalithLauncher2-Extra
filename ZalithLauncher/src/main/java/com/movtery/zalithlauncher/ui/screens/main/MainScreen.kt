/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.main

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.BuildKeys
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.coroutine.Task
import com.movtery.zalithlauncher.coroutine.TaskSystem
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.AndroidStringText
import com.movtery.zalithlauncher.ui.androidText
import com.movtery.zalithlauncher.ui.components.AerixSectionHeader
import com.movtery.zalithlauncher.ui.components.liquidGlass
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.CardTitleLayout
import com.movtery.zalithlauncher.ui.guide.sendStartGuideOnce
import com.movtery.zalithlauncher.ui.screens.BackStackNavKey
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.AccountManageScreen
import com.movtery.zalithlauncher.ui.screens.content.FirstLoginMenu
import com.movtery.zalithlauncher.ui.screens.content.DownloadScreen
import com.movtery.zalithlauncher.ui.screens.content.FileSelectorScreen
import com.movtery.zalithlauncher.ui.screens.content.LauncherScreen
import com.movtery.zalithlauncher.ui.screens.content.LicenseScreen
import com.movtery.zalithlauncher.ui.screens.content.LogViewScreen
import com.movtery.zalithlauncher.ui.screens.content.SettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionExportScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionSettingsScreen
import com.movtery.zalithlauncher.ui.screens.content.VersionsManageScreen
import com.movtery.zalithlauncher.ui.screens.content.WebViewScreen
import com.movtery.zalithlauncher.ui.screens.content.assetinfo.AssetInfoScreen
import com.movtery.zalithlauncher.ui.screens.navigateTo
import com.movtery.zalithlauncher.ui.screens.onBack
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.ui.theme.backgroundColor
import com.movtery.zalithlauncher.ui.theme.festivals.FestivalTitleText
import com.movtery.zalithlauncher.ui.theme.onBackgroundColor
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.utils.animation.getAnimateTween
import com.movtery.zalithlauncher.utils.festival.LocalFestivals
import com.movtery.zalithlauncher.utils.file.formatFileSize
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.LocalBackgroundViewModel
import com.movtery.zalithlauncher.viewmodel.ModpackImportViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import com.movtery.zalithlauncher.viewmodel.sendKeepScreen

@Composable
fun MainScreen(
    screenBackStackModel: ScreenBackStackViewModel,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val tasks by TaskSystem.tasksFlow.collectAsStateWithLifecycle()

    //监控当前是否有任务正在进行
    LaunchedEffect(tasks) {
        if (tasks.isEmpty()) {
            eventViewModel.sendKeepScreen(false)
        } else {
            //有任务正在进行，避免熄屏
            eventViewModel.sendKeepScreen(true)
        }
    }

    val isTaskMenuExpanded = AllSettings.launcherTaskMenuExpanded.state

    fun changeTasksExpandedState() {
        AllSettings.launcherTaskMenuExpanded.save(!isTaskMenuExpanded)
    }

    /** 回到主页面通用函数 */
    val toMainScreen: () -> Unit = {
        screenBackStackModel.mainScreen.clearWith(NormalNavKey.LauncherMain)
    }

    val mainScreenKey = screenBackStackModel.mainScreen.currentKey
    val isBackgroundValid = LocalBackgroundViewModel.current?.isValid == true
    val launcherBackgroundOpacity = AllSettings.launcherBackgroundOpacity.state.toFloat() / 100f

    // Keep the wallpaper present as the actual backplate of the glass system.
    // Use the preference as a soft nonlinear dimmer: the wallpaper stays luminous
    // through glass at its default value, while 100% still fully hides it.
    val wallpaperScrim = launcherBackgroundOpacity.coerceIn(0f, 1f).let { it * it * it }
    val backgroundColor = if (isBackgroundValid) {
        MaterialTheme.colorScheme.background.copy(alpha = wallpaperScrim)
    } else backgroundColor()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor,
        contentColor = onBackgroundColor()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val width = size.width.coerceAtLeast(1f)
                    val height = size.height.coerceAtLeast(1f)
                    // One cached aurora pass avoids repainting two additional full-screen
                    // radial gradients beneath every route and scroll surface.
                    val aurora = Brush.linearGradient(
                        colors = listOf(
                            AerixSurface.auroraCyan.copy(alpha = 0.065f),
                            Color.Transparent,
                            AerixSurface.auroraViolet.copy(alpha = 0.055f)
                        ),
                        start = Offset(0f, height * 0.08f),
                        end = Offset(width, height * 0.92f)
                    )
                    onDrawBehind { drawRect(brush = aurora) }
                }
        ) {
        Row(modifier = Modifier.fillMaxSize()) {
            MiraiNavigationRail(
                modifier = Modifier.fillMaxHeight(),
                selectedSection = mainScreenKey.toLauncherSection(),
                onNavigate = { section ->
                    when (section) {
                        LauncherSection.HOME -> screenBackStackModel.mainScreen.clearWith(NormalNavKey.LauncherMain)
                        LauncherSection.DISCOVER -> {
                            screenBackStackModel.downloadScreen.clearWith(screenBackStackModel.downloadModScreen)
                            screenBackStackModel.mainScreen.clearWith(screenBackStackModel.downloadScreen)
                        }
                        LauncherSection.LIBRARY -> screenBackStackModel.mainScreen.clearWith(NormalNavKey.VersionsManager)
                        LauncherSection.WALLPAPERS -> {
                            screenBackStackModel.settingsScreen.clearWith(NormalNavKey.Settings.Wallpapers)
                            screenBackStackModel.mainScreen.clearWith(screenBackStackModel.settingsScreen)
                        }
                        LauncherSection.SETTINGS -> {
                            if (screenBackStackModel.settingsScreen.currentKey === NormalNavKey.Settings.Wallpapers) {
                                screenBackStackModel.settingsScreen.clearWith(NormalNavKey.Settings.Renderer)
                            }
                            screenBackStackModel.mainScreen.clearWith(screenBackStackModel.settingsScreen)
                        }
                        LauncherSection.ACCOUNTS -> screenBackStackModel.mainScreen.clearWith(
                            NormalNavKey.AccountManager(FirstLoginMenu.NONE)
                        )
                    }
                },
                onCreateInstance = {
                    screenBackStackModel.downloadScreen.clearWith(screenBackStackModel.downloadGameScreen)
                    screenBackStackModel.mainScreen.clearWith(screenBackStackModel.downloadScreen)
                },
                onAccountClick = {
                    screenBackStackModel.mainScreen.clearWith(
                        NormalNavKey.AccountManager(FirstLoginMenu.NONE)
                    )
                }
            )

            //没有全局顶栏：页面内容直接顶到屏幕最上方，
            //只有在需要返回时，才在内容左侧留出一条很窄的返回栏
            val canGoBack = screenBackStackModel.mainScreen.backStack.size > 1
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                if (canGoBack) {
                    PageBackGutter(
                        onBack = { onBack(screenBackStackModel.mainScreen.backStack) }
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    NavigationUI(
                        modifier = Modifier.fillMaxSize(),
                        screenBackStackModel = screenBackStackModel,
                        toMainScreen = toMainScreen,
                        eventViewModel = eventViewModel,
                        modpackImportViewModel = modpackImportViewModel,
                        submitError = submitError
                    )

                    //仅在有下载任务时出现的小圆点提示，不占布局高度
                    if (tasks.isNotEmpty() && !isTaskMenuExpanded) {
                        TasksBadge(
                            count = tasks.size,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(all = AerixSpacing.sm),
                            onClick = { changeTasksExpandedState() }
                        )
                    }

                    TaskMenu(
                        tasks = tasks,
                        isExpanded = isTaskMenuExpanded,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.36f)
                            .align(Alignment.CenterEnd)
                            .padding(all = AerixSpacing.sm)
                    ) {
                        changeTasksExpandedState()
                    }
                }
            }
        }
        }
    }
}

private fun TitledNavKey?.toLauncherSection(): LauncherSection? = when (this) {
    null, NormalNavKey.LauncherMain -> LauncherSection.HOME
    is NestedNavKey.Download,
    is NestedNavKey.DownloadGame,
    is NestedNavKey.DownloadModPack,
    is NestedNavKey.DownloadMod,
    is NestedNavKey.DownloadResourcePack,
    is NestedNavKey.DownloadSaves,
    is NestedNavKey.DownloadShaders,
    is NestedNavKey.DownloadFavorites,
    is NestedNavKey.AssetInfo -> LauncherSection.DISCOVER
    NormalNavKey.VersionsManager,
    is NestedNavKey.VersionSettings,
    is NestedNavKey.VersionExport -> LauncherSection.LIBRARY
    is NestedNavKey.Settings -> if (this.currentKey === NormalNavKey.Settings.Wallpapers) {
        LauncherSection.WALLPAPERS
    } else {
        LauncherSection.SETTINGS
    }
    is NormalNavKey.AccountManager -> LauncherSection.ACCOUNTS
    else -> null
}

/**
 * 页面左侧的返回栏：只有存在上一页时才出现。
 * 它只是一条很窄的竖向留白，不会像顶栏那样占用页面高度。
 */
@Composable
private fun PageBackGutter(
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(AerixSpacing.section)
            .padding(start = AerixSpacing.xs, top = AerixSpacing.sm),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            shape = RoundedCornerShape(AerixRadii.control),
            color = AerixSurface.panel,
            border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
            modifier = Modifier
                .size(32.dp)
                .liquidGlass(
                    shape = RoundedCornerShape(AerixRadii.control),
                    tint = AerixSurface.glassTint,
                    strength = 0.9f,
                    elevation = AerixMetrics.glassSubtleElevation
                ),
            onClick = onBack
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.generic_back),
                    tint = AerixSurface.textPrimary,
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

/**
 * 下载任务提示：仅在存在任务时浮在右上角
 */
@Composable
private fun TasksBadge(
    count: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AerixRadii.pill),
        color = AerixSurface.panelRaised,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AerixSpacing.smPlus, vertical = AerixSpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_download),
                contentDescription = null,
                tint = AerixSurface.action,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AerixSurface.textPrimary
            )
        }
    }
}

@Composable
private fun NavigationUI(
    modifier: Modifier = Modifier,
    screenBackStackModel: ScreenBackStackViewModel,
    toMainScreen: () -> Unit,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val backStack = screenBackStackModel.mainScreen.backStack
    val currentKey = backStack.lastOrNull()

    LaunchedEffect(currentKey) {
        screenBackStackModel.mainScreen.currentKey = currentKey
    }

    if (backStack.isNotEmpty()) {
        /** 导航至版本详细信息屏幕 */
        val navigateToVersions: (Version) -> Unit = { version ->
            screenBackStackModel.mainScreen.navigateTo(
                screenKey = NestedNavKey.VersionSettings(version),
                useClassEquality = true
            )
        }
        /** 导航至整合包导出屏幕 */
        val navigateToExport: (Version) -> Unit = { version ->
            screenBackStackModel.mainScreen.removeAndNavigateTo(
                remove = NestedNavKey.VersionSettings::class,
                screenKey = NestedNavKey.VersionExport(version),
                useClassEquality = true
            )
        }

        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = {
                onBack(backStack)
            },
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(),
            entryProvider = entryProvider {
                entry<NormalNavKey.LauncherMain> {
                    LauncherScreen(
                        backStackViewModel = screenBackStackModel,
                        navigateToVersions = navigateToVersions,
                        onLaunchGame = { version ->
                            eventViewModel.sendEvent(
                                EventViewModel.Event.Launch.Game(version)
                            )
                        },
                        onOpenLink = {
                            eventViewModel.sendEvent(EventViewModel.Event.OpenLink(it))
                        },
                        startGuideOnce = { keys ->
                            eventViewModel.sendStartGuideOnce(keys)
                        }
                    )
                }
                entry<NestedNavKey.Settings> { key ->
                    SettingsScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                        openLicenseScreen = { raw ->
                            backStack.navigateTo(NormalNavKey.License(raw))
                        },
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.License> { key ->
                    LicenseScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel
                    )
                }
                entry<NormalNavKey.AccountManager> { key ->
                    AccountManageScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                        backToMainScreen = toMainScreen,
                        openLink = { url ->
                            eventViewModel.sendEvent(EventViewModel.Event.OpenLink(url))
                        },
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.WebScreen> { key ->
                    WebViewScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel
                    )
                }
                entry<NormalNavKey.VersionsManager> {
                    VersionsManageScreen(
                        backScreenViewModel = screenBackStackModel,
                        navigateToVersions = navigateToVersions,
                        navigateToExport = navigateToExport,
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.FileSelector> { key ->
                    FileSelectorScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel
                    ) {
                        backStack.removeLastOrNull()
                    }
                }
                entry<NestedNavKey.VersionSettings> { key ->
                    VersionSettingsScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel,
                        backToMainScreen = toMainScreen,
                        onExportModpack = {
                            navigateToExport(key.version)
                        },
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NestedNavKey.VersionExport> { key ->
                    VersionExportScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel,
                        backToMainScreen = toMainScreen
                    )
                }
                entry<NestedNavKey.Download> { key ->
                    DownloadScreen(
                        key = key,
                        backScreenViewModel = screenBackStackModel,
                        eventViewModel = eventViewModel,
                        modpackImportViewModel = modpackImportViewModel,
                        submitError = submitError
                    )
                }
                entry<NestedNavKey.AssetInfo> { key ->
                    AssetInfoScreen(
                        key = key,
                        mainScreenKey = screenBackStackModel.mainScreen.currentKey,
                        assetInfoScreenKey = key.currentKey,
                        eventViewModel = eventViewModel,
                        submitError = submitError,
                    )
                }
                entry<NormalNavKey.LogView> { key ->
                    LogViewScreen(
                        key = key,
                        backStackViewModel = screenBackStackModel,
                    )
                }
            }
        )
    } else {
        Box(modifier)
    }
}

@Composable
private fun TaskMenu(
    tasks: List<Task>,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    changeExpandedState: () -> Unit = {}
) {
    val show = isExpanded && tasks.isNotEmpty()

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    AnimatedVisibility(
        modifier = modifier,
        enter = slideInHorizontally(
            initialOffsetX = { if (isRtl) -it else it },
            animationSpec = getAnimateTween()
        ) + fadeIn(),
        exit = slideOutHorizontally(
            targetOffsetX = { if (isRtl) -it else it },
            animationSpec = getAnimateTween()
        ) + fadeOut(),
        visible = show
    ) {
        BackgroundCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = AerixSpacing.xs),
            influencedByBackground = false,
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = AerixSurface.panel,
                contentColor = Color.White
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
        ) {
            Column {
                CardTitleLayout(blur = 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AerixSpacing.mdPlus, vertical = AerixSpacing.smPlus),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Download Tasks (${tasks.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        IconButton(
                            modifier = Modifier.size(28.dp),
                            onClick = changeExpandedState
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = stringResource(R.string.generic_collapse),
                                tint = AerixSurface.textSecondary
                            )
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = AerixSpacing.md, vertical = AerixSpacing.smCompact)
                ) {
                    items(tasks) { task ->
                        val taskProgress by task.progress.collectAsStateWithLifecycle()
                        val taskMessage by task.message.collectAsStateWithLifecycle()
                        val rateBytesPerSec by task.rateBytesPerSec.collectAsStateWithLifecycle()

                        TaskItem(
                            taskProgress = taskProgress,
                            taskMessage = taskMessage,
                            rateBytesPerSec = rateBytesPerSec,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AerixSpacing.smCompact)
                        ) {
                            //取消任务
                            TaskSystem.cancelTask(task.id)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    taskProgress: Float,
    taskMessage: AndroidStringText?,
    rateBytesPerSec: Long?,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    color: Color = AerixSurface.panelRaised,
    contentColor: Color = Color.White,
    onCancelClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier.padding(all = AerixSpacing.smPlus),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(AerixSurface.accentContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(R.drawable.ic_download_2_filled),
                    contentDescription = null,
                    tint = AerixSurface.accent
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.xsPlus)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        taskMessage?.let { message ->
                            AndroidStringText(
                                text = message,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    taskProgress.takeIf { it >= 0f }?.let { progress ->
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            color = AerixSurface.accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (taskProgress < 0) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp),
                        color = AerixSurface.accent,
                        trackColor = AerixSurface.panelRaised
                    )
                } else {
                    LinearProgressIndicator(
                        progress = { taskProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp),
                        color = AerixSurface.accent,
                        trackColor = AerixSurface.panelRaised
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    rateBytesPerSec?.let { bytes ->
                        val text = remember(bytes) { "${formatFileSize(bytes)}/s" }
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelSmall,
                            color = AerixSurface.textSecondary
                        )
                    } ?: Text(
                        text = "Downloading...",
                        style = MaterialTheme.typography.labelSmall,
                        color = AerixSurface.textSecondary
                    )
                }
            }

            IconButton(
                modifier = Modifier
                    .size(26.dp)
                    .align(Alignment.CenterVertically),
                onClick = onCancelClick
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = stringResource(R.string.generic_cancel),
                    tint = AerixSurface.textSecondary
                )
            }
        }
    }
}
