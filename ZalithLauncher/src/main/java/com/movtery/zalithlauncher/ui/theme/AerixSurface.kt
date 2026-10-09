/*
 * Aerix Launcher visual tokens.
 *
 * Keep shared surface colors, spacing, and corner radii here so launcher screens
 * share one translucent, refractive Liquid Glass language.
 */
package com.movtery.zalithlauncher.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AerixSurface {
    val canvas = Color(0xFF07131D)
    // Glass panels deliberately retain enough alpha for text contrast while allowing
    // the wallpaper and the ambient light field to remain visible beneath them.
    val panel = Color(0x9D172B3A)
    val panelRaised = Color(0xB0223B4E)
    val panelTrack = Color(0x68344F63)
    val panelGlassTint = Color(0x7D22445A)
    val border = Color(0x78E6FAFF)
    val borderSoft = Color(0x48E6FAFF)
    val borderHighlight = Color(0xE0E6FCFF)
    val glassTint = Color(0xFF2B5063)
    val glassBlue = Color(0xFF91E9FF)
    val glassViolet = Color(0xFFB7A9FF)
    val glassRose = Color(0xFFF3A9D9)
    val auroraCyan = Color(0xFF37DCEC)
    val auroraViolet = Color(0xFF7A71F5)
    val glassShadow = Color(0x66000610)
    /** Static fallback used to define the built-in Aerix palette and wallpaper presets. */
    val accentDefault = Color(0xFF8DEFE0)
    /** Current launcher accent; UI controls should use this instead of a baked-in color. */
    val accent: Color
        @Composable get() = MiraiThemeManager.currentAccent()
    /** Primary action color follows the selected launcher / wallpaper theme. */
    val action: Color
        @Composable get() = accent
    val onAction: Color
        @Composable get() = onAccent
    val accentSecondary = Color(0xFFC1B5FF)
    val accentGlow = Color(0x668DEFE0)
    /** Static container fallback retained for the built-in Aerix color scheme. */
    val accentContainerDefault = Color(0x5539C8C1)
    /** Theme-tinted translucent container for selected and highlighted UI. */
    val accentContainer: Color
        @Composable get() = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.33f)
    val onAccentDefault = Color(0xFF06201D)
    val onAccent: Color
        @Composable get() = MaterialTheme.colorScheme.onPrimary
    val onAccentContainer = Color(0xFFBDFAF3)
    val accentLight = Color(0xFF006D73)
    val accentLightContainer = Color(0xFFA8F0F1)
    val onAccentLightContainer = Color(0xFF002021)
    val lightCanvas = Color(0xFFF3F8FA)
    val lightPanel = Color(0xFFFCFEFF)
    val lightPanelRaised = Color(0xFFE5F1F3)
    val lightBorder = Color(0xFFBCCED1)
    val lightTextPrimary = Color(0xFF14212B)
    val lightTextSecondary = Color(0xFF4C626D)
    val errorDarkContainer = Color(0xFF7A2635)
    val errorLight = Color(0xFFB3203D)
    val errorLightContainer = Color(0xFFFFDAD9)
    val dangerContainer = Color(0xFF3B1A1E)
    val warningContainer = Color(0xFF3A2E16)
    val modrinthBrand = Color(0xFF1BD96A)
    val onModrinthBrand = Color(0xFF072314)
    val curseForgeBrand = Color(0xFFF16436)
    val logInfoSurface = Color(0xFF447152)
    val logDebugSurface = Color(0xFF43698D)
    val logWarningSurface = Color(0xFF656E76)
    val syntaxMuted = Color(0xFF6E7C83)
    val syntaxString = Color(0xFF6AAB73)
    val syntaxAccent = Color(0xFFC67CBA)
    val shadowSoft = Color(0x3A000000)
    val shadowSubtle = Color(0x1A000000)
    val scrimSoft = Color(0x33000000)
    val textPrimary = Color(0xFFF1F6FA)
    val textSecondary = Color(0xFFA8B9C5)
    val textMuted = Color(0xFF7D909D)
    val success = Color(0xFF73E5B0)
    val warning = Color(0xFFFFC777)
    val danger = Color(0xFFFF7189)
    val glassShine = Color(0x48FFFFFF)

    /** Tint added above an existing surface when the user's blur preference is zero. */
    const val sharpGlassTintAlpha = 0.12f
}

