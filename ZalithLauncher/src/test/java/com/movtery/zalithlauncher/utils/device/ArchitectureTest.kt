/*
 * Zalith Launcher 2
 * Copyright (C) 2026 Aerix Launcher contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.utils.device

import org.junit.Assert.assertEquals
import org.junit.Test

class ArchitectureTest {
    @Test
    fun selectsTheAppProcessAbiRatherThanTheDeviceMaximumAbi() {
        // A 32-bit APK may be installed on an arm64 phone. Native LWJGL/JRE files must
        // still use the 32-bit process ABI in that case.
        assertEquals(
            Architecture.ARCH_ARM,
            Architecture.selectProcessArchitecture(
                is64BitProcess = false,
                supported32BitAbis = arrayOf("armeabi-v7a"),
                supported64BitAbis = arrayOf("arm64-v8a")
            )
        )
        assertEquals(
            Architecture.ARCH_ARM64,
            Architecture.selectProcessArchitecture(
                is64BitProcess = true,
                supported32BitAbis = arrayOf("armeabi-v7a"),
                supported64BitAbis = arrayOf("arm64-v8a")
            )
        )
    }

    @Test
    fun detectsX86AbisForBothProcessWidths() {
        assertEquals(
            Architecture.ARCH_X86,
            Architecture.selectProcessArchitecture(
                is64BitProcess = false,
                supported32BitAbis = arrayOf("x86"),
                supported64BitAbis = arrayOf("x86_64")
            )
        )
        assertEquals(
            Architecture.ARCH_X86_64,
            Architecture.selectProcessArchitecture(
                is64BitProcess = true,
                supported32BitAbis = arrayOf("x86"),
                supported64BitAbis = arrayOf("x86_64")
            )
        )
    }
}
