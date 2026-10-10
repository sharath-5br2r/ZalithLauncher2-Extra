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

import android.os.Environment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.ui.theme.MiraiThemeManager
import com.movtery.zalithlauncher.game.path.GamePathManager
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionComparator
import com.movtery.zalithlauncher.game.version.installed.VersionType
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.game.version.installed.cleanup.GameAssetCleaner
import com.movtery.zalithlauncher.ui.activities.MainActivity
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.AerixPillTab
import com.movtery.zalithlauncher.ui.components.AerixPillTabRow
import com.movtery.zalithlauncher.ui.components.liquidGlass
import com.movtery.zalithlauncher.ui.components.MarqueeText
import com.movtery.zalithlauncher.ui.components.ScalingActionButton
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.CleanupOperation
import com.movtery.zalithlauncher.ui.screens.content.elements.GamePathItemLayout
import com.movtery.zalithlauncher.ui.screens.content.elements.GamePathOperation
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionCategory
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionsOperation
import com.movtery.zalithlauncher.ui.screens.content.home.ModrinthCompactSearchField
import com.movtery.zalithlauncher.ui.screens.content.home.ModrinthMetaPill
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.utils.canHandlePermission
import com.movtery.zalithlauncher.utils.checkStoragePermissions
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import com.movtery.zalithlauncher.viewmodel.sendKeepScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private enum class LibraryFilterGroup(val label: String) {
    ALL("All"),
    MODPACKS("Modpacks"),
    VANILLA("Vanilla"),
    PINNED("Pinned")
}

private enum class LibrarySortMode(val label: String) {
    LAST_PLAYED("Sort: Pinned & Active"),
    NAME_ASC("Sort: Name (A–Z)"),
    MC_VERSION("Sort: MC Version")
}

private fun Version.managementKey(): String = "${getGameHome()}\u0000${getVersionName()}"

private class VersionsScreenViewModel : ViewModel() {
    var versionCategory by mutableStateOf(VersionCategory.ALL)
        private set
    var resortKey by mutableIntStateOf(0)
        private set

    var gamePathOperation by mutableStateOf<GamePathOperation>(GamePathOperation.None)

    var allVersionsCount by mutableIntStateOf(0)
    var vanillaVersionsCount by mutableIntStateOf(0)
    var modloaderVersionsCount by mutableIntStateOf(0)

    fun startRefreshVersions() {
        if (!VersionsManager.isRefreshing.value) {
            VersionsManager.refresh("VersionsScreenViewModel.startRefreshVersions")
        }
    }

    private var currentJob: Job? = null
    private var mutex: Mutex = Mutex()

    fun changeCategory(category: VersionCategory) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            mutex.withLock {
                this@VersionsScreenViewModel.versionCategory = category
            }
        }
    }

    fun resortVersions() {
        resortKey++
    }

    var cleanupOperation by mutableStateOf<CleanupOperation>(CleanupOperation.None)
    var cleaner by mutableStateOf<GameAssetCleaner?>(null)

    fun cleanUnusedFiles(
        onStart: () -> Unit = {},
        onStop: () -> Unit = {}
    ) {
        cleaner = GameAssetCleaner(
            scope = viewModelScope
        ).also {
            cleanupOperation = CleanupOperation.Clean
            it.start(
                onEnd = { count, size ->
                    cleaner = null
                    cleanupOperation = CleanupOperation.Success(count, size)
                    onStop()
                },
                onThrowable = { th ->
                    cleaner = null
                    cleanupOperation = CleanupOperation.Error(th)
                    onStop()
                }
            )
        }
        onStart()
    }

    fun cancelCleaner() {
        cleaner?.cancel()
        cleaner = null
        cleanupOperation = CleanupOperation.None
    }

    override fun onCleared() {
        cancelCleaner()
        currentJob?.cancel()
    }
}

@Composable
private fun rememberVersionViewModel(): VersionsScreenViewModel {
    return viewModel(
        key = NormalNavKey.VersionsManager.toString()
    ) {
        VersionsScreenViewModel()
    }
}

@Composable
private fun rememberVersions(
    versions: StateFlow<List<Version>>,
    viewModel: VersionsScreenViewModel,
): State<List<Version>> {
    val vers by versions.collectAsStateWithLifecycle()
    val category = viewModel.versionCategory
    val resortKey = viewModel.resortKey

    return remember(vers, category, resortKey) {
        derivedStateOf {
            viewModel.allVersionsCount = vers.size

            val vanillaVersions = vers
                .filter { ver -> ver.versionType == VersionType.VANILLA }
                .also { viewModel.vanillaVersionsCount = it.size }
            val modloaderVersions = vers
                .filter { ver -> ver.versionType == VersionType.MODLOADERS }
                .also { viewModel.modloaderVersionsCount = it.size }

            when (category) {
                VersionCategory.ALL -> vers
                VersionCategory.VANILLA -> vanillaVersions
                VersionCategory.MODLOADER -> modloaderVersions
            }.sortedWith(VersionComparator)
        }
    }
}

