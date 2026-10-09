/*
 * Zalith Launcher 2
 * Copyright (C) 2026 Aerix Launcher contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.movtery.zalithlauncher.game.renderer

import com.movtery.zalithlauncher.game.renderer.renderers.GL4ESRenderer
import com.movtery.zalithlauncher.game.renderer.renderers.NGGL4ESRenderer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Gl4esRendererTest {
    @Test
    fun legacyGl4esAdvertisesOnlyTheLegacyMinecraftRangeAndSupportedVboFlag() {
        assertEquals("1.16.5", GL4ESRenderer.getMaxMCVersion())
        assertEquals(2, GL4ESRenderer.getMinimumGlesVersion())
        assertEquals("1", GL4ESRenderer.getRendererEnv().value["LIBGL_USEVBO"])
        assertFalse(GL4ESRenderer.getRendererEnv().value.containsKey("LIBGL_USE_VBO"))
    }

    @Test
    fun ngGl4esIsAvailableAsTheModernGles3Fallback() {
        assertEquals("1.17", NGGL4ESRenderer.getMinMCVersion())
        assertEquals(3, NGGL4ESRenderer.getMinimumGlesVersion())
        assertEquals("1", NGGL4ESRenderer.getRendererEnv().value["LIBGL_USEVBO"])
        assertTrue(NGGL4ESRenderer.getMaxMCVersion()!!.startsWith("26."))
    }
}
