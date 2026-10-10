package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.movtery.zalithlauncher.ui.theme.AerixMetrics
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.download.assets.favorites.FavoriteProjectsRepository
import com.movtery.zalithlauncher.game.download.assets.platform.Platform
import com.movtery.zalithlauncher.game.download.assets.platform.PlatformClasses
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.AerixPillTab
import com.movtery.zalithlauncher.ui.components.AerixPillTabRow
import com.movtery.zalithlauncher.ui.components.AerixSectionHeader
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadFavoritesScreen
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadGameScreen
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadModPackScreen
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadModScreen
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadResourcePackScreen
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadSavesScreen
import com.movtery.zalithlauncher.ui.screens.content.download.DownloadShadersScreen
import com.movtery.zalithlauncher.ui.screens.content.download.assets.search.SearchIdScreen
import com.movtery.zalithlauncher.ui.screens.onBack
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.viewmodel.ErrorViewModel
import com.movtery.zalithlauncher.viewmodel.EventViewModel
import com.movtery.zalithlauncher.viewmodel.ModpackImportViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.ui.components.liquidGlass

fun ScreenBackStackViewModel.navigateToDownload(targetScreen: TitledNavKey? = null) {
    downloadScreen.clearWith(targetScreen ?: downloadGameScreen)
    mainScreen.removeAndNavigateTo(removes = clearBeforeNavKeys, screenKey = downloadScreen, useClassEquality = true)
}

private fun ScreenBackStackViewModel.swapToCategoryAssets(
    platform: Platform,
    classes: PlatformClasses,
    projectId: String,
    iconUrl: String?
) {
    val targetScreen = when (classes) {
        PlatformClasses.MOD -> downloadModScreen
        PlatformClasses.MOD_PACK -> downloadModPackScreen
        PlatformClasses.RESOURCE_PACK -> downloadResourcePackScreen
        PlatformClasses.SAVES -> downloadSavesScreen
        PlatformClasses.SHADERS -> downloadShadersScreen
    }
    navigateToDownload(targetScreen = targetScreen.apply {
        navigateTo(NormalNavKey.DownloadAssets(platform = platform, projectId = projectId, classes = classes, iconUrl = iconUrl))
    })
}

private data class DiscoverCategoryItem(
    val label: String,
    val iconRes: Int,
    val target: TitledNavKey
)

