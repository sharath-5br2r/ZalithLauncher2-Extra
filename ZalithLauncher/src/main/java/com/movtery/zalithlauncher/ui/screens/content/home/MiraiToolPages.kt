package com.movtery.zalithlauncher.ui.screens.content.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.io.File
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface

private val PageBg = AerixSurface.canvas
private val CardBg = AerixSurface.canvas
private val Muted = AerixSurface.textMuted
private val Green: Color
    @Composable get() = AerixSurface.accent

@Composable
fun MiraiAccountsPage(onOffline: () -> Unit, onMicrosoft: () -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(PageBg).padding(AerixSpacing.xl), verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)) {
        Text("Back", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onBack))
        Text("Accounts", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text("Add an offline account or sign in with Microsoft. The login step uses the existing account flow.", color = Muted)
        ActionCard("Offline", "Create or select an offline account", onOffline)
        ActionCard("Microsoft", "Sign in and use that account", onMicrosoft)
    }
}

@Composable
fun MiraiExportPage(instanceName: String?, onExport: () -> Unit, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(PageBg).padding(AerixSpacing.xl), verticalArrangement = Arrangement.spacedBy(AerixSpacing.md)) {
        Text("Back", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onBack))
        Text("Export", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text(instanceName ?: "Select an instance on Play first.", color = Muted)
        if (instanceName != null) ActionCard("Export instance", "Pack this instance with the existing export tool", onExport)
    }
}

@Composable
fun MiraiLogPage(path: String?, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val text = remember(path) {
        if (path.isNullOrBlank()) "Select an instance on Play first."
        else runCatching { File(path).takeIf { it.exists() }?.readText() }.getOrNull()?.takeIf { it.isNotBlank() } ?: "No log yet."
    }
    Column(modifier = modifier.fillMaxSize().background(PageBg).padding(AerixSpacing.xl)) {
        Text("Back", color = Green, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onBack))
        Text("Latest log", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = AerixSpacing.md))
        Text(text, color = Color.White, style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()))
    }
}

@Composable
private fun ActionCard(title: String, body: String, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.card)).background(CardBg).clickable(onClick = onClick).padding(AerixSpacing.lg)) {
        Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
        Text(body, color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}
