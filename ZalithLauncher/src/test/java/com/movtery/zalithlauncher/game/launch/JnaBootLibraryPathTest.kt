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

import com.movtery.zalithlauncher.utils.device.Architecture
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.nio.file.Files

class JnaBootLibraryPathTest {
    @Test
    fun arm32UsesApkNativeLibraryEvenWhenArm64VersionedLibraryExists() {
        val root = Files.createTempDirectory("jna-boot-path").toFile()
        try {
            val gameJnaDirectory = File(root, "game/jna/5.14.0").apply { mkdirs() }
            File(gameJnaDirectory, "libjnidispatch.so").writeBytes(byteArrayOf(1))
            val appNativeDirectory = File(root, "apk/lib").apply { mkdirs() }

            assertEquals(
                appNativeDirectory.absolutePath,
                JnaBootLibraryPath.resolve(
                    gameJnaVersionDirectory = gameJnaDirectory,
                    appNativeLibraryDirectory = appNativeDirectory.absolutePath,
                    processArchitecture = Architecture.ARCH_ARM,
                )
            )
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun arm64PrefersVersionMatchedAndroidDispatchLibraryWhenPresent() {
        val root = Files.createTempDirectory("jna-boot-path").toFile()
        try {
            val gameJnaDirectory = File(root, "game/jna/5.14.0").apply { mkdirs() }
            File(gameJnaDirectory, "libjnidispatch.so").writeBytes(byteArrayOf(1))
            val appNativeDirectory = File(root, "apk/lib").apply { mkdirs() }

            assertEquals(
                gameJnaDirectory.absolutePath,
                JnaBootLibraryPath.resolve(
                    gameJnaVersionDirectory = gameJnaDirectory,
                    appNativeLibraryDirectory = appNativeDirectory.absolutePath,
                    processArchitecture = Architecture.ARCH_ARM64,
                )
            )
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun fallsBackToApkNativeLibraryWhenThereIsNoVersionedDispatchLibrary() {
        val root = Files.createTempDirectory("jna-boot-path").toFile()
        try {
            val gameJnaDirectory = File(root, "game/jna/5.14.0").apply { mkdirs() }
            val appNativeDirectory = File(root, "apk/lib").apply { mkdirs() }

            assertEquals(
                appNativeDirectory.absolutePath,
                JnaBootLibraryPath.resolve(
                    gameJnaVersionDirectory = gameJnaDirectory,
                    appNativeLibraryDirectory = appNativeDirectory.absolutePath,
                    processArchitecture = Architecture.ARCH_ARM64,
                )
            )
        } finally {
            root.deleteRecursively()
        }
    }

    @Test
    fun usesApkNativeLibraryWhenThereIsNoGameJnaDirectory() {
        val root = Files.createTempDirectory("jna-boot-path").toFile()
        try {
            val appNativeDirectory = File(root, "apk/lib").apply { mkdirs() }

            assertEquals(
                appNativeDirectory.absolutePath,
                JnaBootLibraryPath.resolve(
                    gameJnaVersionDirectory = null,
                    appNativeLibraryDirectory = appNativeDirectory.absolutePath,
                    processArchitecture = Architecture.ARCH_ARM,
                )
            )
        } finally {
            root.deleteRecursively()
        }
    }
}
