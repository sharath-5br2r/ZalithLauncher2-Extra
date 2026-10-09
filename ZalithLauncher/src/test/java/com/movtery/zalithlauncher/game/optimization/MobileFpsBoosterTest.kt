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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MobileFpsBoosterTest {
    @Test
    fun renderDistanceAndScaleAreReducedAcrossTheMobilePresets() {
        assertEquals("2", FpsBoostPreset.ULTRA_120FPS.optionsMap["renderDistance"])
        assertEquals("4", FpsBoostPreset.BALANCED_MOBILE.optionsMap["renderDistance"])
        assertEquals("2", FpsBoostPreset.BATTERY_SAVER.optionsMap["renderDistance"])

        assertTrue(FpsBoostPreset.ULTRA_120FPS.renderScale < FpsBoostPreset.BALANCED_MOBILE.renderScale)
        assertTrue(FpsBoostPreset.BATTERY_SAVER.renderScale < FpsBoostPreset.BALANCED_MOBILE.renderScale)
        assertTrue(FpsBoostPreset.BATTERY_SAVER.optionsMap["maxFps"]!!.toInt() <= 60)
    }

    @Test
    fun presetsDoNotDisplayUnmeasuredFpsOrTemperatureClaims() {
        FpsBoostPreset.entries.forEach { preset ->
            assertFalse(preset.badge.contains("%"))
            assertFalse(preset.badge.contains("°"))
            assertFalse(preset.subtitle.contains("prevent", ignoreCase = true))
        }
    }
}
