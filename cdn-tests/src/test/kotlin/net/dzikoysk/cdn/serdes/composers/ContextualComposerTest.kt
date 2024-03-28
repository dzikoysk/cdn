package net.dzikoysk.cdn.serdes.composers

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.KCdnFactory
import net.dzikoysk.cdn.entity.Contextual
import net.dzikoysk.cdn.loadAs
import net.dzikoysk.cdn.reflect.Visibility.PRIVATE
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk
import panda.std.reactive.Reference
import panda.std.reactive.reference

class ContextualComposerTest : CdnSpec() {

    @Contextual
    data class ImmutableSection(
        val key: String = "value"
    )

    @Test
    fun `should serialize data classes`() {
        assertEquals("key: value", assertOk(standard.render(ImmutableSection("value"))))
        assertEquals("custom", assertOk(standard.loadAs<ImmutableSection>(Source.of("key: custom"))).key)
    }

    class ConfigurationWithImmutableSection {
        val section = reference(ImmutableSection("value"))
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

    class ConfigurationWithImmutableSectionInMap(
        val section: Reference<MapSection> = reference(MapSection())
    ) {
        @Contextual
        data class MapSection(
            val map: Map<String, ImmutableSection> = mapOf("entry" to ImmutableSection("value"))
        )
    }

    @Test
    fun `should serialize data classes as sections in maps`() {
        assertEquals("custom", assertOk(standard.loadAs<ConfigurationWithImmutableSectionInMap>(Source.of("""
        section {
            map {
                entry {
                    key: custom
                }
            }
        }"""))).section.get().map["entry"]!!.key)
    }

    private class ConfigWithInterface {
        val section = Mlynarz()
    }

    private interface Mlyn {
        fun getMonke(): String
    }

    @Contextual
    private class Mlynarz : Mlyn {
        override fun getMonke() = "mlynarz"
    }

    @Test
    fun `should serialize monke`() {
        assertEquals(
            "mlynarz",
            assertOk(
                KCdnFactory
                    .createStandard()
                    .also { it.settings.withMemberResolver(PRIVATE) }
                    .loadAs<ConfigWithInterface>(
                        Source.of("""
                            section {
                                monke: mlynarz
                            }""")
                    )
            )
            .section
            .getMonke()
        )
    }

}