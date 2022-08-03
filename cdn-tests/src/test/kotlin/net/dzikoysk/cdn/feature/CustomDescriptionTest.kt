package net.dzikoysk.cdn.feature

import net.dzikoysk.cdn.Cdn
import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.KCdnFactory
import net.dzikoysk.cdn.entity.Description
import net.dzikoysk.cdn.loadAs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class CustomDescriptionTest : CdnSpec()  {

    class ConfigurationWithSeparators {

        @Description("// Normal description")
        var value = "value"

        @Comment("Description")
        var otherValue = "other value"

    }

    @Target(
        AnnotationTarget.FIELD,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
    )
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Comment(val value: String)

    private val custom: Cdn = KCdnFactory.createStandard()
        .settings
        .withDescriptionResolver(Comment::class.java) { comment -> listOf("// CUSTOM // ${comment.value}") }
        .build()

    @Test
    fun `should render content with custom description`() {
        val renderResult = custom.render(ConfigurationWithSeparators())
        val render = assertOk(renderResult)

        assertEquals("""
            // Normal description
            value: value
            // CUSTOM // Description
            otherValue: other value
        """.trimIndent(), render)
    }

    @Test
    fun `should correctly load configuration`() {
        assertOk(custom.loadAs<ConfigurationWithSeparators> { """
            // Normal description
            value: value
            // CUSTOM // Description
            otherValue: other value
        """.trimIndent() })
    }

}