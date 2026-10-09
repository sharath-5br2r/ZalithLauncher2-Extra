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

package com.movtery.zalithlauncher.utils.platform

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MemoryUtilsTest {
    @Test
    fun capsHeapFor32BitProcessWithinThe1536MbCeilingEvenOn64BitHardware() {
        assertEquals(1536, maxMemoryForSettings(deviceRamMb = 8192.0, is64BitProcess = false))
        assertEquals(1536, maxMemoryForSettings(deviceRamMb = 6144.0, is64BitProcess = false))
    }

    @Test
    fun preservesPhysicalMemoryHeadroomForLowMemory32BitDevices() {
        assertEquals(1248, maxMemoryForSettings(deviceRamMb = 2048.0, is64BitProcess = false))
        assertEquals(736, maxMemoryForSettings(deviceRamMb = 1536.0, is64BitProcess = false))
    }

    @Test
    fun keepsExisting64BitAndLowMemoryLimits() {
        assertEquals(7168, maxMemoryForSettings(deviceRamMb = 8192.0, is64BitProcess = true))
        assertEquals(1024, maxMemoryForSettings(deviceRamMb = 1536.0, is64BitProcess = true))
    }

    @Test
    fun clamps32BitLaunchHeapToLargestContiguousAddressSpaceWithHeadroom() {
        assertEquals(672, maxMemoryForLaunch(
            settingsLimitMb = 1024,
            is64BitProcess = false,
            largestAddressSpaceHoleMb = 800
        ))
    }

    @Test
    fun neverLetsA32BitLaunchHeapExceedThe1536MbCeiling() {
        assertEquals(1536, maxMemoryForLaunch(
            settingsLimitMb = 1536,
            is64BitProcess = false,
            largestAddressSpaceHoleMb = 4096
        ))
    }

    @Test
    fun keepsTheMinimumJvmHeapWhenAddressSpaceIsHighlyFragmented() {
        assertEquals(256, maxMemoryForLaunch(
            settingsLimitMb = 1024,
            is64BitProcess = false,
            largestAddressSpaceHoleMb = 300
        ))
    }

    @Test
    fun leaves64BitAndUnknownAddressSpaceLimitsUnchanged() {
        assertEquals(1024, maxMemoryForLaunch(
            settingsLimitMb = 1024,
            is64BitProcess = true,
            largestAddressSpaceHoleMb = 800
        ))
        assertEquals(1024, maxMemoryForLaunch(
            settingsLimitMb = 1024,
            is64BitProcess = false,
            largestAddressSpaceHoleMb = null
        ))
    }

    @Test
    fun parsesLargestFreeRangeFromProcMapsBelowThe32BitCeiling() {
        val maps = """
            00001000-00002000 r-xp 00000000 00:00 0
            00005000-00007000 rw-p 00000000 00:00 0
            00009000-0000a000 r--p 00000000 00:00 0
        """.trimIndent()

        assertEquals(0x6000L, largestAddressSpaceHoleBytes(maps, 0x10000L))
    }

    @Test
    fun ignoresMalformedMappingsAndReturnsUnknownWhenNoneCanBeParsed() {
        assertNull(largestAddressSpaceHoleBytes("not a proc maps line", 0x10000L))
    }

    @Test
    fun clampsMappingsThatCrossTheAddressSpaceLimit() {
        val maps = """
            00008000-00012000 rw-p 00000000 00:00 0
            00001000-00002000 r-xp 00000000 00:00 0
        """.trimIndent()

        assertEquals(0x6000L, largestAddressSpaceHoleBytes(maps, 0x10000L))
    }

    @Test
    fun clampsPreviouslySavedAllocationsToTheCurrentProcessLimit() {
        val maxRamMb = maxMemoryForSettings(deviceRamMb = 8192.0, is64BitProcess = false)
        assertEquals(1536, clampRamAllocation(requestedRamMb = 6144, maxRamMb = maxRamMb))
        assertEquals(768, clampRamAllocation(requestedRamMb = 768, maxRamMb = maxRamMb))
    }
}
