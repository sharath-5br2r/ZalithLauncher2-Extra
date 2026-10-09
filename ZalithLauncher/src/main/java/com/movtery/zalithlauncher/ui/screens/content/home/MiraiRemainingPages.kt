package com.movtery.zalithlauncher.ui.screens.content.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface

private val CardBg = AerixSurface.canvas
private val Muted = AerixSurface.textMuted
private val Green: Color
    @Composable get() = AerixSurface.accent

data class MoreAction(val title: String, val body: String, val onClick: () -> Unit)

@Composable
fun MiraiMorePages(
    onAccounts: () -> Unit,
    onExport: () -> Unit,
    onLicenses: () -> Unit,
    onSkins: () -> Unit,
    onFiles: () -> Unit,
    onLogs: () -> Unit,
    onWeb: () -> Unit,
    onTutorial: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val actions = listOf(
        MoreAction("Accounts", "Offline and Microsoft login", onAccounts),
        MoreAction("Export", "Export the selected instance", onExport),
        MoreAction("Licenses", "Open source licenses", onLicenses),
        MoreAction("Skins", "Browse and equip a skin", onSkins),
        MoreAction("Files", "Launcher files", onFiles),
        MoreAction("Logs", "Latest game log", onLogs),
        MoreAction("Web", "Open a page in the browser", onWeb),
        MoreAction("Tutorial", "How this launcher is laid out", onTutorial)
    )
    LazyVerticalGrid(columns = GridCells.Adaptive(200.dp), horizontalArrangement = Arrangement.spacedBy(AerixSpacing.md), verticalArrangement = Arrangement.spacedBy(AerixSpacing.md), modifier = modifier) {
        items(actions) { action ->
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.card)).background(CardBg).clickable(onClick = action.onClick).padding(AerixSpacing.lg)) {
                Text(action.title, color = Color.White, fontWeight = FontWeight.SemiBold)
                Text(action.body, color = Muted, style = MaterialTheme.typography.bodySmall)
                Text("Open", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = AerixSpacing.sm))
            }
        }
    }
}

@Composable
fun MiraiTutorialPage(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(AerixSpacing.xl), verticalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)) {
        Text("Back", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onBack))
        Text("Welcome", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text("Play selects an instance and launches it. Discover installs content. Library lists instances. Servers joins and hosts. Settings holds renderer, controls, Java, and accounts.", color = Muted)
    }
}
