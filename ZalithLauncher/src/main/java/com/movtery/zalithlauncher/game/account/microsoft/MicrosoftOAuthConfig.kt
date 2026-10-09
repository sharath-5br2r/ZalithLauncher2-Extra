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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.game.account.microsoft

/**
 * OAuth settings must match the client registration. Minecraft Java's built-in public client
 * ID is registered for the legacy Microsoft Account (Live) endpoints; sending it to the Entra
 * `/consumers` v2 endpoint returns AADSTS700016. Custom client IDs configured by packagers use
 * the Entra v2 device-code flow instead.
 */
internal data class MicrosoftOAuthConfig(
    val deviceCodeUrl: String,
    val tokenUrl: String,
    val scope: String,
    val deviceCodeResponseType: String?,
    val xblRpsTicketPrefix: String,
) {
    companion object {
        const val MINECRAFT_JAVA_CLIENT_ID = "00000000402b5328"

        private val legacyLive = MicrosoftOAuthConfig(
            deviceCodeUrl = "https://login.live.com/oauth20_connect.srf",
            tokenUrl = "https://login.live.com/oauth20_token.srf",
            scope = "service::user.auth.xboxlive.com::MBI_SSL",
            deviceCodeResponseType = "device_code",
            xblRpsTicketPrefix = "t=",
        )

        private val entraV2 = MicrosoftOAuthConfig(
            deviceCodeUrl = "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode",
            tokenUrl = "https://login.microsoftonline.com/consumers/oauth2/v2.0/token",
            scope = "XboxLive.signin offline_access",
            deviceCodeResponseType = null,
            xblRpsTicketPrefix = "d=",
        )

        fun forClientId(clientId: String): MicrosoftOAuthConfig =
            if (clientId.trim().equals(MINECRAFT_JAVA_CLIENT_ID, ignoreCase = true)) {
                legacyLive
            } else {
                entraV2
            }
    }
}
