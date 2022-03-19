package net.dzikoysk.cdn.serdes.composers

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.entity.Contextual
import net.dzikoysk.cdn.loadAs
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk
import panda.std.reactive.reference

class ContextualComposerTest : CdnSpec() {

    @Contextual
    data class ImmutableSection(
        val key: String = "value"
    )

    @Test
    fun `should serialize data classes`() {
        assertEquals("key: value", assertOk(standard.render(ImmutableSection())))
        assertEquals("custom", assertOk(standard.loadAs<ImmutableSection>(Source.of("key: custom"))).key)
    }

    class ConfigurationWithImmutableSection {
        val section = reference(ImmutableSection())
    }

    @Test
    fun `should serialize data classes as sections`() {
        assertEquals("""
        section {
          key: value
        }""".trimIndent(), assertOk(standard.render(ConfigurationWithImmutableSection())))

        assertEquals("custom", assertOk(standard.loadAs<ConfigurationWithImmutableSection>(Source.of("""
        section { 
            key: custom
        }"""))).section.get().key)
    }

}