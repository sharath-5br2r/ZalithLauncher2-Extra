package com.movtery.zalithlauncher.game.renderer

import org.junit.Assert.assertEquals
import org.junit.Test

class RendererPickerTest {
    private val all = setOf(
        RendererPicker.GL4ES,
        RendererPicker.VIRGL,
        RendererPicker.LTW,
        RendererPicker.ZINK,
    )

    /** The same set as [all], but with the LTW Legacy wrapper loaded as well. */
    private val allWithLtwLegacy = all + RendererPicker.LTW_LEGACY

    @Test
    fun legacyVersionsPreferGl4es() {
        val choice = RendererPicker.pick("1.12.2", "", all)
        assertEquals(RendererPicker.GL4ES, choice.identifier)
        assertEquals(true, choice.automatic)
    }

    @Test
    fun legacyFallsBackToVirgl() {
        val choice = RendererPicker.pick("1.16.4", "", setOf(RendererPicker.VIRGL))
        assertEquals(RendererPicker.VIRGL, choice.identifier)
    }

    @Test
    fun modernVersionsPreferLtw() {
        val choice = RendererPicker.pick("1.21.1", "", all)
        assertEquals(RendererPicker.LTW, choice.identifier)
    }

    @Test
    fun minecraft26PrefersRendererWithFullEglSupportForSdl() {
        val choice = RendererPicker.pick(
            mcVersion = "26.2",
            manualIdentifier = "",
            available = setOf(RendererPicker.LTW, RendererPicker.MOBILEGLUES, RendererPicker.NG_GL4ES),
        )

        assertEquals(RendererPicker.MOBILEGLUES, choice.identifier)
    }

    @Test
    fun minecraft26FallsBackToNgGl4esInsteadOfThePartialLtwEglShim() {
        val choice = RendererPicker.pick(
            mcVersion = "26.2",
            manualIdentifier = "",
            available = setOf(RendererPicker.LTW, RendererPicker.NG_GL4ES),
        )

        assertEquals(RendererPicker.NG_GL4ES, choice.identifier)
    }

    @Test
    fun persistedLtwOverrideIsRejectedForMinecraft26Snapshots() {
        val choice = RendererPicker.pick(
            mcVersion = "26.2-snapshot-1",
            manualIdentifier = RendererPicker.LTW,
            available = setOf(RendererPicker.LTW, RendererPicker.MOBILEGLUES, RendererPicker.NG_GL4ES),
        )

        assertEquals(RendererPicker.MOBILEGLUES, choice.identifier)
        assertEquals(true, choice.automatic)
        assertEquals(true, choice.reason.contains("instance override unsupported"))
    }

    @Test
    fun modernVersionsCanFallBackToNgGl4esButNeverLegacyGl4es() {
        val choice = RendererPicker.pick(
            mcVersion = "1.20.1",
            manualIdentifier = RendererPicker.GL4ES,
            available = setOf(RendererPicker.GL4ES, RendererPicker.NG_GL4ES),
        )

        assertEquals(RendererPicker.NG_GL4ES, choice.identifier)
        assertEquals(true, choice.automatic)
    }

    @Test
    fun refusesToForceLegacyGl4esForModernMinecraftWhenNoCoreRendererExists() {
        val choice = RendererPicker.pick(
            mcVersion = "1.20.1",
            manualIdentifier = RendererPicker.GL4ES,
            available = setOf(RendererPicker.GL4ES),
        )

        assertEquals("", choice.identifier)
        assertEquals(true, choice.reason.contains("no renderer compatible"))
    }

    @Test
    fun modernPickDoesNotChooseGles3OrUnverifiedVulkanRenderersOnGles2Devices() {
        val available = setOf(
            RendererPicker.GL4ES,
            RendererPicker.LTW,
            RendererPicker.MOBILEGLUES,
            RendererPicker.NG_GL4ES,
            RendererPicker.ZINK,
        )

        val choice = RendererPicker.pick(
            mcVersion = "1.21.1",
            manualIdentifier = "",
            available = available,
            deviceGlesVersion = 2,
        )

        assertEquals("", choice.identifier)
    }

    @Test
    fun manualLtwOverrideIsRejectedWhenDeviceLacksGles3UnlessVulkanIsAvailable() {
        val available = setOf(
            RendererPicker.GL4ES,
            RendererPicker.LTW,
            RendererPicker.MOBILEGLUES,
            RendererPicker.NG_GL4ES,
            RendererPicker.ZINK,
        )

        val unavailable = RendererPicker.pick(
            mcVersion = "1.21.1",
            manualIdentifier = RendererPicker.LTW,
            available = available,
            deviceGlesVersion = 2,
        )
        assertEquals("", unavailable.identifier)
        assertEquals(true, unavailable.reason.contains("requires GLES 3"))

        val withVulkan = RendererPicker.pick(
            mcVersion = "1.21.1",
            manualIdentifier = RendererPicker.LTW,
            available = available,
            deviceGlesVersion = 2,
            vulkanAvailable = true,
        )
        assertEquals(RendererPicker.ZINK, withVulkan.identifier)
        assertEquals(true, withVulkan.automatic)
    }

