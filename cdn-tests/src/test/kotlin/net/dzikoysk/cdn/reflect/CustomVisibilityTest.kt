package net.dzikoysk.cdn.reflect

import net.dzikoysk.cdn.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class CustomVisibilityTest {

    @Test
    fun `should not override java field `() {
        val publicModifiersCdn: Cdn = CdnFactory.createYamlLike().settings
            .withMemberResolver(Visibility.PUBLIC)
            .build()
        val result = publicModifiersCdn.loadAs<Config> { "test:hey" }

        assertEquals("yo", assertOk(result).test)
    }

    @Test
    fun `should override java field `() {
        val allModifiersCdn: Cdn = CdnFactory.createYamlLike().settings
            .withMemberResolver(Visibility.PRIVATE)
            .build()
        val result = allModifiersCdn.loadAs<Config> { "test:hey" }

        assertEquals("hey", assertOk(result).test)
    }

    class Config {
        val test: String = "yo" // private final String test = "yo";
    }

}