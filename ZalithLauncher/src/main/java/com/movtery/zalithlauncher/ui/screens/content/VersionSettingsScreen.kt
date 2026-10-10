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
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonSyntaxException
import com.movtery.zalithlauncher.ui.theme.AERIX_COMPACT_HEIGHT_THRESHOLD_DP
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.download.assets.platform.PlatformClasses
import com.movtery.zalithlauncher.game.download.game.GameDownloadInfo
import com.movtery.zalithlauncher.game.download.game.GameInstaller
import com.movtery.zalithlauncher.game.download.game.optifine.CantFetchingOptiFineUrlException
import com.movtery.zalithlauncher.game.download.jvm_server.JvmCrashException
import com.movtery.zalithlauncher.game.download.jvm_server.isProcessStartRefused
import com.movtery.zalithlauncher.game.optimization.JvmGcAutoTunerDialog
import com.movtery.zalithlauncher.game.optimization.MobileFpsBoosterDialog
import com.movtery.zalithlauncher.game.version.download.DownloadFailedException
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.notification.NotificationManager
import com.movtery.zalithlauncher.ui.androidText
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.MarqueeText
import com.movtery.zalithlauncher.ui.components.NotificationCheck
import com.movtery.zalithlauncher.ui.components.fadeEdge
import com.movtery.zalithlauncher.ui.components.verticalScrollWithBar
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.TitleTaskFlowDialog
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.ui.screens.content.home.ModrinthMetaPill
import com.movtery.zalithlauncher.ui.screens.content.home.resolveRendererShortLabel
import com.movtery.zalithlauncher.ui.screens.content.versions.AddonDiffs
import com.movtery.zalithlauncher.ui.screens.content.versions.ModsManagerScreen
import com.movtery.zalithlauncher.ui.screens.content.versions.ResourcePackManageScreen
import com.movtery.zalithlauncher.ui.screens.content.versions.SavesManagerScreen
import com.movtery.zalithlauncher.ui.screens.content.versions.ScreenshotsManagerScreen
import com.movtery.zalithlauncher.ui.screens.content.versions.ServerListScreen
import com.movtery.zalithlauncher.ui.screens.content.versions.ShadersManagerScreen
import com.movtery.zalithlauncher.ui.screens.content.versions.ModifyVersionScreen
import com.movtery.zalithlauncher.ui.screens.content.versions.UpdateLoaderScreen
import com.movtery.zalithlauncher.ui.screens.content.versions.VersionConfigScreen
import com.movtery.zalithlauncher.ui.screens.content.versions.VersionOverViewScreen
import com.movtery.zalithlauncher.ui.screens.navigateOnce
import com.movtery.zalithlauncher.ui.screens.onBack
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.ui.theme.MiraiThemeManager
import com.movtery.zalithlauncher.ui.theme.showThemed
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.utils.logging.Logger
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.ModifyOperation
import com.movtery.zalithlauncher.viewmodel.ModifyPayload
import com.movtery.zalithlauncher.viewmodel.ModifyVersionViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import com.movtery.zalithlauncher.viewmodel.sendToast
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.nio.channels.UnresolvedAddressException
import java.util.concurrent.TimeoutException

private const val TAG = "VersionSettings"

private sealed interface UpdateLoaderOperation {
    data object None: UpdateLoaderOperation
    data class Tip(val diffs: AddonDiffs, val info: GameDownloadInfo): UpdateLoaderOperation
    data class WarningForNotification(val diffs: AddonDiffs, val info: GameDownloadInfo): UpdateLoaderOperation
    data object Install: UpdateLoaderOperation
    data class Error(val th: Throwable): UpdateLoaderOperation
}

private class UpdateLoaderViewModel: ViewModel() {
    var installOperation by mutableStateOf<UpdateLoaderOperation>(UpdateLoaderOperation.None)
    var installer by mutableStateOf<GameInstaller?>(null)

    fun install(
        context: Context,
        info: GameDownloadInfo
    ) {
        installOperation = UpdateLoaderOperation.Install
        installer = GameInstaller(context, info, viewModelScope).also {
            it.updateLoader(
                onInstalled = {
                    installer = null
                    installOperation = UpdateLoaderOperation.None

                    viewModelScope.launch(Dispatchers.Main) {
                        VersionsManager.refresh("[UpdateLoader] GameInstaller.onInstalled")

                        MaterialAlertDialogBuilder(context)
                            .setTitle(R.string.download_install_success_title)
                            .setMessage(R.string.versions_update_loader_success_message)
                            .setPositiveButton(R.string.generic_confirm) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .showThemed()
                    }
                },
                onError = { th ->
                    installer = null
                    installOperation = UpdateLoaderOperation.Error(th)
                }
            )
        }
    }

