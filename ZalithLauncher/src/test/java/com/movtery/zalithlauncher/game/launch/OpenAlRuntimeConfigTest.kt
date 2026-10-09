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

import java.io.File
import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenAlRuntimeConfigTest {
    @Test
    fun writesAndPointsToTheOpenSlEsFallbackConfiguration() {
        val tempDir = Files.createTempDirectory("openal-config-test-").toFile()
        try {
            val source = """
                [general]
                drivers = opensl
            """.trimIndent() + "\n"
            val target = File(tempDir, "openal/alsoft.conf")

            val configFile = OpenAlRuntimeConfig.installConfig(target, source)
            val environment = OpenAlRuntimeConfig.environment(configFile, source)

            assertEquals(source, configFile.readText())
            assertEquals(configFile.absolutePath, environment["ALSOFT_CONF"])
            assertEquals("opensl", environment["ALSOFT_DRIVERS"])
            assertTrue(source.contains("drivers = ${OpenAlRuntimeConfig.OPENSL_DRIVER}"))
        } finally {
            tempDir.deleteRecursively()
        }
    }

    @Test
    fun lwJglOpenAlPropertyPointsIntoTheProcessSpecificNativeLibraryDirectory() {
        val nativeDirectory = File("/data/app/process-abi/lib")
        val expected = File(nativeDirectory, "libopenal.so").absolutePath

        assertEquals(
            "-Dorg.lwjgl.openal.libname=$expected",
            OpenAlRuntimeConfig.lwjglOpenAlProperty(nativeDirectory.absolutePath),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsAConfigThatDoesNotProvideOpenSlEs() {
        OpenAlRuntimeConfig.installConfig(
            target = File("unused-openal-test", "alsoft.conf"),
            configText = "[general]\ndrivers = null\n",
        )
    }
}
