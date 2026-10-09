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

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.nio.file.Files

class SdlLibraryPathsTest {
    @Test
    fun builtInMobileGluesLibraryResolvesToApkNativeLibraryPath() {
        val root = Files.createTempDirectory("sdl-gl-library").toFile()
        try {
            val nativeDirectory = File(root, "apk/lib").apply { mkdirs() }

            assertEquals(
                File(nativeDirectory, "libmobileglues.so").absolutePath,
                resolveSdlOpenGlLibraryPath("libmobileglues.so", nativeDirectory.absolutePath),
            )
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun absolutePluginLibraryPathIsPreserved() {
        val root = Files.createTempDirectory("sdl-gl-library").toFile()
        try {
            val pluginLibrary = File(root, "plugin/libcustom-renderer.so").absolutePath

            assertEquals(
                pluginLibrary,
                resolveSdlOpenGlLibraryPath(pluginLibrary, File(root, "apk/lib").absolutePath),
            )
        } finally {
            root.deleteRecursively()
        }
    }
}