    fun cancel() {
        installer?.cancelInstall(
            clearTarget = false
        )
        installer = null
        installOperation = UpdateLoaderOperation.None
    }

    override fun onCleared() {
        cancel()
    }
}

@Composable
private fun rememberUpdateLoaderViewModel(
    key: NestedNavKey.VersionSettings
): UpdateLoaderViewModel {
    return viewModel(
        key = key.toString() + "_UpdateLoader"
    ) {
        UpdateLoaderViewModel()
    }
}

/** 实例管理页的功能入口（模组、资源包、光影……） */
private data class InstanceTabItem(
    val key: TitledNavKey,
    val label: String,
    /** 紧凑侧栏网格使用的短标签（对齐设计稿） */
    val shortLabel: String,
    val iconRes: Int
)

/** 宽屏（横屏手机 / 平板）启用左右双栏布局的宽度阈值 */
private val InstanceTwoPaneMinWidth = 620.dp
/** 矮屏横屏手机即使可用宽度较窄，也应优先显示参考设计中的左右管理布局。 */
private val InstanceCompactTwoPaneMinWidth = 540.dp
/** 双栏布局下左侧实例侧栏的宽度范围（按可用宽度自适应） */
private val InstanceSidebarMinWidth = 248.dp
private val InstanceSidebarMaxWidth = 336.dp
/** 侧栏高度低于该阈值时（横屏手机），改用紧凑摘要卡与导航网格 */
private val InstanceRoomySidebarMinHeight = 620.dp
/** 导航网格单元格高度 */
private val InstanceNavCellHeight = 38.dp
/** 侧栏导航项的高度 */
private val InstanceNavItemHeight = 46.dp
/** 单栏布局下实例图标尺寸 */
private val InstanceHeroIconSize = 52.dp

@Composable
private fun rememberInstanceTabs(
    canUpdateLoader: Boolean,
    isUpdateLoader: Boolean
): List<InstanceTabItem> = remember(canUpdateLoader, isUpdateLoader) {
    buildList {
        add(InstanceTabItem(NormalNavKey.Versions.ModsManager, "Mods", "Mods", R.drawable.ic_extension_outlined))
        add(InstanceTabItem(NormalNavKey.Versions.ResourcePackManager, "Resource Packs", "Packs", R.drawable.ic_format_paint_outlined))
        add(InstanceTabItem(NormalNavKey.Versions.ShadersManager, "Shaders", "Shaders", R.drawable.ic_lightbulb))
        add(InstanceTabItem(NormalNavKey.Versions.SavesManager, "Worlds", "Worlds", R.drawable.ic_public))
        add(InstanceTabItem(NormalNavKey.Versions.ScreenshotsManager, "Screenshots", "Shots", R.drawable.ic_image_outlined))
        add(InstanceTabItem(NormalNavKey.Versions.Config, "Settings", "Settings", R.drawable.ic_build_outlined))
        add(InstanceTabItem(NormalNavKey.Versions.OverView, "Overview", "Overview", R.drawable.ic_dashboard_outlined))
        add(InstanceTabItem(NormalNavKey.Versions.ModifyVersion, "Modify", "Modify", R.drawable.ic_edit_outlined))
        if (canUpdateLoader) {
            add(
                InstanceTabItem(
                    NormalNavKey.Versions.UpdateLoader,
                    if (isUpdateLoader) "Update Loader" else "Install Loader",
                    "Loader",
                    R.drawable.ic_update
                )
            )
        }
    }
}

