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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.game.launch

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MinecraftPlatformCompatibilityTest {
    @Test
    fun applies64BitRequirementFromMinecraft1205() {
        assertFalse(MinecraftPlatformCompatibility.requires64BitOperatingSystem("1.20.4"))
        assertTrue(MinecraftPlatformCompatibility.requires64BitOperatingSystem("1.20.5"))
        assertTrue(MinecraftPlatformCompatibility.requires64BitOperatingSystem("1.21.1"))
        assertTrue(MinecraftPlatformCompatibility.requires64BitOperatingSystem("1.21.1-forge"))
        assertTrue(MinecraftPlatformCompatibility.requires64BitOperatingSystem("26.1"))
    }

    @Test
    fun leavesUnknownVersionIdentifiersUnblocked() {
        assertFalse(MinecraftPlatformCompatibility.requires64BitOperatingSystem("24w14a"))
        assertFalse(MinecraftPlatformCompatibility.requires64BitOperatingSystem("custom-version"))
    }

    @Test
    fun onlyWarnsWhenA64BitOnlyReleaseIsAttemptedFromA32BitPlatform() {
        assertFalse(
            MinecraftPlatformCompatibility.needsBestEffort32BitWarning(
                minecraftVersion = "1.21.1",
                supports64BitOperatingSystem = true,
                is64BitProcess = true
            )
        )
        assertTrue(
            MinecraftPlatformCompatibility.needsBestEffort32BitWarning(
                minecraftVersion = "1.21.1",
                supports64BitOperatingSystem = true,
                is64BitProcess = false
            )
        )
        assertTrue(
            MinecraftPlatformCompatibility.needsBestEffort32BitWarning(
                minecraftVersion = "1.21.1",
                supports64BitOperatingSystem = false,
                is64BitProcess = false
            )
        )
        assertFalse(
            MinecraftPlatformCompatibility.needsBestEffort32BitWarning(
                minecraftVersion = "1.20.4",
                supports64BitOperatingSystem = false,
                is64BitProcess = false
            )
        )
    }
}