    @Test
    fun unknownGlesDetectionDoesNotHideLtw() {
        val choice = RendererPicker.pick(
            mcVersion = "1.21.1",
            manualIdentifier = "",
            available = all,
            deviceGlesVersion = -3,
        )

        assertEquals(RendererPicker.LTW, choice.identifier)
    }

    @Test
    fun modernUsesZinkAsAFallbackOnlyAfterVulkanHasBeenDetected() {
        val choice = RendererPicker.pick(
            mcVersion = "1.17.1",
            manualIdentifier = "",
            available = setOf(RendererPicker.ZINK),
            vulkanAvailable = true,
        )
        assertEquals(RendererPicker.ZINK, choice.identifier)
    }

    @Test
    fun fallsBackFromZinkWhenVulkanIsKnownToBeUnavailable() {
        val choice = RendererPicker.pick(
            mcVersion = "1.21.1",
            manualIdentifier = RendererPicker.ZINK,
            available = setOf(RendererPicker.ZINK, RendererPicker.LTW, RendererPicker.NG_GL4ES),
            vulkanAvailable = false,
        )

        assertEquals(RendererPicker.LTW, choice.identifier)
        assertEquals(true, choice.automatic)
        assertEquals(true, choice.reason.contains("requires Vulkan"))
    }

    @Test
    fun instanceOverrideWinsWhenGl4esIsUsedOnALegacyVersion() {
        val choice = RendererPicker.pick("1.16.5", RendererPicker.GL4ES, all)
        assertEquals(RendererPicker.GL4ES, choice.identifier)
        assertEquals(false, choice.automatic)
    }

    @Test
    fun missingOverrideFallsBack() {
        val choice = RendererPicker.pick("1.8.9", "missing", all)
        assertEquals(RendererPicker.GL4ES, choice.identifier)
    }

    @Test
    fun legacyVersionsPreferLtwLegacy() {
        val choice = RendererPicker.pick("1.12.2", "", allWithLtwLegacy)
        assertEquals(RendererPicker.LTW_LEGACY, choice.identifier)
        assertEquals(true, choice.automatic)
    }

    /** 1.16.5 is the last release on the legacy OpenGL pipeline, so it must not move to LTW. */
    @Test
    fun lastLegacyReleaseStillPicksTheLegacyWrapper() {
        val choice = RendererPicker.pick("1.16.5", "", allWithLtwLegacy)
        assertEquals(RendererPicker.LTW_LEGACY, choice.identifier)
    }

    /** 1.17 is where Minecraft requires the OpenGL 3.2 core profile. */
    @Test
    fun coreProfileVersionsPickLtw() {
        assertEquals(
            RendererPicker.LTW,
            RendererPicker.pick("1.17", "", allWithLtwLegacy).identifier,
        )
    }

    @Test
    fun legacyStillFallsBackToGl4esWhenTheLegacyWrapperIsAbsent() {
        val choice = RendererPicker.pick("1.12.2", "", all)
        assertEquals(RendererPicker.GL4ES, choice.identifier)
    }

    /** An override for a version the wrapper supports must survive untouched. */
    @Test
    fun supportedOverrideWinsOnLegacyVersions() {
        val choice = RendererPicker.pick("1.8.9", RendererPicker.VIRGL, allWithLtwLegacy)
        assertEquals(RendererPicker.VIRGL, choice.identifier)
        assertEquals(false, choice.automatic)
    }

    /** GL4ES is legacy-only: Minecraft 1.17+ must use a core-profile-compatible renderer. */
    @Test
    fun legacyGl4esOverrideIsRejectedForCoreProfileVersions() {
        val choice = RendererPicker.pick("1.17", RendererPicker.GL4ES, all)

        assertEquals(RendererPicker.LTW, choice.identifier)
        assertEquals(true, choice.automatic)
    }

    @Test
    fun resolveLeavesUnknownVersionsToTheCaller() {
        assertEquals(null, RendererPicker.resolve("", RendererPicker.GL4ES))
    }

    @Test
    fun vgpuOverrideWinsOn1165() {
        val withVgpu = allWithLtwLegacy + RendererPicker.VGPU + RendererPicker.VGPU_1368

        val fastChoice = RendererPicker.pick("1.16.5", RendererPicker.VGPU, withVgpu)
        assertEquals(RendererPicker.VGPU, fastChoice.identifier)
        assertEquals(false, fastChoice.automatic)

        val betaChoice = RendererPicker.pick("1.16.5", RendererPicker.VGPU_1368, withVgpu)
        assertEquals(RendererPicker.VGPU_1368, betaChoice.identifier)
        assertEquals(false, betaChoice.automatic)
    }
}
