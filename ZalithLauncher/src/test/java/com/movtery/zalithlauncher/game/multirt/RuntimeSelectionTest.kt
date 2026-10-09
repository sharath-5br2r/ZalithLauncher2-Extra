/*
 * Aerix Launcher
 * Copyright (C) 2026 Aerix Launcher contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.game.multirt

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RuntimeSelectionTest {
    @Test
    fun selectsTheLowestCompatibleRuntimeThatMeetsTheMinimumJavaMajor() {
        val runtimes = listOf(
            runtime("Internal-17", 17, "arm"),
            runtime("Internal-25", 25, "arm"),
            runtime("Internal-21", 21, "arm")
        )

        val selected = selectRuntimeAtLeast(21, runtimes) { it.arch == "arm" }

        assertEquals("Internal-21", selected?.name)
    }

    @Test
    fun arm32ProcessSelectsThe32BitRuntimeEvenOnAnArm64CapableDevice() {
        val runtimes = listOf(
            runtime("Internal-17-arm64", 17, "aarch64"),
            runtime("Internal-17-arm", 17, "arm"),
            runtime("Internal-21-arm", 21, "arm"),
        )

        val selected = selectRuntimeAtLeast(17, runtimes) { it.arch == "arm" }

        assertEquals("Internal-17-arm", selected?.name)
    }

    @Test
    fun neverFallsBackToAnOlderJavaWhenTheMinimumIsUnavailable() {
        val runtimes = listOf(runtime("Internal-17", 17, "arm"))

        assertNull(selectRuntimeAtLeast(21, runtimes) { it.arch == "arm" })
    }

    @Test
    fun canUseANewerRuntimeWhenTheExactMajorHasNoMatchingAbi() {
        val runtimes = listOf(
            runtime("Internal-21-x86_64", 21, "x86_64"),
            runtime("Internal-25-arm", 25, "arm")
        )

        val selected = selectRuntimeAtLeast(21, runtimes) { it.arch == "arm" }

        assertEquals("Internal-25-arm", selected?.name)
    }

    private fun runtime(name: String, major: Int, arch: String) = Runtime(
        name = name,
        versionString = "$major.0.0",
        arch = arch,
        javaVersion = major,
        isProvidedByLauncher = true,
        isJDK8 = major == 8
    )
}