/**
 * 屏幕高度低于该阈值时（横屏手机），外壳与页面切换为紧凑排版，
 * 把垂直空间留给内容，而不是让导航与工具栏占满屏幕。
 */
const val AERIX_COMPACT_HEIGHT_THRESHOLD_DP = 520

object AerixSpacing {
    val zero: Dp = 0.dp
    val hairline: Dp = 1.dp
    val xxs: Dp = 2.dp
    val tiny: Dp = 3.dp
    val xs: Dp = 4.dp
    val xsPlus: Dp = 5.dp
    val smCompact: Dp = 6.dp
    val smTight: Dp = 7.dp
    val sm: Dp = 8.dp
    val smPlus: Dp = 10.dp
    val smNarrow: Dp = 9.dp
    val mdTight: Dp = 11.dp
    val md: Dp = 12.dp
    val mdPlus: Dp = 14.dp
    val lg: Dp = 16.dp
    val lgPlus: Dp = 18.dp
    val xl: Dp = 20.dp
    val xlPlus: Dp = 22.dp
    val xxl: Dp = 24.dp
    val xxxl: Dp = 32.dp
    val section: Dp = 40.dp
}

object AerixLayoutDefaults {
    /**
     * 页面内容区留白。矮屏（横屏手机）自动收紧，把高度留给内容本身。
     */
    @Composable
    fun pagePadding(
        horizontal: Dp = AerixSpacing.lg,
        vertical: Dp = AerixSpacing.lg
    ): PaddingValues {
        val compact = LocalConfiguration.current.screenHeightDp < AERIX_COMPACT_HEIGHT_THRESHOLD_DP
        return PaddingValues(
            horizontal = if (compact) AerixSpacing.md else horizontal,
            vertical = if (compact) AerixSpacing.sm else vertical
        )
    }
}

object AerixRadii {
    val square: Dp = 0.dp
    val tiny: Dp = 4.dp
    val micro: Dp = 6.dp
    val compact: Dp = 8.dp
    val controlSmall: Dp = 10.dp
    val control: Dp = 12.dp
    val cardSmall: Dp = 16.dp
    val card: Dp = 18.dp
    val cardLarge: Dp = 20.dp
    val panelSmall: Dp = 22.dp
    val panel: Dp = 24.dp
    val dialog: Dp = 28.dp
    val hero: Dp = 32.dp
    val pill: Dp = 50.dp
}

object AerixMetrics {
    val pillTabHeight: Dp = 38.dp
    val pageHeaderHeight: Dp = 44.dp
    val shellHeaderHeight: Dp = 60.dp
    val shellActionHeight: Dp = 40.dp
    /** 横屏手机等矮屏使用的外壳顶栏高度 */
    val shellHeaderHeightCompact: Dp = 48.dp
    /** 横屏手机等矮屏使用的顶栏按钮高度 */
    val shellActionHeightCompact: Dp = 36.dp
    val navigationRailWidth: Dp = 68.dp
    val expandedNavigationRailWidth: Dp = 78.dp
    val favoritesEmptyStateTopInset: Dp = 80.dp
    val exportTreeLabelStartInset: Dp = 46.dp
    val expandedKeyboardEndInset: Dp = 58.dp
    val assetSearchBottomInset: Dp = 58.dp
    val glassSurfaceElevation: Dp = 1.dp
    val glassFloatingElevation: Dp = 3.dp
    val glassRailElevation: Dp = 2.dp
    val glassDialogElevation: Dp = 3.dp
    val glassSelectedElevation: Dp = 0.dp
    val glassSubtleElevation: Dp = 0.dp
}
