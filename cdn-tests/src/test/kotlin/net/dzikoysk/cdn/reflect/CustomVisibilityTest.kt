package net.dzikoysk.cdn.reflect

import net.dzikoysk.cdn.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class CustomVisibilityTest {

    private val publicModifiersCdn: Cdn = CdnFactory.createYamlLike().settings
        .withMemberResolver(Visibility.PUBLIC)
        .build()

    private val allModifiersCdn: Cdn = CdnFactory.createYamlLike().settings
        .withMemberResolver(Visibility.PRIVATE)
        .build()

    @Test
    fun `should not override java field `() {
        val result = publicModifiersCdn.load( { "test: hey" } , Config::class.java)

        assertEquals("siema", assertOk(result).test)
    }

    @Test
    fun `should override java field `() {
        val result = allModifiersCdn.load( { "test: hey" } , Config::class.java)

        assertEquals("hey", assertOk(result).test)
    }

    class Config {
        val test: String = "siema" // private final String test = "siema";
    }

}