@Composable
fun DownloadScreen(
    key: NestedNavKey.Download,
    backScreenViewModel: ScreenBackStackViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    eventViewModel: EventViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit
) {
    LaunchedEffect(Unit) { FavoriteProjectsRepository.ensureLoaded() }
    BaseScreen(screenKey = key, currentKey = backScreenViewModel.mainScreen.currentKey, useClassEquality = true) {
        NavigationUI(
            key = key,
            backScreenViewModel = backScreenViewModel,
            eventViewModel = eventViewModel,
            modpackImportViewModel = modpackImportViewModel,
            submitError = submitError,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun DiscoverCategoryPill(
    category: DiscoverCategoryItem,
    selected: Boolean,
    vertical: Boolean = false,
    onClick: () -> Unit
) {
    AerixPillTab(
        modifier = if (vertical) Modifier.fillMaxWidth() else Modifier,
        selected = selected,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(category.iconRes),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = category.label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun NavigationUI(
    key: NestedNavKey.Download,
    backScreenViewModel: ScreenBackStackViewModel,
    eventViewModel: EventViewModel,
    modpackImportViewModel: ModpackImportViewModel,
    submitError: (ErrorViewModel.ThrowableMessage) -> Unit,
    modifier: Modifier = Modifier
) {
    val backStack = key.backStack
    val stackTopKey = backStack.lastOrNull()
    LaunchedEffect(stackTopKey) { backScreenViewModel.downloadScreen.currentKey = stackTopKey }

    val categories = listOf(
        DiscoverCategoryItem("Modpacks", R.drawable.ic_package_2_outlined, backScreenViewModel.downloadModPackScreen),
        DiscoverCategoryItem("Mods", R.drawable.ic_extension_outlined, backScreenViewModel.downloadModScreen),
        DiscoverCategoryItem("Resource Packs", R.drawable.ic_format_paint_outlined, backScreenViewModel.downloadResourcePackScreen),
        DiscoverCategoryItem("Shaders", R.drawable.ic_lightbulb, backScreenViewModel.downloadShadersScreen),
        DiscoverCategoryItem("Worlds", R.drawable.ic_public, backScreenViewModel.downloadSavesScreen),
        DiscoverCategoryItem("Install Vanilla", R.drawable.ic_videogame_asset_outlined, backScreenViewModel.downloadGameScreen),
        DiscoverCategoryItem("Favorites", R.drawable.ic_favorite_filled, backScreenViewModel.downloadFavoritesScreen)
    )

    val isCreateInstanceScreen = stackTopKey is NestedNavKey.DownloadGame

    BoxWithConstraints(
        modifier = modifier.padding(horizontal = AerixSpacing.md, vertical = AerixSpacing.sm)
    ) {
        val useLandscapeCategories = !isCreateInstanceScreen && maxWidth >= 720.dp && maxWidth > maxHeight

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md)
        ) {
            if (useLandscapeCategories) {
                Surface(
                    modifier = Modifier
                        .width(224.dp)
                        .fillMaxHeight()
                        .liquidGlass(
                            shape = RoundedCornerShape(AerixRadii.panel),
                            tint = AerixSurface.glassTint,
                            strength = 0.98f,
                            elevation = AerixMetrics.glassFloatingElevation
                        ),
                    shape = RoundedCornerShape(AerixRadii.panel),
                    color = Color.Transparent,
                    border = BorderStroke(AerixSpacing.hairline, AerixSurface.borderSoft)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(AerixSpacing.md),
                        verticalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact)
                    ) {
                        AerixSectionHeader(
                            title = "Discover",
                            subtitle = "Curated for your next world",
                            modifier = Modifier.padding(horizontal = AerixSpacing.sm)
                        )
                        HorizontalDivider(color = AerixSurface.borderSoft)
                        categories.forEach { category ->
                            val selected = stackTopKey?.javaClass == category.target.javaClass
                            DiscoverCategoryPill(
                                category = category,
                                selected = selected,
                                vertical = true,
                                onClick = { backScreenViewModel.navigateToDownload(category.target) }
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                if (!isCreateInstanceScreen && !useLandscapeCategories) {
                    AerixPillTabRow(modifier = Modifier.fillMaxWidth()) {
                        categories.forEach { category ->
                            val selected = stackTopKey?.javaClass == category.target.javaClass
                            DiscoverCategoryPill(
                                category = category,
                                selected = selected,
                                onClick = { backScreenViewModel.navigateToDownload(category.target) }
                            )
                        }
                    }
                }

                if (backStack.isNotEmpty()) {
                    NavDisplay(
                        backStack = backStack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onBack = { onBack(backStack) },
                        transitionSpec = rememberTransitionSpec(),
                        popTransitionSpec = rememberTransitionSpec(),
                        entryProvider = entryProvider {
                            entry<NestedNavKey.DownloadGame> { child ->
                                DownloadGameScreen(
                                    key = child,
                                    mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                                    downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                                    downloadGameScreenKey = backScreenViewModel.downloadGameScreen.currentKey,
                                    onCurrentKeyChange = { backScreenViewModel.downloadGameScreen.currentKey = it },
                                    eventViewModel = eventViewModel
                                )
                            }
                            entry<NestedNavKey.DownloadModPack> { child ->
                                DownloadModPackScreen(
                                    key = child,
                                    mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                                    downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                                    downloadModPackScreenKey = backScreenViewModel.downloadModPackScreen.currentKey,
                                    onCurrentKeyChange = { backScreenViewModel.downloadModPackScreen.currentKey = it },
                                    eventViewModel = eventViewModel,
                                    importerViewModel = modpackImportViewModel
                                )
                            }
                            entry<NestedNavKey.DownloadMod> { child ->
                                DownloadModScreen(
                                    key = child,
                                    mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                                    downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                                    downloadModScreenKey = backScreenViewModel.downloadModScreen.currentKey,
                                    onCurrentKeyChange = { backScreenViewModel.downloadModScreen.currentKey = it },
                                    submitError = submitError,
                                    eventViewModel = eventViewModel
                                )
                            }
                            entry<NestedNavKey.DownloadResourcePack> { child ->
                                DownloadResourcePackScreen(
                                    key = child,
                                    mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                                    downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                                    downloadResourcePackScreenKey = backScreenViewModel.downloadResourcePackScreen.currentKey,
                                    onCurrentKeyChange = { backScreenViewModel.downloadResourcePackScreen.currentKey = it },
                                    submitError = submitError,
                                    eventViewModel = eventViewModel
                                )
                            }
                            entry<NestedNavKey.DownloadSaves> { child ->
                                DownloadSavesScreen(
                                    key = child,
                                    mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                                    downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                                    downloadSavesScreenKey = backScreenViewModel.downloadSavesScreen.currentKey,
                                    onCurrentKeyChange = { backScreenViewModel.downloadSavesScreen.currentKey = it },
                                    submitError = submitError,
                                    eventViewModel = eventViewModel
                                )
                            }
                            entry<NestedNavKey.DownloadShaders> { child ->
                                DownloadShadersScreen(
                                    key = child,
                                    mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                                    downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                                    downloadShadersScreenKey = backScreenViewModel.downloadShadersScreen.currentKey,
                                    onCurrentKeyChange = { backScreenViewModel.downloadShadersScreen.currentKey = it },
                                    submitError = submitError,
                                    eventViewModel = eventViewModel
                                )
                            }
                            entry<NormalNavKey.SearchId> {
                                SearchIdScreen(
                                    mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                                    downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                                    swapToDownload = { platform, classes, projectId, iconUrl ->
                                        backScreenViewModel.swapToCategoryAssets(platform, classes, projectId, iconUrl)
                                    },
                                    openLink = { eventViewModel.sendEvent(EventViewModel.Event.OpenLink(it)) }
                                )
                            }
                            entry<NestedNavKey.DownloadFavorites> { child ->
                                DownloadFavoritesScreen(
                                    key = child,
                                    mainScreenKey = backScreenViewModel.mainScreen.currentKey,
                                    downloadScreenKey = backScreenViewModel.downloadScreen.currentKey,
                                    downloadFavoritesScreenKey = backScreenViewModel.downloadFavoritesScreen.currentKey,
                                    onCurrentKeyChange = { backScreenViewModel.downloadFavoritesScreen.currentKey = it },
                                    swapToDownload = { platform, classes, projectId, iconUrl ->
                                        backScreenViewModel.swapToCategoryAssets(platform, classes, projectId, iconUrl)
                                    }
                                )
                            }
                        }
                    )
                } else {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }
        }
    }
}
