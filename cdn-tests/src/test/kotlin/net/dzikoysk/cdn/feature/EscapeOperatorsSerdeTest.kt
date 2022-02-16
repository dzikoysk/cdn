package net.dzikoysk.cdn.feature

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.entity.Description
import net.dzikoysk.cdn.loadAs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class EscapeOperatorsSerdeTest : CdnSpec()  {

    class ConfigurationWithSeparators {

        @Description("// Separator")
        var separator = "\n"

        @Description("", "// Separators in one entry")
        var separators = "\n\n\n"

        @Description("", "// Separator with value")
        var separatorValue = "\nvalue"

        @Description("", "// List of different separators")
        var separatorsList = listOf("value", "\nvalue", "\nvalue\n", "\nvalue\nvalue\nvalue")

    }

    @Test
    fun `should render with raw separators in entries`() {
        val renderResult = standard.render(ConfigurationWithSeparators())
        val render = assertOk(renderResult)

        assertEquals("""
            // Separator
            separator: \n

            // Separators in one entry
            separators: \n\n\n

            // Separator with value
            separatorValue: \nvalue

            // List of different separators
            separatorsList [
              value
              \nvalue
              \nvalue\n
              \nvalue\nvalue\nvalue
            ]
        """.trimIndent(), render)
    }

    @Test
    fun `should load to  separators`() {
        val renderResult = standard.render(ConfigurationWithSeparators())
        val render = assertOk(renderResult)

        val loadResult = standard.loadAs<ConfigurationWithSeparators> { render }
        val load = assertOk(loadResult)

        assertEquals("\n", load.separator)
        assertEquals("\n\n\n", load.separators)
        assertEquals("\nvalue", load.separatorValue)
        assertEquals("value", load.separatorsList[0])
        assertEquals("\nvalue", load.separatorsList[1])
        assertEquals("\nvalue\n", load.separatorsList[2])
        assertEquals("\nvalue\nvalue\nvalue", load.separatorsList[3])
    }


}