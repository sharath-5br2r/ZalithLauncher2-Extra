package com.movtery.zalithlauncher.game.launch

import com.movtery.zalithlauncher.game.version.installed.GraphicsApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MinecraftGraphicsBackendTest {
    @Test
    fun defaultPreservesAnExistingMinecraftChoice() {
        assertNull(
            resolvePreferredGraphicsBackendOption(
                graphicsApi = GraphicsApi.DEFAULT,
                hasVulkanBackend = true,
                existingOption = GraphicsApi.VULKAN.option,
            )
        )
    }

    @Test
    fun defaultUsesTheGameDefaultWhenNoChoiceWasSaved() {
        assertEquals(
            GraphicsApi.DEFAULT.option,
            resolvePreferredGraphicsBackendOption(
                graphicsApi = GraphicsApi.DEFAULT,
                hasVulkanBackend = false,
                existingOption = null,
            )
        )
    }

    @Test
    fun defaultOpenGlPreservesExistingChoicesForOlderClients() {
        assertNull(
            resolvePreferredGraphicsBackendOption(
                graphicsApi = GraphicsApi.DEFAULT_OPENGL,
                hasVulkanBackend = false,
                existingOption = GraphicsApi.VULKAN.option,
            )
        )
    }

    @Test
    fun defaultOpenGlReplacesStaleVulkanChoiceForClientsWithVulkanBackend() {
        assertEquals(
            GraphicsApi.OPENGL.option,
            resolvePreferredGraphicsBackendOption(
                graphicsApi = GraphicsApi.DEFAULT_OPENGL,
                hasVulkanBackend = true,
                existingOption = GraphicsApi.VULKAN.option,
            )
        )
    }

    @Test
    fun explicitGraphicsApiAlwaysOverridesTheSavedChoice() {
        assertEquals(
            GraphicsApi.OPENGL.option,
            resolvePreferredGraphicsBackendOption(
                graphicsApi = GraphicsApi.OPENGL,
                hasVulkanBackend = false,
                existingOption = GraphicsApi.VULKAN.option,
            )
        )
        assertEquals(
            GraphicsApi.VULKAN.option,
            resolvePreferredGraphicsBackendOption(
                graphicsApi = GraphicsApi.VULKAN,
                hasVulkanBackend = false,
                existingOption = GraphicsApi.OPENGL.option,
            )
        )
    }
}
