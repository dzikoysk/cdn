package net.dzikoysk.cdn.feature

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.entity.Contextual
import net.dzikoysk.cdn.entity.Description
import net.dzikoysk.cdn.loadAs
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class OptionalSectionTest : CdnSpec()  {

    class ConfigurationWithOptionalValues {

        @Description("// Normal value")
        var normal = "value"

        @Description("// Optional section")
        var optionalSection: Section? = null

        @Contextual
        class Section {
            @Description("// Other value in section")
            var other = "value"
            @Description("// Optional value")
            var optional: String? = null;
        }

    }

    @Test
    fun `should render without optional section`() {
        val renderResult = standard.render(ConfigurationWithOptionalValues())
        val render = assertOk(renderResult)

        assertEquals("""
            // Normal value
            normal: value
        """.trimIndent(), render)
    }

    @Test
    fun `should render section after without optional value`() {
        val modified = ConfigurationWithOptionalValues()

        modified.optionalSection = ConfigurationWithOptionalValues.Section()

        val renderResult = standard.render(modified)
        val render = assertOk(renderResult)

        assertEquals("""
            // Normal value
            normal: value
            // Optional section
            optionalSection {
              // Other value in section
              other: value
            }
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
        assertNull(load.optionalSection)
    }

    @Test
    fun `should load all values`() {
        val loadResult = standard.loadAs<ConfigurationWithOptionalValues> { """
            // Normal value
            normal: value
            // Optional section
            optionalSection {
              // Other value in section
              other: otherValue
              // Optional value
              optional: optionalValue
            }
        """.trimIndent() }

        val load = assertOk(loadResult)

        assertEquals("value", load.normal)
        assertNotNull(load.optionalSection)
        assertEquals("otherValue", load.optionalSection?.other)
        assertEquals("optionalValue", load.optionalSection?.optional)
    }

}