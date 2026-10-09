package com.movtery.zalithlauncher.ui.screens.content.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.game.version.installed.VersionsManager

@Composable
fun ClassicHome(
    onAddAccount: () -> Unit,
    onCreateInstance: () -> Unit,
    onLaunch: () -> Unit,
    onDownload: () -> Unit,
    onSettings: () -> Unit,
    onFiles: () -> Unit,
    onAccounts: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()
    val count = versions.size
    Column(modifier.fillMaxSize().padding(AerixSpacing.lg)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Aerix Launcher  /  Main Menu", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text("Unofficial Modified Version", color = AerixSurface.textSecondary, fontSize = 12.sp)
            }
            TopAction("Files", onFiles)
            TopAction("Accounts", onAccounts)
            TopAction("Download", onDownload)
            TopAction("Settings", onSettings)
        }
        Spacer(Modifier.height(AerixSpacing.lg))
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(AerixSpacing.mdPlus)) {
            Column(Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(AerixSpacing.lg)) {
                Text("Jump in", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 28.sp)
                Text("Recently played, selected, and pinned installations", color = AerixSurface.textSecondary)
                GlassCard {
                    Text(if (count == 0) "Nothing to jump into yet" else "${versions.first().getVersionName()}", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(if (count == 0) "Install a version or pin one from your library for quick access." else "Selected installation", color = AerixSurface.textSecondary)
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Library", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 28.sp)
                        Text("$count installed", color = AerixSurface.textSecondary)
                    }
                    Text("+  New instance", color = AerixSurface.onAccent, fontWeight = FontWeight.SemiBold, modifier = Modifier.clip(RoundedCornerShape(AerixRadii.dialog)).background(AerixSurface.accent).clickable(onClick = onCreateInstance).padding(horizontal = AerixSpacing.lg, vertical = AerixSpacing.smPlus))
                }
                GlassCard {
                    Text(if (count == 0) "Your library is empty" else "${count} installed versions", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(if (count == 0) "Install a Minecraft version to get started." else "Tap New instance to add another, or Launch to play the selected one.", color = AerixSurface.textSecondary)
                    if (count == 0) {
                        Spacer(Modifier.height(AerixSpacing.sm))
                        Text("+  New instance", color = AerixSurface.onAccent, fontWeight = FontWeight.SemiBold, modifier = Modifier.clip(RoundedCornerShape(AerixRadii.dialog)).background(AerixSurface.accent).clickable(onClick = onCreateInstance).padding(horizontal = AerixSpacing.lg, vertical = AerixSpacing.smPlus))
                    }
                }
            }
            Column(
                Modifier.width(220.dp).fillMaxHeight().clip(RoundedCornerShape(AerixRadii.dialog)).background(AerixSurface.panel.copy(alpha = 0.4f)).padding(AerixSpacing.lgPlus),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.lg)
            ) {
                Text("+", color = Color.White, fontSize = 36.sp, modifier = Modifier.clickable(onClick = onAddAccount))
                Text("Add Account", color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onAddAccount))
                Spacer(Modifier.weight(1f))
                Text("$count installed versions", color = Color.White)
                Text("Launch", color = AerixSurface.onAccent, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.dialog)).background(AerixSurface.accent).clickable(onClick = onLaunch).padding(vertical = AerixSpacing.md))
            }
        }
    }
}

@Composable
private fun TopAction(label: String, onClick: () -> Unit) {
    Text(label, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = AerixSpacing.smCompact).clickable(onClick = onClick))
}

@Composable
private fun GlassCard(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.cardLarge)).background(AerixSurface.panel.copy(alpha = 0.4f)).padding(AerixSpacing.lg), verticalArrangement = Arrangement.spacedBy(AerixSpacing.xs)) { content() }
}
