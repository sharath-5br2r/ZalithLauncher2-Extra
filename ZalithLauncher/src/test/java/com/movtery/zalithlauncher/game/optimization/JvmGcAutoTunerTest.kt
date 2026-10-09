/*
 * Zalith Launcher 2
 * Copyright (C) 2026 Aerix Launcher contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.game.optimization

import com.movtery.zalithlauncher.game.addons.modloader.ModLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class JvmGcAutoTunerTest {

    @Test
    fun recommendsExpectedJavaVersionAcrossMinecraftReleases() {
        assertEquals(8, JvmGcAutoTuner.recommendJavaMajorVersion("1.12.2", ModLoader.FORGE))
        assertEquals(8, JvmGcAutoTuner.recommendJavaMajorVersion("1.16.5", ModLoader.FABRIC))
        assertEquals(17, JvmGcAutoTuner.recommendJavaMajorVersion("1.18.2", ModLoader.FABRIC))
        assertEquals(17, JvmGcAutoTuner.recommendJavaMajorVersion("1.20.1", ModLoader.FORGE))
        assertEquals(21, JvmGcAutoTuner.recommendJavaMajorVersion("1.20.5", ModLoader.FABRIC))
        assertEquals(21, JvmGcAutoTuner.recommendJavaMajorVersion("1.21.4", ModLoader.NEOFORGE))
    }

    @Test
    fun sanitizesZgcOnJava8AndInjectsLowPauseG1Gc() {
        val args = mutableListOf(
            "-Xms2048M",
            "-Xmx2048M",
            "-XX:+UseZGC",
            "-XX:+ZGenerational"
        )
        JvmGcAutoTuner.sanitizeAndInjectGcArgs(
            args, javaMajor = 8, ramAllocationMb = 2048, is64BitRuntime = true
        )

        assertFalse(args.contains("-XX:+UseZGC"))
        assertFalse(args.contains("-XX:+ZGenerational"))
        assertTrue(args.contains("-XX:+UseG1GC"))
        assertTrue(args.contains("-XX:MaxGCPauseMillis=50"))
    }

    @Test
    fun automaticallyUsesSerialGcAndSmallCodeCacheForLowHeap() {
        val args = mutableListOf<String>()
        JvmGcAutoTuner.sanitizeAndInjectGcArgs(
            args, javaMajor = 21, ramAllocationMb = 1024, is64BitRuntime = false
        )

        assertTrue(args.contains("-XX:+UseSerialGC"))
        assertTrue(args.contains("-XX:ReservedCodeCacheSize=48M"))
        assertFalse(args.contains("-XX:+UseG1GC"))
    }

    @Test
    fun keepsExplicitCollectorAndCodeCacheChoicesOnLowHeap() {
        val args = mutableListOf("-XX:+UseG1GC", "-XX:ReservedCodeCacheSize=96M")
        JvmGcAutoTuner.sanitizeAndInjectGcArgs(
            args, javaMajor = 21, ramAllocationMb = 1024, is64BitRuntime = true
        )

        assertTrue(args.contains("-XX:+UseG1GC"))
        assertFalse(args.contains("-XX:+UseSerialGC"))
        assertTrue(args.contains("-XX:ReservedCodeCacheSize=96M"))
        assertFalse(args.contains("-XX:ReservedCodeCacheSize=48M"))
    }

    @Test
    fun keepsCustomCodeCacheSizeWhileSelectingLowHeapGcAutomatically() {
        val args = mutableListOf("-XX:ReservedCodeCacheSize=96M")
        JvmGcAutoTuner.sanitizeAndInjectGcArgs(
            args, javaMajor = 17, ramAllocationMb = 1024, is64BitRuntime = true
        )

        assertTrue(args.contains("-XX:+UseSerialGC"))
        assertTrue(args.contains("-XX:ReservedCodeCacheSize=96M"))
        assertFalse(args.contains("-XX:ReservedCodeCacheSize=48M"))
    }

    @Test
    fun keepsG1GcAsTheAutomaticChoiceAboveTheLowHeapThreshold() {
        val args = mutableListOf<String>()
        JvmGcAutoTuner.sanitizeAndInjectGcArgs(
            args, javaMajor = 21, ramAllocationMb = 1281, is64BitRuntime = true
        )

        assertTrue(args.contains("-XX:+UseG1GC"))
        assertFalse(args.contains("-XX:+UseSerialGC"))
        assertFalse(args.contains("-XX:ReservedCodeCacheSize=48M"))
    }

    @Test
    fun sanitizesCmsOnJava17AndInjectsG1Gc() {
        val args = mutableListOf(
            "-XX:+UseConcMarkSweepGC",
            "-XX:+CMSIncrementalMode"
        )
        JvmGcAutoTuner.sanitizeAndInjectGcArgs(
            args, javaMajor = 17, ramAllocationMb = 3072, is64BitRuntime = true
        )

        assertFalse(args.contains("-XX:+UseConcMarkSweepGC"))
        assertFalse(args.contains("-XX:+CMSIncrementalMode"))
        assertTrue(args.contains("-XX:+UseG1GC"))
    }

    @Test
    fun buildsGenerationalZgcOnJava21AndFallsBackOnJava17() {
        val java21Flags = JvmGcAutoTuner.buildJvmFlags(
            preset = GcTuningPreset.GENERATIONAL_ZGC_TURBO,
            javaMajor = 21,
            ramAllocationMb = 4096,
            is64BitRuntime = true
        )
        assertTrue(java21Flags.contains("-XX:+UseZGC"))
        assertTrue(java21Flags.contains("-XX:+ZGenerational"))

        val java17Flags = JvmGcAutoTuner.buildJvmFlags(
            preset = GcTuningPreset.GENERATIONAL_ZGC_TURBO,
            javaMajor = 17,
            ramAllocationMb = 4096,
            is64BitRuntime = true
        )
        assertFalse(java17Flags.contains("-XX:+ZGenerational"))
        assertTrue(java17Flags.contains("-XX:+UseG1GC"))
    }

    @Test
    fun usesJdk25DefaultGenerationalZgcWithoutTheRemovedToggle() {
        val java25Flags = JvmGcAutoTuner.buildJvmFlags(
            preset = GcTuningPreset.GENERATIONAL_ZGC_TURBO,
            javaMajor = 25,
            ramAllocationMb = 4096,
            is64BitRuntime = true
        )
        assertTrue(java25Flags.contains("-XX:+UseZGC"))
        assertFalse(java25Flags.contains("-XX:+ZGenerational"))

        val configuredArgs = mutableListOf("-XX:+UseZGC", "-XX:+ZGenerational")
        JvmGcAutoTuner.sanitizeAndInjectGcArgs(
            configuredArgs,
            javaMajor = 25,
            ramAllocationMb = 4096,
            is64BitRuntime = true
        )
        assertTrue(configuredArgs.contains("-XX:+UseZGC"))
        assertFalse(configuredArgs.contains("-XX:+ZGenerational"))
    }

    @Test
    fun fallsBackToG1WhenZgcIsRequestedOnA32BitRuntime() {
        listOf(21, 25).forEach { javaMajor ->
            val flags = JvmGcAutoTuner.buildJvmFlags(
                preset = GcTuningPreset.GENERATIONAL_ZGC_TURBO,
                javaMajor = javaMajor,
                ramAllocationMb = 4096,
                is64BitRuntime = false
            )
            assertTrue(flags.contains("-XX:+UseG1GC"))
            assertFalse(flags.contains("-XX:+UseZGC"))
        }

        val configuredArgs = mutableListOf("-XX:+UseZGC", "-XX:+ZGenerational")
        JvmGcAutoTuner.sanitizeAndInjectGcArgs(
            args = configuredArgs,
            javaMajor = 25,
            ramAllocationMb = 4096,
            is64BitRuntime = false
        )
        assertFalse(configuredArgs.contains("-XX:+UseZGC"))
        assertFalse(configuredArgs.contains("-XX:+ZGenerational"))
        assertTrue(configuredArgs.contains("-XX:+UseG1GC"))
    }
}
