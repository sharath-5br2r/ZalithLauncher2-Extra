package com.movtery.zalithlauncher.ui.screens.content.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.setting.AllSettings
import java.io.File

private val PageBg = AerixSurface.canvas
private val CardBg = AerixSurface.canvas
private val Muted = AerixSurface.textMuted
private val Green: Color
    @Composable get() = AerixSurface.accent

@Composable
fun MiraiInstanceDetail(version: Version, onOpenContent: () -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier) {
    var tab by remember { mutableStateOf("Mods") }
    val tabs = listOf("Mods", "Saves", "Resource packs", "Shaders", "Settings")
    Column(modifier = modifier.fillMaxSize().background(PageBg).padding(AerixSpacing.xl), verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)) {
        Text("Back", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onBack))
        Text(version.getVersionName(), color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text(if (version.isValid()) "Installed instance" else "This instance is missing files", color = Muted)
        Row(horizontalArrangement = Arrangement.spacedBy(AerixSpacing.sm)) {
            tabs.forEach { name ->
                Text(name, color = if (tab == name) AerixSurface.canvas else Color.White, modifier = Modifier.clip(RoundedCornerShape(AerixRadii.panelSmall)).background(if (tab == name) Green else CardBg).clickable { tab = name }.padding(horizontal = AerixSpacing.md, vertical = AerixSpacing.sm))
            }
        }
        when (tab) {
            "Settings" -> SettingsReadout(version)
            else -> FileList(version.getGameDir().resolve(folderFor(tab)))
        }
        Text("Advanced", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onOpenContent))
    }
}

@Composable
fun MiraiSettingsDetail(title: String, onOpenEditor: () -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(PageBg).padding(AerixSpacing.xl), verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)) {
        Text("Back", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onBack))
        Text(title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        ReadRow("Renderer", AllSettings.renderer.getValue())
        ReadRow("Java", AllSettings.javaRuntime.getValue().ifBlank { "Default" })
        ReadRow("Controls", AllSettings.controlLayout.getValue().ifBlank { "Default" })
        Text("Edit", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onOpenEditor))
    }
}

@Composable
fun MiraiDiscoverResults(title: String, onOpenSearch: () -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    Column(modifier = modifier.fillMaxSize().background(PageBg).padding(AerixSpacing.xl), verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)) {
        Text("Back", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onBack))
        Text(title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        BasicTextField(value = query, onValueChange = { query = it }, singleLine = true, textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White), cursorBrush = SolidColor(Green), modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.control)).background(CardBg).padding(AerixSpacing.mdPlus), decorationBox = { inner -> if (query.isEmpty()) Text("Search this category", color = Muted); inner() })
        Text(if (query.isBlank()) "Enter a name, then search to install." else "Search for $query", color = Muted)
        Text("Search", color = AerixSurface.canvas, fontWeight = FontWeight.SemiBold, modifier = Modifier.clip(RoundedCornerShape(AerixRadii.cardSmall)).background(Green).clickable(onClick = onOpenSearch).padding(AerixSpacing.mdPlus))
    }
}

@Composable
fun MiraiServerDetail(onOpen: () -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val enabled = AllSettings.enableTerracotta.state
    Column(modifier = modifier.fillMaxSize().background(PageBg).padding(AerixSpacing.xl), verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)) {
        Text("Back", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onBack))
        Text("Servers", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.card)).background(CardBg).padding(AerixSpacing.lg), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Multiplayer", color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(if (enabled) "On" else "Off", color = Muted)
            }
            Switch(checked = enabled, onCheckedChange = { AllSettings.enableTerracotta.save(it) })
        }
        InfoCard("Join", "Enable multiplayer, then enter an invite from the host.")
        InfoCard("Host", "Open a world to LAN, then copy the invite.")
        Text("Nodes and logs", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onOpen))
    }
}

@Composable
private fun SettingsReadout(version: Version) {
    Column(verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)) {
        ReadRow("Renderer", version.getRenderer())
        ReadRow("Java", version.getJavaRuntime().ifBlank { "Default" })
        ReadRow("Isolation", if (version.isIsolation()) "On" else "Off")
        ReadRow("Server", version.getServerIp() ?: "None")
    }
}

@Composable
private fun ReadRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.cardSmall)).background(CardBg).padding(AerixSpacing.mdPlus)) {
        Text(label, color = Muted, style = MaterialTheme.typography.bodySmall)
        Text(value, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.card)).background(CardBg).padding(AerixSpacing.lg)) {
        Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
        Text(body, color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun FileList(dir: File) {
    val files = remember(dir.absolutePath) { dir.listFiles()?.filter { it.isFile }?.sortedBy { it.name }.orEmpty() }
    if (!dir.exists() || files.isEmpty()) {
        Text("Nothing in ${dir.name} yet.", color = Muted)
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)) {
            items(files, key = { it.name }) { file ->
                Text(file.name, color = Color.White, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.cardSmall)).background(CardBg).padding(AerixSpacing.mdPlus), maxLines = 1)
            }
        }
    }
}

private fun folderFor(tab: String): String = when (tab) {
    "Saves" -> "saves"
    "Resource packs" -> "resourcepacks"
    "Shaders" -> "shaderpacks"
    else -> "mods"
}
