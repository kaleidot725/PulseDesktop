package jp.kaleidot725.pulse.architecture

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * How [PulseModuleLayout] turns a module's directory into a module name and a layer.
 */
class PulseModuleLayoutTest {
    /**
     * A pattern matches the end of the directory, a wildcard stands for one directory, and the name is the part that
     * matched — so the same layout works wherever the project is checked out.
     */
    @Test
    fun `names a module after the part of its directory a pattern matched`() {
        val layout = PulseModuleLayout.NowInAndroid

        assertEquals("core/data", layout.moduleOf("/home/me/app-repo/core/data"))
        assertEquals(ModuleLayer.Data, layout.layerOf("/home/me/app-repo/core/data"))
        assertEquals("core/common", layout.moduleOf("/home/me/app-repo/core/common"))
        assertEquals(ModuleLayer.Support, layout.layerOf("/home/me/app-repo/core/common"))
        assertNull(layout.moduleOf("/home/me/app-repo/benchmarks"))
    }

    /**
     * Earlier layers win, so `core/data` is data even though the support wildcard under `core` matches it as well.
     */
    @Test
    fun `gives a module to the first layer that claims it`() {
        assertEquals(ModuleLayer.Data, PulseModuleLayout.NowInAndroid.layerOf("/repo/core/data"))
    }

    /**
     * A feature split into `api` and `impl` is one feature by name, and keeps its layer even when the layout only lists
     * the `impl` directory.
     */
    @Test
    fun `reads an api or impl directory as part of the feature above it`() {
        val layout = PulseModuleLayout(features = listOf("features/*/impl", "features/*/api"))

        assertEquals("features/shop", layout.moduleOf("/repo/features/shop/impl"))
        assertEquals("features/shop", layout.moduleOf("/repo/features/shop/api"))
        assertEquals(ModuleLayer.Feature, layout.layerOf("/repo/features/shop/impl"))
    }
}
