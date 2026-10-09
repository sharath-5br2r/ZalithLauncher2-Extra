/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
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

package com.movtery.zalithlauncher.ui.screens.content.elements

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.path.GLOBAL_CLIENT
import com.movtery.zalithlauncher.ui.theme.AerixRadii
import com.movtery.zalithlauncher.ui.theme.AerixSpacing
import com.movtery.zalithlauncher.ui.theme.AerixSurface
import com.movtery.zalithlauncher.ui.theme.MiraiThemeManager
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** 皮肤库里的一款皮肤 */
data class LibrarySkin(
    /** 皮肤所有者（用于拉取预览与皮肤文件） */
    val owner: String,
    /** 展示名称 */
    val label: String = owner,
    /** 是否使用纤细（Alex）模型 */
    val slim: Boolean = false
)

/** 皮肤库：默认展示的热门皮肤，输入名称后可以从 Mojang 名字查询皮肤 */
val LibrarySkins: List<LibrarySkin> = listOf(
    "Notch", "jeb_", "Dinnerbone", "Dream", "GeorgeNotFound", "Sapnap", "Technoblade",
    "TommyInnit", "Ph1LzA", "Ranboo", "Tubbo", "CaptainSparklez", "Steve", "Alex",
    "Mumbo", "Grian", "GoodTimesWithScar", "Xisuma", "Iskall85", "FalseSymmetry",
    "LDShadowLady", "Smallishbeans", "Etho", "VintageBeef", "BdoubleO100", "ZombieCleo",
    "Docm77", "Tango", "ImpulseSV", "Skizzleman"
).map { LibrarySkin(it) }

private fun skinAvatarUrl(owner: String, size: Int) = "https://minotar.net/avatar/$owner/$size.png"
private fun skinBodyUrl(owner: String) = "https://minotar.net/body/$owner/180.png"

/**
 * 皮肤库面板：上方搜索框，下方三列可滚动皮肤网格。
 * 点击任意皮肤会弹出预览对话框，确认后由调用方应用皮肤。
 */
@Composable
fun SkinLibraryPanel(
    modifier: Modifier = Modifier,
    installingSkin: String? = null,
    onInstall: (LibrarySkin) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var skins by remember { mutableStateOf(LibrarySkins) }
    var searching by remember { mutableStateOf(false) }
    var previewSkin by remember { mutableStateOf<LibrarySkin?>(null) }
    val scope = rememberCoroutineScope()

    fun search() {
        val name = query.trim()
        if (name.isEmpty()) {
            skins = LibrarySkins
            return
        }
        scope.launch {
            searching = true
            skins = lookupLibrarySkins(name)
            searching = false
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact)
        ) {
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(AerixRadii.control),
                color = AerixSurface.panelRaised,
                border = androidx.compose.foundation.BorderStroke(
                    AerixSpacing.hairline,
                    AerixSurface.borderSoft
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .padding(horizontal = AerixSpacing.smPlus),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null,
                        tint = AerixSurface.textSecondary,
                        modifier = Modifier.size(15.dp)
                    )

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Search skins from MC Skin...",
                                style = MaterialTheme.typography.labelMedium,
                                color = AerixSurface.textMuted,
                                maxLines = 1
                            )
                        }
                        androidx.compose.foundation.text.BasicTextField(
                            value = query,
                            onValueChange = {
                                query = it
                                if (it.isEmpty()) skins = LibrarySkins
                            },
                            singleLine = true,
                            textStyle = MaterialTheme.typography.labelMedium.copy(
                                color = AerixSurface.textPrimary
                            ),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(
                                MiraiThemeManager.currentAccent()
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = { query = ""; skins = LibrarySkins },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = null,
                                tint = AerixSurface.textSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(AerixRadii.control),
                color = MiraiThemeManager.currentAccent(),
                contentColor = AerixSurface.onAccent,
                onClick = { search() }
            ) {
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .padding(horizontal = AerixSpacing.mdPlus),
                    contentAlignment = Alignment.Center
                ) {
                    if (searching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(15.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Search",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(bottom = AerixSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact),
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.smCompact)
        ) {
            items(skins, key = { it.owner }) { skin ->
                SkinTile(
                    skin = skin,
                    installing = installingSkin == skin.owner,
                    onClick = { previewSkin = skin }
                )
            }
        }
    }

    previewSkin?.let { skin ->
        SkinInstallDialog(
            skin = skin,
            installing = installingSkin == skin.owner,
            onDismiss = { previewSkin = null },
            onInstall = {
                previewSkin = null
                onInstall(skin)
            }
        )
    }
}