@Composable
fun VersionSettingsScreen(
    key: NestedNavKey.VersionSettings,
    modifyViewModel: ModifyVersionViewModel,
    backScreenViewModel: ScreenBackStackViewModel,
    backToMainScreen: () -> Unit,
    onExportModpack: () -> Unit,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val context = LocalContext.current
    val viewModel = rememberUpdateLoaderViewModel(key = key)

    val cBackToMainScreen by rememberUpdatedState(backToMainScreen)
    DisposableEffect(key) {
        val listener = object : suspend () -> Unit {
            override suspend fun invoke() {
                cBackToMainScreen()
            }
        }
        VersionsManager.registerListener(listener)
        onDispose {
            VersionsManager.unregisterListener(listener)
        }
    }

    UpdateLoaderOperation(
        operation = viewModel.installOperation,
        changeOperation = { viewModel.installOperation = it },
        installer = viewModel.installer,
        onInstall = { info ->
            viewModel.install(context, info)
        },
        onCancel = {
            viewModel.cancel()
        }
    )

    BaseScreen(
        screenKey = key,
        currentKey = backScreenViewModel.mainScreen.currentKey
    ) { isVisible ->
        val yOffset by swapAnimateDpAsState(
            targetValue = (-30).dp,
            swapIn = isVisible
        )
        val loaderInfo = remember(key) {
            key.version.getVersionInfo()?.loaderInfo
        }
        val canUpdateLoader = loaderInfo == null || loaderInfo.loader.autoDownloadable
        val isUpdateLoader = loaderInfo != null && loaderInfo.loader.autoDownloadable
        val tabs = rememberInstanceTabs(canUpdateLoader, isUpdateLoader)
        //首帧时 currentKey 尚未写入，直接使用栈顶作为选中项，避免导航高亮闪烁
        val selectedTabKey = key.currentKey ?: key.backStack.lastOrNull()

        val launchGame: () -> Unit = {
            VersionsManager.saveVersion(key.version)
            eventViewModel.sendEvent(EventViewModel.Event.Launch.Game(key.version))
        }
        val openInstanceFolder: () -> Unit = {
            eventViewModel.sendEvent(
                EventViewModel.Event.OpenFileManager(
                    rootPath = key.version.getGameDir().absolutePath
                )
            )
        }
        val selectTab: (InstanceTabItem) -> Unit = { tab ->
            if (tab.key == NormalNavKey.Versions.UpdateLoader) {
                NormalNavKey.Versions.UpdateLoader.title = androidText(
                    if (isUpdateLoader) R.string.versions_update_loader
                    else R.string.versions_install_loader
                )
            }
            key.backStack.navigateOnce(tab.key)
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(x = 0, y = yOffset.roundToPx()) }
        ) {
            //矮屏（横屏手机）压缩页面留白，把高度全部留给内容列表
            val shortScreen = maxHeight < AERIX_COMPACT_HEIGHT_THRESHOLD_DP.dp
            val twoPane = maxWidth >= InstanceTwoPaneMinWidth ||
                    (shortScreen && maxWidth >= InstanceCompactTwoPaneMinWidth)
            //侧栏尺寸必须在 BoxWithConstraints 的作用域内取好，
            //嵌套 lambda 里无法再隐式访问 maxHeight / maxWidth
            val compactSidebar = maxHeight < InstanceRoomySidebarMinHeight
            val sidebarWidth = (
                maxWidth * if (compactSidebar) 0.34f else 0.32f
            ).coerceIn(
                minimumValue = InstanceSidebarMinWidth,
                maximumValue = InstanceSidebarMaxWidth
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = if (shortScreen) AerixSpacing.md else AerixSpacing.lg,
                        vertical = if (shortScreen) AerixSpacing.sm else AerixSpacing.md
                    )
            ) {
                if (twoPane) {
                    //横屏 / 平板：左侧实例信息与功能导航，右侧完整的内容区域
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(
                            if (shortScreen) AerixSpacing.md else AerixSpacing.lg
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .width(sidebarWidth)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                        ) {
                            if (compactSidebar) {
                                //横屏手机：一行摘要 + 双列功能网格，所有入口都看得见
                                InstanceSidebarCard(
                                    version = key.version,
                                    onPlay = launchGame,
                                    onOpenFolder = openInstanceFolder
                                )

                                InstanceNavGrid(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f, fill = false),
                                    tabs = tabs,
                                    currentKey = selectedTabKey,
                                    onSelectTab = selectTab
                                )
                            } else {
                                InstanceIdentityCard(
                                    version = key.version,
                                    onBack = backToMainScreen,
                                    onPlay = launchGame,
                                    onOpenFolder = openInstanceFolder
                                )

                                InstanceNavPanel(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    tabs = tabs,
                                    currentKey = selectedTabKey,
                                    onSelectTab = selectTab
                                )
                            }
                        }

                        NavigationUI(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            key = key,
                            viewModel = viewModel,
                            modifyViewModel = modifyViewModel,
                            backScreenViewModel = backScreenViewModel,
                            versionsScreenKey = key.currentKey,
                            onCurrentKeyChange = { newKey ->
                                key.currentKey = newKey
                            },
                            backToMainScreen = backToMainScreen,
                            onExport = onExportModpack,
                            version = key.version,
                            eventViewModel = eventViewModel,
                            submitError = submitError
                        )
                    }
                } else {
                    //手机竖屏：实例信息、功能标签、内容从上到下依次排布
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)
                    ) {
                        InstanceHeroCard(
                            version = key.version,
                            onBack = backToMainScreen,
                            onPlay = launchGame,
                            onOpenFolder = openInstanceFolder
                        )

                        InstanceTabStrip(
                            tabs = tabs,
                            currentKey = selectedTabKey,
                            onSelectTab = selectTab
                        )

                        NavigationUI(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            key = key,
                            viewModel = viewModel,
                            modifyViewModel = modifyViewModel,
                            backScreenViewModel = backScreenViewModel,
                            versionsScreenKey = key.currentKey,
                            onCurrentKeyChange = { newKey ->
                                key.currentKey = newKey
                            },
                            backToMainScreen = backToMainScreen,
                            onExport = onExportModpack,
                            version = key.version,
                            eventViewModel = eventViewModel,
                            submitError = submitError
                        )
                    }
                }
            }
        }
    }
}

