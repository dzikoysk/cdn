package net.dzikoysk.cdn.feature

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.entity.Description
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions

internal class MultiDescriptionTest : CdnSpec() {

    internal inner class ConfigurationWithMultiDescription {

        @Description("#")
        @Description("# Test multi line banner")
        @Description("#")
        @Description(" ")
        var value = "test"

    }

    @Test
    fun `should render multiline banner with empty descriptions`() {
        val renderResult = standard.render(ConfigurationWithMultiDescription())
        val render = ResultAssertions.assertOk(renderResult)

        assertEquals("""
            #
            # Test multi line banner
            #
             
            value: test
            """.trimIndent(), render)
    }

}