@Composable
fun VersionsManageScreen(
    backScreenViewModel: ScreenBackStackViewModel,
    navigateToVersions: (Version) -> Unit,
    navigateToExport: (Version) -> Unit,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    val viewModel = rememberVersionViewModel()

    val versions by rememberVersions(VersionsManager.versions, viewModel)
    val currentVersion by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val isRefreshing by VersionsManager.isRefreshing.collectAsStateWithLifecycle()
    var selectedVersionKey by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedVersion = versions.firstOrNull { it.managementKey() == selectedVersionKey }
        ?: currentVersion?.let { active -> versions.firstOrNull { it.managementKey() == active.managementKey() } }
        ?: versions.firstOrNull()
    LaunchedEffect(selectedVersionKey, selectedVersion) {
        val resolvedKey = selectedVersion?.managementKey()
        if (selectedVersionKey != resolvedKey) selectedVersionKey = resolvedKey
    }
    var showGamePathDrawer by rememberSaveable { mutableStateOf(false) }

    GamePathOperation(
        gamePathOperation = viewModel.gamePathOperation,
        changeState = { viewModel.gamePathOperation = it },
        submitError = submitError
    )

    BaseScreen(
        screenKey = NormalNavKey.VersionsManager,
        currentKey = backScreenViewModel.mainScreen.currentKey
    ) { isVisible ->
        Row(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = showGamePathDrawer,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(240.dp),
                    color = AerixSurface.panel,
                    border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                ) {
                    LeftMenu(
                        isVisible = isVisible,
                        isRefreshing = isRefreshing,
                        swapToFileSelector = { path ->
                            backScreenViewModel.mainScreen.backStack.navigateToFileSelector(
                                startPath = path,
                                selectFile = false,
                                saveKey = NormalNavKey.VersionsManager
                            ) { selectedPath ->
                                viewModel.gamePathOperation = GamePathOperation.AddNewPath(selectedPath)
                            }
                        },
                        onCleanupGameFiles = {
                            if (viewModel.cleanupOperation == CleanupOperation.None) {
                                viewModel.cleanupOperation = CleanupOperation.Tip
                            }
                        },
                        changePathOperation = {
                            viewModel.gamePathOperation = it
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            VersionsLayout(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                isVisible = isVisible,
                isRefreshing = isRefreshing,
                versions = versions,
                currentVersion = currentVersion,
                selectedVersion = selectedVersion,
                selectedVersionKey = selectedVersionKey,
                onSelectVersion = { version -> selectedVersionKey = version.managementKey() },
                openVersionSubscreen = { version, destination ->
                    val versionSettings = NestedNavKey.VersionSettings(version)
                    versionSettings.clearWith(destination)
                    backScreenViewModel.mainScreen.navigateTo(versionSettings, useClassEquality = true)
                },
                onOpenFolder = { version ->
                    eventViewModel.sendEvent(
                        EventViewModel.Event.OpenFileManager(
                            rootPath = version.getGameDir().absolutePath
                        )
                    )
                },
                versionCategory = viewModel.versionCategory,
                onCategoryChange = { viewModel.changeCategory(it) },
                allVersionsCount = viewModel.allVersionsCount,
                vanillaVersionsCount = viewModel.vanillaVersionsCount,
                modloaderVersionsCount = viewModel.modloaderVersionsCount,
                showGamePathDrawer = showGamePathDrawer,
                onToggleGamePathDrawer = { showGamePathDrawer = !showGamePathDrawer },
                navigateToVersions = navigateToVersions,
                navigateToExport = navigateToExport,
                onLaunchVersion = { version ->
                    VersionsManager.saveVersion(version)
                    eventViewModel.sendEvent(EventViewModel.Event.Launch.Game(version))
                },
                submitError = submitError,
                onRefresh = {
                    viewModel.startRefreshVersions()
                },
                onVersionPinned = {
                    viewModel.resortVersions()
                },
                onInstall = {
                    backScreenViewModel.navigateToDownload()
                }
            )

            CleanupOperation(
                operation = viewModel.cleanupOperation,
                changeOperation = { viewModel.cleanupOperation = it },
                cleaner = viewModel.cleaner,
                onClean = {
                    viewModel.cleanUnusedFiles(
                        onStart = {
                            eventViewModel.sendKeepScreen(true)
                        },
                        onStop = {
                            eventViewModel.sendKeepScreen(false)
                        }
                    )
                },
                onCancel = {
                    viewModel.cancelCleaner()
                    eventViewModel.sendKeepScreen(false)
                },
                submitError = submitError
            )
        }
    }
}

@Composable
private fun LeftMenu(
    isVisible: Boolean,
    isRefreshing: Boolean,
    swapToFileSelector: (path: String) -> Unit,
    onCleanupGameFiles: () -> Unit,
    changePathOperation: (GamePathOperation) -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceXOffset by swapAnimateDpAsState(
        targetValue = (-40).dp,
        swapIn = isVisible,
        isHorizontal = true
    )

    Column(
        modifier = modifier.offset { IntOffset(x = surfaceXOffset.roundToPx(), y = 0) },
    ) {
        val gamePaths by GamePathManager.gamePathData.collectAsStateWithLifecycle()
        val currentPath by GamePathManager.currentPath.collectAsStateWithLifecycle()
        val context = LocalContext.current

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = AerixSpacing.md,
                end = AerixSpacing.md,
                top = AerixSpacing.md,
                bottom = AerixSpacing.md
            )
        ) {
            items(gamePaths, key = { it.id }) { pathItem ->
                GamePathItemLayout(
                    item = pathItem,
                    selected = currentPath == pathItem.path,
                    enabled = canHandlePermission,
                    onClick = {
                        if (!isRefreshing) {
                            if (pathItem.id == GamePathManager.DEFAULT_ID) {
                                GamePathManager.saveDefaultPath()
                            } else {
                                (context as? MainActivity)?.let { activity ->
                                    checkStoragePermissions(
                                        activity = activity,
                                        message = activity.getString(R.string.versions_manage_game_storage_permissions),
                                        messageSdk30 = activity.getString(R.string.versions_manage_game_storage_permissions_sdk30),
                                        hasPermission = {
                                            GamePathManager.saveCurrentPath(pathItem.id)
                                        }
                                    )
                                }
                            }
                        }
                    },
                    onDelete = {
                        changePathOperation(GamePathOperation.DeletePath(pathItem))
                    },
                    onRename = {
                        changePathOperation(GamePathOperation.RenamePath(pathItem))
                    }
                )
            }
        }

        ScalingActionButton(
            modifier = Modifier
                .padding(horizontal = AerixSpacing.md)
                .padding(top = AerixSpacing.sm)
                .fillMaxWidth(),
            onClick = {
                (context as? MainActivity)?.let { activity ->
                    checkStoragePermissions(
                        activity = activity,
                        message = activity.getString(R.string.versions_manage_game_path_storage_permissions),
                        messageSdk30 = activity.getString(R.string.versions_manage_game_path_storage_permissions_sdk30),
                        hasPermission = {
                            swapToFileSelector(Environment.getExternalStorageDirectory().absolutePath)
                        }
                    )
                }
            },
            enabled = canHandlePermission
        ) {
            MarqueeText(text = stringResource(R.string.versions_manage_game_path_add_new))
        }

        ScalingActionButton(
            modifier = Modifier
                .padding(start = AerixSpacing.md, end = AerixSpacing.md, top = AerixSpacing.sm, bottom = AerixSpacing.md)
                .fillMaxWidth(),
            onClick = onCleanupGameFiles
        ) {
            MarqueeText(text = stringResource(R.string.versions_manage_cleanup))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
@Composable
private fun VersionsLayout(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    isRefreshing: Boolean,
    versions: List<Version>,
    currentVersion: Version?,
    selectedVersion: Version?,
    selectedVersionKey: String?,
    onSelectVersion: (Version) -> Unit,
    openVersionSubscreen: (Version, TitledNavKey) -> Unit,
    onOpenFolder: (Version) -> Unit,
    versionCategory: VersionCategory,
    onCategoryChange: (VersionCategory) -> Unit,
    allVersionsCount: Int,
    vanillaVersionsCount: Int,
    modloaderVersionsCount: Int,
    showGamePathDrawer: Boolean,
    onToggleGamePathDrawer: () -> Unit,
    navigateToVersions: (Version) -> Unit,
    navigateToExport: (Version) -> Unit,
    onLaunchVersion: (Version) -> Unit,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    onRefresh: () -> Unit,
    onVersionPinned: () -> Unit,
    onInstall: () -> Unit,
) {
    val surfaceYOffset by swapAnimateDpAsState(
        targetValue = (-40).dp,
        swapIn = isVisible
    )

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(LibraryFilterGroup.ALL) }
    var sortMode by rememberSaveable { mutableStateOf(LibrarySortMode.LAST_PLAYED) }
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(selectedFilter) {
        when (selectedFilter) {
            LibraryFilterGroup.MODPACKS -> if (versionCategory != VersionCategory.MODLOADER) onCategoryChange(VersionCategory.MODLOADER)
            LibraryFilterGroup.VANILLA -> if (versionCategory != VersionCategory.VANILLA) onCategoryChange(VersionCategory.VANILLA)
            else -> if (versionCategory != VersionCategory.ALL) onCategoryChange(VersionCategory.ALL)
        }
    }

    val displayedVersions = remember(versions, currentVersion, searchQuery, selectedFilter, sortMode) {
        val q = searchQuery.trim().lowercase()
        val filtered = versions.filter { version ->
            val info = version.getVersionInfo()
            val mcVer = info?.minecraftVersion.orEmpty()
            val loader = info?.loaderInfo?.loader?.displayName.orEmpty()

            val matchesFilter = when (selectedFilter) {
                LibraryFilterGroup.ALL -> true
                LibraryFilterGroup.MODPACKS -> info?.loaderInfo != null
                LibraryFilterGroup.VANILLA -> info?.loaderInfo == null
                LibraryFilterGroup.PINNED -> version.pinnedState
            }
            val matchesQuery = q.isEmpty() ||
                version.getVersionName().lowercase().contains(q) ||
                version.getVersionSummary().lowercase().contains(q) ||
                mcVer.lowercase().contains(q) ||
                loader.lowercase().contains(q)
            matchesFilter && matchesQuery
        }

        when (sortMode) {
            LibrarySortMode.LAST_PLAYED -> filtered
            LibrarySortMode.NAME_ASC -> filtered.sortedBy { it.getVersionName().lowercase() }
            LibrarySortMode.MC_VERSION -> filtered.sortedByDescending { it.getVersionInfo()?.minecraftVersion.orEmpty() }
        }
    }

    Box(
        modifier = modifier.offset { IntOffset(x = 0, y = surfaceYOffset.roundToPx()) }
    ) {
        if (isRefreshing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        } else {
            var versionsOperation by remember { mutableStateOf<VersionsOperation>(VersionsOperation.None) }
            VersionsOperation(
                versionsOperation = versionsOperation,
                updateVersionsOperation = { versionsOperation = it },
                submitError = submitError
            )

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val compactToolbar = maxWidth < 520.dp
                val landscapeManagement = maxWidth >= 760.dp && maxWidth > maxHeight

                if (landscapeManagement) {
                    val compactWideToolbar = maxWidth < 1080.dp
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = AerixSpacing.mdPlus, vertical = AerixSpacing.smPlus),
                        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md)
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.39f),
                            shape = RoundedCornerShape(AerixRadii.card),
                            color = AerixSurface.panel,
                            border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(AerixSpacing.md),
                                verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Instance Management",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Manage your Minecraft instances",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AerixSurface.textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = "${displayedVersions.size}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = AerixSurface.textSecondary
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
                                ) {
                                    ModrinthCompactSearchField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        placeholder = "Search instances...",
                                        modifier = Modifier.weight(1f)
                                    )
                                    Box {
                                        Surface(
                                            modifier = Modifier.size(34.dp),
                                            shape = RoundedCornerShape(AerixRadii.controlSmall),
                                            color = AerixSurface.panelRaised,
                                            border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
                                            onClick = { showSortMenu = true }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    painter = painterResource(R.drawable.ic_sort),
                                                    contentDescription = "Sort",
                                                    tint = AerixSurface.textSecondary,
                                                    modifier = Modifier.size(17.dp)
                                                )
                                            }
                                        }
                                        DropdownMenu(
                                            expanded = showSortMenu,
                                            onDismissRequest = { showSortMenu = false }
                                        ) {
                                            LibrarySortMode.entries.forEach { mode ->
                                                DropdownMenuItem(
                                                    text = { Text(mode.label) },
                                                    onClick = {
                                                        sortMode = mode
                                                        showSortMenu = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    Surface(
                                        modifier = Modifier.size(34.dp),
                                        shape = RoundedCornerShape(AerixRadii.controlSmall),
                                        color = if (showGamePathDrawer) MiraiThemeManager.currentAccent().copy(alpha = 0.18f) else AerixSurface.panelRaised,
                                        border = BorderStroke(
                                            AerixSpacing.hairline,
                                            if (showGamePathDrawer) MiraiThemeManager.currentAccent() else AerixSurface.borderSoft
                                        ),
                                        onClick = onToggleGamePathDrawer
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_folder_outlined),
                                                contentDescription = "Directories",
                                                tint = if (showGamePathDrawer) MiraiThemeManager.currentAccent() else AerixSurface.textSecondary,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }
                                    Surface(
                                        modifier = Modifier
                                            .height(34.dp)
                                            .widthIn(min = 34.dp),
                                        shape = RoundedCornerShape(AerixRadii.controlSmall),
                                        color = MiraiThemeManager.currentAccent(),
                                        contentColor = AerixSurface.onAccent,
                                        onClick = onInstall
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(
                                                horizontal = if (compactWideToolbar) AerixSpacing.sm else AerixSpacing.md,
                                                vertical = AerixSpacing.xs
                                            ),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ic_add),
                                                contentDescription = if (compactWideToolbar) "Install instance" else null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            if (!compactWideToolbar) {
                                                Text("Install", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                AerixPillTabRow(modifier = Modifier.fillMaxWidth()) {
                                    LibraryFilterGroup.entries.forEach { group ->
                                        val selected = selectedFilter == group
                                        AerixPillTab(
                                            selected = selected,
                                            onClick = { selectedFilter = group }
                                        ) {
                                            Text(
                                                text = group.label,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }

                                if (displayedVersions.isEmpty()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = if (versions.isEmpty()) {
                                                stringResource(R.string.versions_manage_no_versions)
                                            } else {
                                                "No instances match this search."
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AerixSurface.textSecondary
                                        )
                                        if (versions.isEmpty()) {
                                            Button(
                                                onClick = onInstall,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MiraiThemeManager.currentAccent(),
                                                    contentColor = AerixSurface.onAccent
                                                )
                                            ) {
                                                Text("Install an instance", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm),
                                        contentPadding = PaddingValues(bottom = AerixSpacing.xs)
                                    ) {
                                        items(
                                            items = displayedVersions,
                                            key = { it.managementKey() }
                                        ) { version ->
                                            LandscapeInstanceListItem(
                                                version = version,
                                                selected = version.managementKey() == selectedVersion?.managementKey(),
                                                active = version.managementKey() == currentVersion?.managementKey(),
                                                onClick = {
                                                    onSelectVersion(version)
                                                    if (version.managementKey() != currentVersion?.managementKey() &&
                                                        !VersionsManager.saveVersion(version)
                                                    ) {
                                                        versionsOperation = VersionsOperation.InvalidDelete(version)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        VersionManagementDetailPane(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.61f),
                            version = selectedVersion,
                            isCurrent = selectedVersion?.managementKey() == currentVersion?.managementKey(),
                            onInstall = onInstall,
                            onPlay = onLaunchVersion,
                            onOpenFolder = onOpenFolder,
                            openSubscreen = openVersionSubscreen,
                            onToggleFavorite = { version ->
                                runCatching {
                                    version.setPinnedAndSave(!version.pinnedState)
                                }.onSuccess { onVersionPinned() }
                            },
                            onRename = { versionsOperation = VersionsOperation.Rename(it) },
                            onCopy = { versionsOperation = VersionsOperation.Copy(it) },
                            onExport = navigateToExport,
                            onDelete = { versionsOperation = VersionsOperation.Delete(it) }
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = AerixSpacing.mdPlus, vertical = AerixSpacing.smPlus),
                        verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Instance Management",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Select an instance to manage",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AerixSurface.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Surface(
                                modifier = Modifier.size(30.dp),
                                shape = RoundedCornerShape(AerixRadii.pill),
                                color = AerixSurface.panelRaised,
                                border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${displayedVersions.size}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = AerixSurface.textPrimary
                                    )
                                }
                            }
                            Surface(
                                modifier = Modifier.size(34.dp),
                                shape = RoundedCornerShape(AerixRadii.controlSmall),
                                color = if (showGamePathDrawer) MiraiThemeManager.currentAccent().copy(alpha = 0.18f) else AerixSurface.panel,
                                border = BorderStroke(
                                    AerixSpacing.hairline,
                                    if (showGamePathDrawer) MiraiThemeManager.currentAccent() else AerixSurface.borderSoft
                                ),
                                onClick = onToggleGamePathDrawer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_folder_outlined),
                                        contentDescription = "Directories",
                                        tint = if (showGamePathDrawer) MiraiThemeManager.currentAccent() else AerixSurface.textSecondary,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }
                            Surface(
                                modifier = Modifier
                                    .height(34.dp)
                                    .widthIn(min = 34.dp),
                                shape = RoundedCornerShape(AerixRadii.controlSmall),
                                color = MiraiThemeManager.currentAccent(),
                                contentColor = AerixSurface.onAccent,
                                onClick = onInstall
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = if (compactToolbar) AerixSpacing.sm else AerixSpacing.md,
                                        vertical = AerixSpacing.xs
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_add),
                                        contentDescription = "Install instance",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    if (!compactToolbar) {
                                        Text(
                                            text = "Install",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                        ) {
                            ModrinthCompactSearchField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = "Search instances...",
                                modifier = Modifier.weight(1f)
                            )
                            Box {
                                Surface(
                                    modifier = Modifier.size(36.dp),
                                    shape = RoundedCornerShape(AerixRadii.controlSmall),
                                    color = AerixSurface.panel,
                                    border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
                                    onClick = { showSortMenu = true }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_sort),
                                            contentDescription = "Sort instances",
                                            tint = AerixSurface.textSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false }
                                ) {
                                    LibrarySortMode.entries.forEach { mode ->
                                        DropdownMenuItem(
                                            text = { Text(mode.label) },
                                            onClick = {
                                                sortMode = mode
                                                showSortMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        AerixPillTabRow(modifier = Modifier.fillMaxWidth()) {
                            LibraryFilterGroup.entries.forEach { group ->
                                val selected = selectedFilter == group
                                AerixPillTab(
                                    selected = selected,
                                    onClick = { selectedFilter = group }
                                ) {
                                    Text(
                                        text = group.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "YOUR INSTANCES",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AerixSurface.textSecondary
                            )
                            Text(
                                text = "${versions.size} installed",
                                style = MaterialTheme.typography.labelSmall,
                                color = AerixSurface.textMuted
                            )
                        }

                        if (displayedVersions.isEmpty()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(64.dp),
                                shape = RoundedCornerShape(AerixRadii.cardSmall),
                                color = AerixSurface.panel,
                                border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                            ) {
                                Box(
                                    modifier = Modifier.padding(horizontal = AerixSpacing.md),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = if (versions.isEmpty()) {
                                            "No instances yet — install one to get started."
                                        } else {
                                            "No instances match this search."
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AerixSurface.textSecondary
                                    )
                                }
                            }
                        } else {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = AerixSpacing.xs),
                                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                            ) {
                                items(
                                    items = displayedVersions,
                                    key = { it.managementKey() }
                                ) { version ->
                                    LandscapeInstanceListItem(
                                        version = version,
                                        selected = version.managementKey() == selectedVersion?.managementKey(),
                                        active = version.managementKey() == currentVersion?.managementKey(),
                                        onClick = {
                                            onSelectVersion(version)
                                            if (version.managementKey() != currentVersion?.managementKey() &&
                                                !VersionsManager.saveVersion(version)
                                            ) {
                                                versionsOperation = VersionsOperation.InvalidDelete(version)
                                            }
                                        },
                                        modifier = Modifier.width(220.dp)
                                    )
                                }
                            }
                        }

                        VersionManagementDetailPane(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            compactLayout = true,
                            version = selectedVersion,
                            isCurrent = selectedVersion?.managementKey() == currentVersion?.managementKey(),
                            onInstall = onInstall,
                            onPlay = onLaunchVersion,
                            onOpenFolder = onOpenFolder,
                            openSubscreen = openVersionSubscreen,
                            onToggleFavorite = { version ->
                                runCatching {
                                    version.setPinnedAndSave(!version.pinnedState)
                                }.onSuccess { onVersionPinned() }
                            },
                            onRename = { versionsOperation = VersionsOperation.Rename(it) },
                            onCopy = { versionsOperation = VersionsOperation.Copy(it) },
                            onExport = navigateToExport,
                            onDelete = { versionsOperation = VersionsOperation.Delete(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LandscapeInstanceListItem(
    version: Version,
    selected: Boolean,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val info = version.getVersionInfo()
    val loaderLabel = info?.loaderInfo?.let { "${it.loader.displayName} ${it.version}" } ?: "Vanilla"
    val gameVersion = info?.minecraftVersion ?: "Unknown"
    val shape = RoundedCornerShape(AerixRadii.cardSmall)
    val accent = MiraiThemeManager.currentAccent()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape = shape,
                tint = if (selected) accent else AerixSurface.glassBlue,
                strength = if (selected) 0.88f else 0.58f,
                elevation = AerixMetrics.glassSubtleElevation
            ),
        shape = shape,
        color = if (selected) AerixSurface.panelRaised else AerixSurface.panel.copy(alpha = 0.82f),
        border = BorderStroke(
            if (selected) 1.5.dp else AerixSpacing.hairline,
            if (selected) accent.copy(alpha = 0.86f) else AerixSurface.borderSoft
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AerixSpacing.smPlus),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
        ) {
            Surface(
                shape = RoundedCornerShape(AerixRadii.controlSmall),
                color = AerixSurface.panelRaised,
                border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
            ) {
                VersionIconImage(
                    version = version,
                    modifier = Modifier
                        .padding(AerixSpacing.xs)
                        .size(46.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)
            ) {
                Text(
                    text = version.getVersionName(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$loaderLabel • $gameVersion",
                    style = MaterialTheme.typography.labelSmall,
                    color = AerixSurface.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (active || version.pinnedState) {
                    Text(
                        text = if (active) "Active" else "Pinned",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (active) AerixSurface.success else MiraiThemeManager.currentAccent()
                    )
                }
            }
            Surface(
                modifier = Modifier.size(8.dp),
                shape = RoundedCornerShape(AerixRadii.pill),
                color = if (version.isValid()) AerixSurface.success else AerixSurface.warning
            ) {}
        }
    }
}

private data class VersionManagementTab(
    val label: String,
    val iconRes: Int,
    val destination: TitledNavKey? = null
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VersionManagementDetailPane(
    modifier: Modifier = Modifier,
    compactLayout: Boolean = false,
    version: Version?,
    isCurrent: Boolean,
    onInstall: () -> Unit,
    onPlay: (Version) -> Unit,
    onOpenFolder: (Version) -> Unit,
    openSubscreen: (Version, TitledNavKey) -> Unit,
    onToggleFavorite: (Version) -> Unit,
    onRename: (Version) -> Unit,
    onCopy: (Version) -> Unit,
    onExport: (Version) -> Unit,
    onDelete: (Version) -> Unit
) {
    val shape = RoundedCornerShape(AerixRadii.card)
    Surface(
        modifier = modifier,
        shape = shape,
        color = AerixSurface.panel,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
    ) {
        if (version == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AerixSpacing.xl),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Select an instance to manage it",
                    style = MaterialTheme.typography.titleMedium,
                    color = AerixSurface.textSecondary
                )
            }
        } else {
            val info = version.getVersionInfo()
            val loaderInfo = info?.loaderInfo
            val loaderLabel = loaderInfo?.let { "${it.loader.displayName} ${it.version}" } ?: "Vanilla"
            val gameVersion = info?.minecraftVersion ?: "Unknown"
            val tabs = listOf(
                VersionManagementTab("Overview", R.drawable.ic_dashboard_outlined),
                VersionManagementTab("Mods", R.drawable.ic_extension_outlined, NormalNavKey.Versions.ModsManager),
                VersionManagementTab("Resource Packs", R.drawable.ic_format_paint_outlined, NormalNavKey.Versions.ResourcePackManager),
                VersionManagementTab("Worlds", R.drawable.ic_public, NormalNavKey.Versions.SavesManager),
                VersionManagementTab("Settings", R.drawable.ic_build_outlined, NormalNavKey.Versions.Config)
            )
            var showActions by remember(version.managementKey()) { mutableStateOf(false) }
            val healthy = version.isValid()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AerixSpacing.md),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
                ) {
                    Surface(
                        shape = RoundedCornerShape(AerixRadii.controlSmall),
                        color = AerixSurface.panelRaised,
                        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                    ) {
                        VersionIconImage(
                            version = version,
                            modifier = Modifier
                                .padding(AerixSpacing.xs)
                                .size(42.dp)
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
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
                        ) {
                            ModrinthMetaPill(
                                text = loaderLabel,
                                backgroundColor = AerixSurface.panelRaised,
                                textColor = AerixSurface.textPrimary
                            )
                            Text(
                                text = "Minecraft $gameVersion",
                                style = MaterialTheme.typography.labelSmall,
                                color = AerixSurface.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(onClick = { onToggleFavorite(version) }) {
                        Icon(
                            painter = painterResource(
                                if (version.pinnedState) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_outlined
                            ),
                            contentDescription = stringResource(
                                if (version.pinnedState) R.string.favorite_added else R.string.favorite_add
                            ),
                            tint = if (version.pinnedState) MiraiThemeManager.currentAccent() else AerixSurface.textSecondary
                        )
                    }
                    Box {
                        IconButton(onClick = { showActions = true }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_more_horiz),
                                contentDescription = stringResource(R.string.generic_more),
                                tint = AerixSurface.textPrimary
                            )
                        }
                        DropdownMenu(
                            expanded = showActions,
                            onDismissRequest = { showActions = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(if (version.pinnedState) "Unpin" else stringResource(R.string.versions_manage_pin)) },
                                onClick = {
                                    showActions = false
                                    onToggleFavorite(version)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.generic_rename)) },
                                onClick = { showActions = false; onRename(version) }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.generic_copy)) },
                                onClick = { showActions = false; onCopy(version) }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.versions_export)) },
                                onClick = { showActions = false; onExport(version) }
                            )
                            DropdownMenuItem(
                                text = { Text("Open instance folder") },
                                onClick = { showActions = false; onOpenFolder(version) }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stringResource(R.string.generic_delete),
                                        color = AerixSurface.danger
                                    )
                                },
                                onClick = { showActions = false; onDelete(version) }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(AerixRadii.control),
                        color = AerixSurface.panelRaised,
                        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
                        contentColor = AerixSurface.textPrimary,
                        onClick = onInstall
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(painterResource(R.drawable.ic_add), contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(AerixSpacing.xs))
                            Text("Install", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp),
                        shape = RoundedCornerShape(AerixRadii.control),
                        color = MiraiThemeManager.currentAccent().copy(alpha = 0.18f),
                        border = BorderStroke(AerixSpacing.hairline, MiraiThemeManager.currentAccent().copy(alpha = 0.72f)),
                        contentColor = MiraiThemeManager.currentAccent()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(painterResource(R.drawable.ic_check), contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(AerixSpacing.xs))
                            Text("Installed", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                AerixPillTabRow(modifier = Modifier.fillMaxWidth()) {
                    tabs.forEach { tab ->
                        val selected = tab.destination == null
                        AerixPillTab(
                            selected = selected,
                            onClick = { tab.destination?.let { openSubscreen(version, it) } }
                        ) {
                            Icon(
                                painter = painterResource(tab.iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(AerixSpacing.xs))
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
                ) {
                    if (compactLayout) {
                        Column(verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                            ) {
                                VersionManagementInfoTile(
                                    modifier = Modifier.weight(1f),
                                    title = "Status",
                                    value = if (healthy) "Healthy" else "Missing files",
                                    valueColor = if (healthy) AerixSurface.success else AerixSurface.warning
                                )
                                VersionManagementInfoTile(
                                    modifier = Modifier.weight(1f),
                                    title = "Version / Loader",
                                    value = loaderLabel
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                            ) {
                                VersionManagementInfoTile(
                                    modifier = Modifier.weight(1f),
                                    title = "Game Version",
                                    value = gameVersion
                                )
                                VersionManagementInfoTile(
                                    modifier = Modifier.weight(1f),
                                    title = "Instance Folder",
                                    value = version.getGameDir().name.ifBlank { "Default" }
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                        ) {
                            VersionManagementInfoTile(
                                modifier = Modifier.weight(1f),
                                title = "Status",
                                value = if (healthy) "Healthy" else "Missing files",
                                valueColor = if (healthy) AerixSurface.success else AerixSurface.warning
                            )
                            VersionManagementInfoTile(
                                modifier = Modifier.weight(1f),
                                title = "Version / Loader",
                                value = loaderLabel
                            )
                            VersionManagementInfoTile(
                                modifier = Modifier.weight(1f),
                                title = "Game Version",
                                value = gameVersion
                            )
                            VersionManagementInfoTile(
                                modifier = Modifier.weight(1f),
                                title = "Instance Folder",
                                value = version.getGameDir().name.ifBlank { "Default" }
                            )
                        }
                    }

                    Text(
                        text = "Quick Actions",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AerixSurface.textPrimary
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm),
                        verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
                    ) {
                        VersionManagementQuickAction(
                            label = "Add Mod",
                            iconRes = R.drawable.ic_extension_outlined,
                            onClick = { openSubscreen(version, NormalNavKey.Versions.ModsManager) }
                        )
                        VersionManagementQuickAction(
                            label = "Add Resource Pack",
                            iconRes = R.drawable.ic_format_paint_outlined,
                            onClick = { openSubscreen(version, NormalNavKey.Versions.ResourcePackManager) }
                        )
                        VersionManagementQuickAction(
                            label = "Worlds",
                            iconRes = R.drawable.ic_public,
                            onClick = { openSubscreen(version, NormalNavKey.Versions.SavesManager) }
                        )
                        VersionManagementQuickAction(
                            label = "Advanced Settings",
                            iconRes = R.drawable.ic_build_outlined,
                            onClick = { openSubscreen(version, NormalNavKey.Versions.Config) }
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(AerixRadii.cardSmall),
                    color = MiraiThemeManager.currentAccent(),
                    contentColor = AerixSurface.onAccent,
                    onClick = { onPlay(version) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_play_arrow_filled),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(AerixSpacing.sm))
                        Text(
                            text = "PLAY",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                if (isCurrent) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        text = "Selected as the current launch instance",
                        style = MaterialTheme.typography.labelSmall,
                        color = AerixSurface.textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun VersionManagementInfoTile(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    valueColor: Color = AerixSurface.textPrimary
) {
    Surface(
        modifier = modifier.heightIn(min = 70.dp),
        shape = RoundedCornerShape(AerixRadii.control),
        color = AerixSurface.panelRaised.copy(alpha = 0.82f),
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AerixSpacing.smPlus),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = AerixSurface.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = valueColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun VersionManagementQuickAction(
    label: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(AerixRadii.control),
        color = AerixSurface.panelRaised,
        border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft),
        contentColor = AerixSurface.textPrimary,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = AerixSpacing.smPlus),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.xs)
        ) {
            Icon(painter = painterResource(iconRes), contentDescription = null, modifier = Modifier.size(16.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
