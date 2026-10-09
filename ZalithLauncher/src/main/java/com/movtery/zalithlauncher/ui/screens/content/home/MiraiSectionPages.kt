package com.movtery.zalithlauncher.ui.screens.content.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage

private val PageBg = AerixSurface.canvas
private val CardBg = AerixSurface.canvas
private val Muted = AerixSurface.textMuted
private val Green: Color
    @Composable get() = AerixSurface.accent

data class SectionAction(val title: String, val subtitle: String, val onClick: () -> Unit)

@Composable
fun MiraiDiscoverPage(onMods: () -> Unit, onModpacks: () -> Unit, onResourcePacks: () -> Unit, onShaders: () -> Unit, onWorlds: () -> Unit, onVersions: () -> Unit, onFavorites: () -> Unit, onSearchId: () -> Unit, modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    val actions = listOf(
        SectionAction("Mods", "Search and install mods", onMods),
        SectionAction("Modpacks", "Install a full pack", onModpacks),
        SectionAction("Resource packs", "Textures and sounds", onResourcePacks),
        SectionAction("Shaders", "Shader packs", onShaders),
        SectionAction("Worlds", "Save downloads", onWorlds),
        SectionAction("Game versions", "Create an instance", onVersions),
        SectionAction("Favorites", "Saved projects", onFavorites),
        SectionAction("Search by ID", "Open a project id", onSearchId)
    ).filter { query.isBlank() || it.title.contains(query, true) || it.subtitle.contains(query, true) }
    Column(modifier = modifier.fillMaxSize().background(PageBg).padding(AerixSpacing.xl)) {
        Text("Discover", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(AerixSpacing.md))
        BasicTextField(value = query, onValueChange = { query = it }, singleLine = true, textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White), cursorBrush = SolidColor(Green), modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.control)).background(CardBg).padding(AerixSpacing.mdPlus), decorationBox = { inner -> if (query.isEmpty()) Text("Search mods, packs, shaders", color = Muted); inner() })
        Spacer(Modifier.height(AerixSpacing.lg))
        if (actions.isEmpty()) Text("No matching category.", color = Muted) else LazyVerticalGrid(columns = GridCells.Adaptive(220.dp), horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md), verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)) {
            items(actions) { action ->
                Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.card)).background(CardBg).clickable(onClick = action.onClick).padding(AerixSpacing.lg)) {
                    Text(action.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(AerixSpacing.xs))
                    Text(action.subtitle, color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun MiraiLibraryPage(onOpenInstance: (Version) -> Unit, onFiles: () -> Unit, modifier: Modifier = Modifier) {
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()
    Column(modifier = modifier.fillMaxSize().background(PageBg).padding(AerixSpacing.xl)) {
        Text("Library", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(AerixSpacing.smCompact))
        Text("Installed instances. Open one for its content and settings.", color = Muted)
        Spacer(Modifier.height(AerixSpacing.lg))
        if (versions.isEmpty()) Text("No instances yet. Create one from Play.", color = Muted) else LazyColumn(verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm), contentPadding = PaddingValues(bottom = AerixSpacing.xxl)) {
            items(versions, key = { it.getVersionName() }) { version ->
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.cardSmall)).background(CardBg).clickable { onOpenInstance(version) }.padding(AerixSpacing.md), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md)) {
                    VersionIconImage(version = version, modifier = Modifier.size(36.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(version.getVersionName(), color = Color.White, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Instance", color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                    Text("Open", color = Green, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(AerixSpacing.sm))
        Text("Files", color = Green, modifier = Modifier.clickable(onClick = onFiles), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun MiraiServersPage(onMultiplayer: () -> Unit, modifier: Modifier = Modifier) {
    val enabled = AllSettings.enableTerracotta.state
    Column(modifier = modifier.fillMaxSize().background(PageBg).padding(AerixSpacing.xl), verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)) {
        Text("Servers", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text("Join a server or host one from this launcher.", color = Muted)
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.card)).background(CardBg).padding(AerixSpacing.lg), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Multiplayer", color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(if (enabled) "Terracotta is on" else "Terracotta is off", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = enabled, onCheckedChange = { AllSettings.enableTerracotta.save(it) })
        }
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.card)).background(CardBg).clickable(onClick = onMultiplayer).padding(AerixSpacing.lg)) {
            Text("Join", color = Color.White, fontWeight = FontWeight.SemiBold)
            Text("Open the server list and invite flow.", color = Muted, style = MaterialTheme.typography.bodySmall)
        }
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.card)).background(CardBg).clickable(onClick = onMultiplayer).padding(AerixSpacing.lg)) {
            Text("Host", color = Color.White, fontWeight = FontWeight.SemiBold)
            Text("Share a world and copy an invite.", color = Muted, style = MaterialTheme.typography.bodySmall)
        }
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.card)).background(CardBg).clickable(onClick = onMultiplayer).padding(AerixSpacing.lg)) {
            Text("Nodes and logs", color = Color.White, fontWeight = FontWeight.SemiBold)
            Text("Custom nodes and Terracotta logs.", color = Muted, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun MiraiSettingsPage(onRenderer: () -> Unit, onGame: () -> Unit, onControls: () -> Unit, onGamepad: () -> Unit, onLauncher: () -> Unit, onJava: () -> Unit, onControlLayouts: () -> Unit, onAccounts: () -> Unit, onAbout: () -> Unit, onExport: () -> Unit = {}, onSkins: () -> Unit = {}, onFiles: () -> Unit = {}, onLogs: () -> Unit = {}, onWeb: () -> Unit = {}, onTutorial: () -> Unit = {}, modifier: Modifier = Modifier) {
    SectionPage("Settings", "Launcher, game, controls, and accounts.", listOf(
        SectionAction("Renderer", "Resolution, renderer, and RAM", onRenderer),
        SectionAction("Game", "Game options", onGame),
        SectionAction("Controls", "Touch controls", onControls),
        SectionAction("Control layouts", "Manage control layouts", onControlLayouts),
        SectionAction("Gamepad", "Controller options", onGamepad),
        SectionAction("Launcher", "Launcher behavior", onLauncher),
        SectionAction("Java", "Java runtime", onJava),
        SectionAction("Accounts", "Offline and Microsoft login", onAccounts),
        SectionAction("Export", "Export the selected instance", onExport),
        SectionAction("Licenses", "Open source licenses", onAbout),
        SectionAction("Skins", "Browse and equip a skin", onSkins),
        SectionAction("Files", "Launcher files", onFiles),
        SectionAction("Logs", "Latest game log", onLogs),
        SectionAction("Web", "Open a page in the browser", onWeb),
        SectionAction("Tutorial", "How this launcher is laid out", onTutorial),
        SectionAction("About", "Version and licenses", onAbout)
    ), modifier)
}

@Composable
private fun SectionPage(title: String, subtitle: String, actions: List<SectionAction>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(PageBg).padding(AerixSpacing.xl)) {
        Text(title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(AerixSpacing.smCompact))
        Text(subtitle, color = Muted, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(AerixSpacing.lgPlus))
        LazyVerticalGrid(columns = GridCells.Adaptive(220.dp), horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md), verticalArrangement = Arrangement.spacedBy(AerixSpacing.md), contentPadding = PaddingValues(bottom = AerixSpacing.xxl)) {
            items(actions) { action ->
                Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.card)).background(CardBg).clickable(onClick = action.onClick).padding(AerixSpacing.lg)) {
                    Text(action.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(AerixSpacing.xs))
                    Text(action.subtitle, color = Muted, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(AerixSpacing.smPlus))
                    Text("Open", color = Green, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