@Composable
private fun SkinTile(
    skin: LibrarySkin,
    installing: Boolean,
    onClick: () -> Unit
) {
    var bitmap by remember(skin.owner) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(skin.owner) {
        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                val bytes = GLOBAL_CLIENT.get(skinAvatarUrl(skin.owner, 96)).bodyAsBytes()
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }.getOrNull()
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(AerixRadii.cardSmall),
        color = AerixSurface.panel,
        border = androidx.compose.foundation.BorderStroke(
            AerixSpacing.hairline,
            AerixSurface.borderSoft
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AerixSpacing.smCompact),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AerixSpacing.xxs)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(AerixRadii.compact)),
                contentAlignment = Alignment.Center
            ) {
                val image = bitmap
                if (installing || image == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Image(
                        bitmap = image.asImageBitmap(),
                        contentDescription = skin.label,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Text(
                text = skin.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = AerixSurface.textPrimary,
                maxLines = 1
            )

            Text(
                text = if (skin.slim) "Slim" else "Classic",
                style = MaterialTheme.typography.labelSmall,
                color = AerixSurface.textMuted,
                maxLines = 1
            )
        }
    }
}

/** 点击皮肤后的小弹窗：预览 + 安装按钮 */
@Composable
private fun SkinInstallDialog(
    skin: LibrarySkin,
    installing: Boolean,
    onDismiss: () -> Unit,
    onInstall: () -> Unit
) {
    var bitmap by remember(skin.owner) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(skin.owner) {
        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                val bytes = GLOBAL_CLIENT.get(skinBodyUrl(skin.owner)).bodyAsBytes()
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            }.getOrNull()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(AerixRadii.dialog),
        containerColor = AerixSurface.panelRaised,
        title = {
            Text(
                text = skin.label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AerixSurface.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AerixSpacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 120.dp, height = 170.dp)
                        .clip(RoundedCornerShape(AerixRadii.cardSmall)),
                    contentAlignment = Alignment.Center
                ) {
                    val image = bitmap
                    if (image == null) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = skin.label,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Text(
                    text = if (skin.slim) "Slim model (Alex)" else "Classic model (Steve)",
                    style = MaterialTheme.typography.labelSmall,
                    color = AerixSurface.textSecondary
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !installing,
                shape = RoundedCornerShape(AerixRadii.control),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AerixSurface.action,
                    contentColor = AerixSurface.onAction
                ),
                onClick = onInstall
            ) {
                if (installing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_download),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(AerixSpacing.smCompact))
                    Text(
                        text = "Install",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.generic_cancel))
            }
        }
    )
}

/**
 * 搜索皮肤：先按 Mojang 名字精确查询，再从内置热门列表中匹配。
 */
suspend fun lookupLibrarySkins(query: String): List<LibrarySkin> = withContext(Dispatchers.IO) {
    val name = query.trim()
    val exact = runCatching {
        val response = GLOBAL_CLIENT.get("https://api.mojang.com/users/profiles/minecraft/$name")
        if (response.status.isSuccess()) {
            val body = response.bodyAsBytes().decodeToString()
            Regex("\"name\"\\s*:\\s*\"([^\"]+)\"").find(body)?.groupValues?.get(1)
        } else null
    }.getOrNull()?.let { LibrarySkin(it) }

    val matches = LibrarySkins.filter { it.owner.contains(name, ignoreCase = true) }
    (listOfNotNull(exact) + matches.filter { it.owner != exact?.owner })
        .ifEmpty { listOf(LibrarySkin(name, name)) }
}