/**
 * 横屏手机上的紧凑实例摘要：一行信息 + 整行启动按钮。
 */
@Composable
private fun InstanceSidebarCard(
    version: Version,
    onPlay: () -> Unit,
    onOpenFolder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val info = version.getVersionInfo()
    val mcVer = info?.minecraftVersion ?: "Unknown"
    val loaderName = info?.loaderInfo?.loader?.displayName ?: "Vanilla"
    val ramMb = remember(version) { version.getRamAllocation(context) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AerixRadii.panelSmall),
        color = AerixSurface.panel,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AerixSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
        ) {
            //一行摘要：实例图标、名称、加载器/版本/内存，右侧更多操作
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                Surface(
                    shape = RoundedCornerShape(AerixRadii.control),
                    color = AerixSurface.panelRaised,
                    border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                ) {
                    VersionIconImage(
                        version = version,
                        modifier = Modifier
                            .padding(AerixSpacing.xxs)
                            .size(38.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)
                ) {
                    Text(
                        text = version.getVersionName(),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = AerixSurface.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$loaderName $mcVer   ·   $ramMb MB RAM",
                        style = MaterialTheme.typography.labelSmall,
                        color = AerixSurface.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                InstanceOverflowAction(
                    version = version,
                    onOpenFolder = onOpenFolder,
                    buttonSize = 32.dp,
                    iconSize = 18.dp
                )
            }

            //整行启动按钮，使用设计稿中的主操作色
            Button(
                onClick = onPlay,
                shape = RoundedCornerShape(AerixRadii.control),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AerixSurface.action,
                    contentColor = AerixSurface.onAction
                ),
                contentPadding = PaddingValues(horizontal = AerixSpacing.md, vertical = AerixSpacing.xs),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_play_arrow_filled),
                    contentDescription = stringResource(R.string.main_launch_game),
                    modifier = Modifier.size(17.dp)
                )
                Spacer(Modifier.width(AerixSpacing.sm))
                Text(
                    text = "Play",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

/**
 * 横屏手机上的紧凑功能导航：双列网格，所有入口一屏可见。
 */
@Composable
private fun InstanceNavGrid(
    tabs: List<InstanceTabItem>,
    currentKey: TitledNavKey?,
    onSelectTab: (InstanceTabItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAccent = MiraiThemeManager.currentAccent()
    val scrollState = rememberScrollState()
    val rows = remember(tabs) { tabs.chunked(2) }

    Column(
        modifier = modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact)
            ) {
                row.forEach { tab ->
                    InstanceNavGridItem(
                        modifier = Modifier.weight(1f),
                        tab = tab,
                        selected = currentKey === tab.key,
                        activeAccent = activeAccent,
                        onClick = { onSelectTab(tab) }
                    )
                }
                //补齐最后一行，保证两列宽度一致
                repeat(2 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun InstanceNavGridItem(
    tab: InstanceTabItem,
    selected: Boolean,
    activeAccent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(InstanceNavCellHeight),
        shape = RoundedCornerShape(AerixRadii.control),
        color = if (selected) activeAccent.copy(alpha = 0.16f) else AerixSurface.panel,
        contentColor = if (selected) activeAccent else AerixSurface.textPrimary,
        border = if (selected) {
            BorderStroke(AerixSpacing.hairline, activeAccent)
        } else {
            BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
        },
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AerixSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact)
        ) {
            Icon(
                painter = painterResource(tab.iconRes),
                contentDescription = null,
                tint = if (selected) activeAccent else AerixSurface.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = tab.shortLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                color = if (selected) activeAccent else AerixSurface.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * 双栏布局左侧的实例信息卡：返回、实例图标与信息、启动、打开实例目录。
 */
@Composable
private fun InstanceIdentityCard(
    version: Version,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onOpenFolder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeAccent = MiraiThemeManager.currentAccent()
    val info = version.getVersionInfo()
    val mcVer = info?.minecraftVersion ?: "Unknown"
    val loaderName = info?.loaderInfo?.loader?.displayName ?: "Vanilla"
    val ramMb = remember(version) { version.getRamAllocation(context) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AerixRadii.panel),
        color = AerixSurface.panel,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AerixSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                InstanceIconButton(
                    iconRes = R.drawable.ic_arrow_back,
                    description = stringResource(R.string.generic_back),
                    onClick = onBack
                )
                Spacer(Modifier.weight(1f))
                InstanceOverflowAction(
                    version = version,
                    onOpenFolder = onOpenFolder
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md)
            ) {
                Surface(
                    shape = RoundedCornerShape(AerixRadii.cardSmall),
                    color = AerixSurface.panelRaised,
                    border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                ) {
                    VersionIconImage(
                        version = version,
                        modifier = Modifier
                            .padding(AerixSpacing.sm)
                            .size(InstanceHeroIconSize)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
                ) {
                    Text(
                        text = version.getVersionName(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AerixSurface.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$loaderName · $mcVer",
                        style = MaterialTheme.typography.labelMedium,
                        color = AerixSurface.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$ramMb MB allocated",
                        style = MaterialTheme.typography.labelSmall,
                        color = AerixSurface.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                InstancePlayButton(
                    modifier = Modifier.weight(1f),
                    activeAccent = activeAccent,
                    onPlay = onPlay
                )
                InstanceIconButton(
                    iconRes = R.drawable.ic_folder_filled,
                    description = stringResource(R.string.files_browse_folder),
                    onClick = onOpenFolder
                )
            }
        }
    }
}

/**
 * 单栏布局下的实例头部卡片。
 */
@Composable
private fun InstanceHeroCard(
    version: Version,
    onBack: () -> Unit,
    onPlay: () -> Unit,
    onOpenFolder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeAccent = MiraiThemeManager.currentAccent()
    val info = version.getVersionInfo()
    val mcVer = info?.minecraftVersion ?: "Unknown"
    val loaderName = info?.loaderInfo?.loader?.displayName ?: "Vanilla"
    val ramMb = remember(version) { version.getRamAllocation(context) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AerixRadii.panel),
        color = AerixSurface.panel,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AerixSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)
        ) {
            //返回、打开实例目录与更多操作
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                InstanceIconButton(
                    iconRes = R.drawable.ic_arrow_back,
                    description = stringResource(R.string.generic_back),
                    onClick = onBack
                )
                InstanceIconButton(
                    iconRes = R.drawable.ic_folder_filled,
                    description = stringResource(R.string.files_browse_folder),
                    onClick = onOpenFolder
                )
                Spacer(Modifier.weight(1f))
                InstanceOverflowAction(
                    version = version,
                    onOpenFolder = onOpenFolder
                )
            }

            //实例图标与信息，以及启动入口
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md)
            ) {
                Surface(
                    shape = RoundedCornerShape(AerixRadii.cardSmall),
                    color = AerixSurface.panelRaised,
                    border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                ) {
                    VersionIconImage(
                        version = version,
                        modifier = Modifier
                            .padding(AerixSpacing.smCompact)
                            .size(44.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)
                ) {
                    Text(
                        text = version.getVersionName(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AerixSurface.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$loaderName · $mcVer",
                        style = MaterialTheme.typography.labelMedium,
                        color = AerixSurface.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$ramMb MB allocated",
                        style = MaterialTheme.typography.labelSmall,
                        color = AerixSurface.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                InstancePlayButton(
                    activeAccent = activeAccent,
                    onPlay = onPlay
                )
            }
        }
    }
}

@Composable
private fun InstancePlayButton(
    activeAccent: Color,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onPlay,
        shape = RoundedCornerShape(AerixRadii.control),
        colors = ButtonDefaults.buttonColors(
            containerColor = activeAccent,
            contentColor = AerixSurface.onAccent
        ),
        contentPadding = PaddingValues(horizontal = AerixSpacing.mdPlus, vertical = AerixSpacing.sm),
        modifier = modifier.height(42.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_play_arrow_filled),
            contentDescription = stringResource(R.string.main_launch_game),
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(AerixSpacing.sm))
        Text(
            text = "Play",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun InstanceIconButton(
    iconRes: Int,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 42.dp,
    iconSize: Dp = 19.dp
) {
    Surface(
        modifier = modifier.size(buttonSize),
        shape = RoundedCornerShape(AerixRadii.control),
        color = AerixSurface.panelRaised,
        contentColor = AerixSurface.textPrimary,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = description,
                tint = AerixSurface.textPrimary,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
private fun InstanceOverflowAction(
    version: Version,
    onOpenFolder: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 42.dp,
    iconSize: Dp = 20.dp
) {
    var showActionsMenu by remember { mutableStateOf(false) }
    var showFpsBooster by remember { mutableStateOf(false) }
    var showJreGcTuner by remember { mutableStateOf(false) }

    if (showFpsBooster) {
        MobileFpsBoosterDialog(
            version = version,
            onDismiss = { showFpsBooster = false }
        )
    }

    if (showJreGcTuner) {
        JvmGcAutoTunerDialog(
            version = version,
            onDismiss = { showJreGcTuner = false }
        )
    }

    Box(modifier = modifier) {
        IconButton(
            onClick = { showActionsMenu = true },
            modifier = Modifier.size(buttonSize)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_more_vert),
                contentDescription = stringResource(R.string.generic_more),
                tint = AerixSurface.textPrimary,
                modifier = Modifier.size(iconSize)
            )
        }

        DropdownMenu(
            expanded = showActionsMenu,
            onDismissRequest = { showActionsMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Boost FPS") },
                onClick = {
                    showActionsMenu = false
                    showFpsBooster = true
                }
            )
            DropdownMenuItem(
                text = { Text("JRE & GC") },
                onClick = {
                    showActionsMenu = false
                    showJreGcTuner = true
                }
            )
            DropdownMenuItem(
                text = { Text("Open instance folder") },
                onClick = {
                    showActionsMenu = false
                    onOpenFolder()
                }
            )
        }
    }
}

/**
 * 双栏布局左侧的功能导航面板。
 */
@Composable
private fun InstanceNavPanel(
    tabs: List<InstanceTabItem>,
    currentKey: TitledNavKey?,
    onSelectTab: (InstanceTabItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAccent = MiraiThemeManager.currentAccent()
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(AerixRadii.panel),
        color = AerixSurface.panel,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(AerixSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)
        ) {
            Text(
                text = "INSTANCE CONTENT",
                modifier = Modifier.padding(
                    start = AerixSpacing.smPlus,
                    top = AerixSpacing.xsPlus,
                    bottom = AerixSpacing.smCompact
                ),
                fontSize = 9.sp,
                letterSpacing = 1.6.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AerixSurface.textMuted
            )

            tabs.forEach { tab ->
                val selected = currentKey === tab.key

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(InstanceNavItemHeight),
                    shape = RoundedCornerShape(AerixRadii.control),
                    color = if (selected) activeAccent.copy(alpha = 0.18f) else Color.Transparent,
                    contentColor = if (selected) activeAccent else AerixSurface.textPrimary,
                    border = if (selected) {
                        BorderStroke(AerixSpacing.hairline, activeAccent)
                    } else {
                        null
                    },
                    onClick = { onSelectTab(tab) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = AerixSpacing.smPlus),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.mdTight)
                    ) {
                        Icon(
                            painter = painterResource(tab.iconRes),
                            contentDescription = null,
                            tint = if (selected) activeAccent else AerixSurface.textSecondary,
                            modifier = Modifier.size(19.dp)
                        )
                        Text(
                            text = tab.label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (selected) activeAccent else AerixSurface.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

/**
 * 单栏布局下的横向功能标签。
 */
@Composable
private fun InstanceTabStrip(
    tabs: List<InstanceTabItem>,
    currentKey: TitledNavKey?,
    onSelectTab: (InstanceTabItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeAccent = MiraiThemeManager.currentAccent()
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { tab ->
            val selected = currentKey === tab.key
            val bgColor by animateColorAsState(
                targetValue = if (selected) activeAccent else AerixSurface.panel,
                animationSpec = tween(140),
                label = "instanceTabBg"
            )
            val contentColor by animateColorAsState(
                targetValue = if (selected) AerixSurface.onAccent else AerixSurface.textSecondary,
                animationSpec = tween(140),
                label = "instanceTabContent"
            )

            Surface(
                shape = RoundedCornerShape(AerixRadii.pill),
                color = bgColor,
                contentColor = contentColor,
                border = BorderStroke(
                    AerixSpacing.hairline,
                    if (selected) activeAccent else AerixSurface.borderSoft
                ),
                onClick = { onSelectTab(tab) }
            ) {
                Row(
                    modifier = Modifier
                        .height(AerixMetrics.pillTabHeight)
                        .padding(horizontal = AerixSpacing.mdPlus),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact)
                ) {
                    Icon(
                        painter = painterResource(tab.iconRes),
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                        color = contentColor,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationUI(
    modifier: Modifier = Modifier,
    key: NestedNavKey.VersionSettings,
    viewModel: UpdateLoaderViewModel,
    modifyViewModel: ModifyVersionViewModel,
    backScreenViewModel: ScreenBackStackViewModel,
    versionsScreenKey: TitledNavKey?,
    onCurrentKeyChange: (TitledNavKey?) -> Unit,
    backToMainScreen: () -> Unit,
    onExport: () -> Unit,
    version: Version,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val context = LocalContext.current
    val mainScreenKey = backScreenViewModel.mainScreen.currentKey

    val backStack = key.backStack
    val stackTopKey = backStack.lastOrNull()
    LaunchedEffect(stackTopKey) {
        onCurrentKeyChange(stackTopKey)
    }

    if (backStack.isNotEmpty()) {
        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = {
                onBack(backStack)
            },
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(),
            entryProvider = entryProvider {
                entry<NormalNavKey.Versions.ModifyVersion> {
                    ModifyVersionScreen(
                        viewModel = modifyViewModel,
                        mainScreenKey = mainScreenKey,
                        versionsScreenKey = versionsScreenKey,
                        version = version,
                        eventViewModel = eventViewModel,
                        onModify = { payload ->
                            if (modifyViewModel.installOperation !is ModifyOperation.None) {
                                return@ModifyVersionScreen
                            }
                            if (!NotificationManager.checkNotificationEnabled(context)) {
                                modifyViewModel.installOperation = ModifyOperation.WarningForNotification(payload)
                            } else {
                                modifyViewModel.installOperation = ModifyOperation.Confirm(payload)
                            }
                        }
                    )
                }
                entry<NormalNavKey.Versions.OverView> {
                    VersionOverViewScreen(
                        mainScreenKey = mainScreenKey,
                        versionsScreenKey = versionsScreenKey,
                        backToMainScreen = backToMainScreen,
                        onExport = onExport,
                        version = version,
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.Versions.Config> {
                    VersionConfigScreen(
                        mainScreenKey = mainScreenKey,
                        versionsScreenKey = versionsScreenKey,
                        version = version,
                        backToMainScreen = backToMainScreen,
                        onCheckVulkan = { version ->
                            eventViewModel.sendEvent(
                                EventViewModel.Event.VulkanCheck(version)
                            )
                        },
                        showToast = { text ->
                            eventViewModel.sendToast(text)
                        },
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.Versions.UpdateLoader> {
                    UpdateLoaderScreen(
                        mainScreenKey = mainScreenKey,
                        versionsScreenKey = versionsScreenKey,
                        backToMainScreen = backToMainScreen,
                        version = version
                    ) { diffs, info ->
                        if (viewModel.installOperation !is UpdateLoaderOperation.None) {
                            return@UpdateLoaderScreen
                        }
                        if (!NotificationManager.checkNotificationEnabled(context)) {
                            viewModel.installOperation = UpdateLoaderOperation.WarningForNotification(diffs, info)
                        } else {
                            viewModel.installOperation = UpdateLoaderOperation.Tip(diffs, info)
                        }
                    }
                }
                entry(NormalNavKey.Versions.ModsManager) {
                    ModsManagerScreen(
                        mainScreenKey = mainScreenKey,
                        versionsScreenKey = versionsScreenKey,
                        version = version,
                        backToMainScreen = backToMainScreen,
                        swapToDownload = {
                            backScreenViewModel.navigateToDownload(
                                targetScreen = backScreenViewModel.downloadModScreen
                            )
                        },
                        onSwapMoreInfo = { projectId, platform ->
                            backScreenViewModel.mainScreen.removeAndNavigateTo(
                                NestedNavKey.AssetInfo::class,
                                NestedNavKey.AssetInfo(platform, projectId, PlatformClasses.MOD)
                            )
                        },
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.Versions.SavesManager> {
                    SavesManagerScreen(
                        mainScreenKey = mainScreenKey,
                        versionsScreenKey = versionsScreenKey,
                        version = version,
                        backToMainScreen = backToMainScreen,
                        swapToDownload = {
                            backScreenViewModel.navigateToDownload(
                                targetScreen = backScreenViewModel.downloadSavesScreen
                            )
                        },
                        onQuickPlay = { version, saveName ->
                            eventViewModel.sendEvent(
                                EventViewModel.Event.Launch.PlaySave(
                                    version = version,
                                    saveName = saveName
                                )
                            )
                        },
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.Versions.ResourcePackManager> {
                    ResourcePackManageScreen(
                        mainScreenKey = mainScreenKey,
                        versionsScreenKey = versionsScreenKey,
                        version = version,
                        backToMainScreen = backToMainScreen,
                        swapToDownload = {
                            backScreenViewModel.navigateToDownload(
                                targetScreen = backScreenViewModel.downloadResourcePackScreen
                            )
                        },
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.Versions.ShadersManager> {
                    ShadersManagerScreen(
                        mainScreenKey = mainScreenKey,
                        versionsScreenKey = versionsScreenKey,
                        version = version,
                        backToMainScreen = backToMainScreen,
                        swapToDownload = {
                            backScreenViewModel.navigateToDownload(
                                targetScreen = backScreenViewModel.downloadShadersScreen
                            )
                        },
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.Versions.ScreenshotsManager> {
                    ScreenshotsManagerScreen(
                        mainScreenKey = mainScreenKey,
                        versionsScreenKey = versionsScreenKey,
                        version = version,
                        backToMainScreen = backToMainScreen,
                        eventViewModel = eventViewModel,
                        submitError = submitError
                    )
                }
                entry<NormalNavKey.Versions.ServerList> {
                    ServerListScreen(
                        mainScreenKey = mainScreenKey,
                        versionsScreenKey = versionsScreenKey,
                        version = version,
                        onQuickPlay = { version, address ->
                            eventViewModel.sendEvent(
                                EventViewModel.Event.Launch.PlayServer(
                                    version = version,
                                    address = address
                                )
                            )
                        },
                        backToMainScreen = backToMainScreen,
                    )
                }
            }
        )
    } else {
        Box(modifier)
    }
}

@Composable
private fun UpdateLoaderOperation(
    operation: UpdateLoaderOperation,
    changeOperation: (UpdateLoaderOperation) -> Unit,
    installer: GameInstaller?,
    onInstall: (GameDownloadInfo) -> Unit,
    onCancel: () -> Unit
) {
    when (operation) {
        is UpdateLoaderOperation.None -> {}
        is UpdateLoaderOperation.WarningForNotification -> {
            NotificationCheck(
                text = stringResource(R.string.notification_data_jvm_service_message),
                onGranted = {
                    changeOperation(UpdateLoaderOperation.Tip(operation.diffs, operation.info))
                },
                onIgnore = {
                    changeOperation(UpdateLoaderOperation.Tip(operation.diffs, operation.info))
                },
                onDismiss = {
                    changeOperation(UpdateLoaderOperation.None)
                }
            )
        }
        is UpdateLoaderOperation.Tip -> {
            val dismiss = {
                changeOperation(UpdateLoaderOperation.None)
            }
            AlertDialog(
                onDismissRequest = dismiss,
                title = {
                    Text(text = stringResource(R.string.generic_tip))
                },
                text = {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fadeEdge(state = scrollState)
                            .verticalScrollWithBar(state = scrollState),
                        verticalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
                    ) {
                        Text(text = stringResource(R.string.versions_update_loader_diff_message))

                        operation.diffs.list.forEach { diff ->
                            val modloader = diff.getLoader().displayName
                            val string = when (diff) {
                                is AddonDiffs.VersionChangeDiff -> {
                                    stringResource(R.string.versions_update_loader_diff_change, modloader, diff.original, diff.updateTo)
                                }
                                is AddonDiffs.RemoveDiff -> {
                                    stringResource(R.string.versions_update_loader_diff_remove, modloader)
                                }
                                is AddonDiffs.NewLoadDiff -> {
                                    stringResource(R.string.versions_update_loader_diff_load, modloader, diff.version)
                                }
                            }
                            Text(text = string)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { onInstall(operation.info) }
                    ) {
                        MarqueeText(text = stringResource(R.string.generic_confirm))
                    }
                },
                dismissButton = {
                    Button(onClick = dismiss) {
                        MarqueeText(text = stringResource(R.string.generic_cancel))
                    }
                }
            )
        }
        is UpdateLoaderOperation.Install -> {
            if (installer != null) {
                val updateLoader by installer.tasksFlow.collectAsStateWithLifecycle()
                val installLog = installer.logOutput.collectAsStateWithLifecycle()
                if (updateLoader.isNotEmpty()) {
                    TitleTaskFlowDialog(
                        title = stringResource(R.string.versions_update_loader),
                        tasks = updateLoader,
                        onCancel = {
                            onCancel()
                            changeOperation(UpdateLoaderOperation.None)
                        },
                        logOutput = installLog.value
                    )
                }
            }
        }
        is UpdateLoaderOperation.Error -> {
            val th = operation.th
            Logger.error(TAG, "Failed to download the game!", th)
            val message = when (th) {
                is HttpRequestTimeoutException, is SocketTimeoutException, is TimeoutException -> stringResource(R.string.error_timeout)
                is UnknownHostException, is UnresolvedAddressException -> stringResource(R.string.error_network_unreachable)
                is ConnectException -> stringResource(R.string.error_connection_failed)
                is SerializationException, is JsonSyntaxException -> stringResource(R.string.error_parse_failed)
                is CantFetchingOptiFineUrlException -> stringResource(R.string.download_install_error_cant_fetch_optifine_download_url)
                is JvmCrashException -> stringResource(R.string.download_install_error_jvm_crash, th.code)
                is DownloadFailedException -> stringResource(R.string.download_install_error_download_failed)
                else -> when {
                    th.isProcessStartRefused() -> stringResource(R.string.download_install_error_process_start)
                    else -> th.localizedMessage ?: th.message ?: th::class.qualifiedName ?: "Unknown error"
                }
            }
            val dismiss = {
                changeOperation(UpdateLoaderOperation.None)
            }
            AlertDialog(
                onDismissRequest = dismiss,
                title = {
                    Text(text = stringResource(R.string.download_install_error_title))
                },
                text = {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fadeEdge(state = scrollState)
                            .verticalScrollWithBar(state = scrollState),
                        verticalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
                    ) {
                        Text(text = stringResource(R.string.versions_update_loader_error_message))
                        Text(text = message)
                    }
                },
                confirmButton = {
                    Button(onClick = dismiss) {
                        MarqueeText(text = stringResource(R.string.generic_confirm))
                    }
                }
            )
        }
    }
}
