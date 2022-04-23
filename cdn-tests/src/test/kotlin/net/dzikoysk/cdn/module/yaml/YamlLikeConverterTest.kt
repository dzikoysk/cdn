package net.dzikoysk.cdn.module.yaml

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.module.yaml.YamlLikeConverterTest.ConfigurationWithEmptyDescription.Statistics
import net.dzikoysk.cdn.entity.Contextual
import net.dzikoysk.cdn.entity.Description
import net.dzikoysk.cdn.loadAs
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

internal class YamlLikeConverterTest : CdnSpec() {

    class ConfigurationWithEmptyDescription {
        @Description("")
        var statistics = Statistics()

        @Contextual
        class Statistics {
            @Description("")
            var flag = false
            @Description("")
            var slot = 25
            @Description("")
            var name = "value"
        }
    }

    // Make sure converter properly handles empty lines
    // ~ https://github.com/dzikoysk/cdn/issues/130
    @Test
    fun `should handle empty description`() {
        val source = assertOk(yamlLike.render(ConfigurationWithEmptyDescription().also { configuration ->
            configuration.statistics = Statistics().also {
                it.flag = true
                it.slot = 7
                it.name = "custom"
            }
        }))

        assertEquals("""
        statistics:
          
          flag: true
          
          slot: 7
          
          name: "custom"
        """.trimIndent(), source)

        val configuration = assertOk(yamlLike.loadAs<ConfigurationWithEmptyDescription>(Source.of(source)))
        assertEquals(7, configuration.statistics.slot)
        assertEquals("custom", configuration.statistics.name)
    }

}