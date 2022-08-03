package net.dzikoysk.cdn.feature

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.entity.Description
import net.dzikoysk.cdn.loadAs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class OptionalValueTest : CdnSpec()  {

    class ConfigurationWithOptionalValues {

        @Description("// Normal value")
        var normal = "value"

        @Description("// Optional value")
        var optional: String? = null

    }

    @Test
    fun `should render without optional value`() {
        val renderResult = standard.render(ConfigurationWithOptionalValues())
        val render = assertOk(renderResult)

        assertEquals("""
            // Normal value
            normal: value
        """.trimIndent(), render)
    }

    @Test
    fun `should render with all values after modification`() {
        val modified = ConfigurationWithOptionalValues()

        modified.optional = "newValue"

        val renderResult = standard.render(modified)
        val render = assertOk(renderResult)

        assertEquals("""
            // Normal value
            normal: value
            // Optional value
            optional: newValue
        """.trimIndent(), render)
    }

    @Test
    fun `should load without optional value`() {
        val loadResult = standard.loadAs<ConfigurationWithOptionalValues> { """
            // Normal value
            normal: value
        """.trimIndent() }

        val load = assertOk(loadResult)

        assertEquals("value", load.normal)
        assertNull(load.optional)
    }

    @Test
    fun `should load all values`() {
        val loadResult = standard.loadAs<ConfigurationWithOptionalValues> { """
            // Normal value
            normal: value
            // Optional value
            optional: newValue
        """.trimIndent() }

        val load = assertOk(loadResult)

        assertEquals("value", load.normal)
        assertEquals("newValue", load.optional)
    }

}