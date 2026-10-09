/*
 * Zalith Launcher 2
 * Copyright (C) 2026 Aerix Launcher contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.game.launch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MinecraftRuntimeFlagsTest {
    @Test
    fun opensRequiredModulesAndEnablesLwjglNativeAccessForJava17Minecraft120() {
        val flags = MinecraftRuntimeFlags.forMinecraft(
            javaMajor = 17,
            minecraftVersion = "1.20",
            sharedLibraryExtractPath = "/private/cache/lwjgl",
        )

        assertTrue("--enable-native-access=ALL-UNNAMED" in flags)
        assertTrue("--add-opens=java.base/java.lang=ALL-UNNAMED" in flags)
        assertTrue("--add-opens=java.base/sun.nio.ch=ALL-UNNAMED" in flags)
        assertTrue("--add-opens=java.desktop/sun.awt=ALL-UNNAMED" in flags)
        assertTrue("-Dorg.lwjgl.system.allocator=system" in flags)
        assertTrue("-Dorg.lwjgl.system.SharedLibraryExtractPath=/private/cache/lwjgl" in flags)
    }

    @Test
    fun appliesThroughMinecraft26AndOnJava21OrNewer() {
        val flags = MinecraftRuntimeFlags.forMinecraft(
            javaMajor = 21,
            minecraftVersion = "26.1",
            sharedLibraryExtractPath = "/cache",
        )

        assertTrue("--enable-native-access=ALL-UNNAMED" in flags)
        assertTrue(flags.any { it.startsWith("--add-opens=") })
    }

    @Test
    fun leavesPre120GamesAndPre17JavaWithoutModernFlags() {
        assertFalse(
            MinecraftRuntimeFlags.forMinecraft(17, "1.19.4", "/cache").isNotEmpty()
        )
        assertFalse(
            MinecraftRuntimeFlags.forMinecraft(16, "1.20.1", "/cache").isNotEmpty()
        )
        assertFalse(
            MinecraftRuntimeFlags.forMinecraft(21, "", "/cache").isNotEmpty()
        )
    }
}
