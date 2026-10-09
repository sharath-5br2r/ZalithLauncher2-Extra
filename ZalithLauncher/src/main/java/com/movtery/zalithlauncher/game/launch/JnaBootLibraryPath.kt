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
import java.io.File

/**
 * Chooses an Android-compatible JNA dispatch library for the launched game.
 *
 * Minecraft's Maven JNA jar contains desktop Linux natives. The bundled
 * version-specific Android archive currently contains ARM64 binaries only, so
 * it must not be used by an ARM32 process even when its directory exists. In
 * every other case, use the ABI-specific library packaged with the launcher
 * instead of letting JNA extract a glibc Linux binary from the game jar.
 */
internal object JnaBootLibraryPath {
    private const val DISPATCH_LIBRARY = "libjnidispatch.so"

    fun resolve(
        gameJnaVersionDirectory: File?,
        appNativeLibraryDirectory: String,
        processArchitecture: Int,
    ): String {
        val versionedDirectory = gameJnaVersionDirectory
        return if (
            processArchitecture == Architecture.ARCH_ARM64 &&
            versionedDirectory != null &&
            File(versionedDirectory, DISPATCH_LIBRARY).isFile
        ) {
            versionedDirectory.absolutePath
        } else {
            appNativeLibraryDirectory
        }
    }
}
