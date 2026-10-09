package com.movtery.zalithlauncher.ui.screens.content.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage

@Composable
fun MiraiPlayPage(
    onLaunch: (Version?) -> Unit,
    onExploreContent: () -> Unit,
    onCreateInstance: () -> Unit,
    onAddAccount: () -> Unit,
    onManageVersions: () -> Unit,
    onOpenVersionSettings: (Version) -> Unit,
    modifier: Modifier = Modifier,
) {
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()
    val current by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val selected = current ?: versions.firstOrNull()
    val glass = ButtonDefaults.buttonColors(containerColor = AerixSurface.accent.copy(alpha = 0.6f), contentColor = AerixSurface.onAccent)

    Column(modifier = modifier.fillMaxSize().padding(AerixSpacing.lg)) {
        Text("Instances", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(AerixSpacing.md))
        if (versions.isEmpty()) {
            Text("No instances yet.", color = AerixSurface.textSecondary)
            Spacer(Modifier.weight(1f))
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm), contentPadding = PaddingValues(bottom = AerixSpacing.md)) {
                items(versions, key = { it.getVersionName() }) { version ->
                    InstanceRow(version, version.getVersionName() == selected?.getVersionName()) {
                        VersionsManager.saveVersion(version)
                        onOpenVersionSettings(version)
                    }
                }
            }
        }
        Button(onClick = onAddAccount, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(AerixRadii.dialog), colors = glass) { Text("Add Account", fontWeight = FontWeight.SemiBold) }
        Spacer(Modifier.height(AerixSpacing.sm))
        Button(onClick = onCreateInstance, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(AerixRadii.dialog), colors = glass) { Text("Create instance", fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
private fun InstanceRow(version: Version, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(AerixRadii.control)).background(if (selected) AerixSurface.accent.copy(alpha = 0.8f) else AerixSurface.panel.copy(alpha = 0.2f)).clickable(onClick = onClick).padding(horizontal = AerixSpacing.md, vertical = AerixSpacing.smPlus),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smPlus)
    ) {
        Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(AerixRadii.compact)).background(AerixSurface.scrimSoft), contentAlignment = Alignment.Center) {
            VersionIconImage(version = version, modifier = Modifier.size(22.dp))
        }
        Text(version.getVersionName(), color = if (selected) AerixSurface.onAccent else Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
