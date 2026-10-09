package com.movtery.zalithlauncher.game.account.microsoft

import com.movtery.zalithlauncher.game.account.microsoft.models.DeviceCodeResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MicrosoftOAuthConfigTest {
    @Test
    fun minecraftJavaClientUsesLiveOAuthEndpoints() {
        val config = MicrosoftOAuthConfig.forClientId("00000000402b5328")

        assertEquals("https://login.live.com/oauth20_connect.srf", config.deviceCodeUrl)
        assertEquals("https://login.live.com/oauth20_token.srf", config.tokenUrl)
        assertEquals("service::user.auth.xboxlive.com::MBI_SSL", config.scope)
        assertEquals("device_code", config.deviceCodeResponseType)
        assertEquals("t=", config.xblRpsTicketPrefix)
    }

    @Test
    fun customClientUsesConsumersV2OAuthEndpoints() {
        val config = MicrosoftOAuthConfig.forClientId("custom-public-client-id")

        assertEquals(
            "https://login.microsoftonline.com/consumers/oauth2/v2.0/devicecode",
            config.deviceCodeUrl
        )
        assertEquals(
            "https://login.microsoftonline.com/consumers/oauth2/v2.0/token",
            config.tokenUrl
        )
        assertEquals("XboxLive.signin offline_access", config.scope)
        assertNull(config.deviceCodeResponseType)
        assertEquals("d=", config.xblRpsTicketPrefix)
    }

    @Test
    fun minecraftClientIdMatchingIgnoresWhitespaceAndCase() {
        val config = MicrosoftOAuthConfig.forClientId(" 00000000402B5328 ")

        assertEquals("https://login.live.com/oauth20_connect.srf", config.deviceCodeUrl)
    }

    @Test
    fun deviceCodeResponseAcceptsBothMicrosoftUrlFieldNames() {
        val liveResponse = Json.decodeFromString<DeviceCodeResponse>(
            """{"user_code":"ABCD-EFGH","device_code":"live-code","verification_uri":"https://www.microsoft.com/link","expires_in":900,"interval":5}"""
        )
        val entraResponse = Json.decodeFromString<DeviceCodeResponse>(
            """{"user_code":"IJKL-MNOP","device_code":"entra-code","verification_url":"https://microsoft.com/devicelogin","expires_in":900,"interval":5}"""
        )

        assertEquals("https://www.microsoft.com/link", liveResponse.verificationUrl)
        assertEquals("https://microsoft.com/devicelogin", entraResponse.verificationUrl)
        assertEquals("", liveResponse.message)
    }
}
