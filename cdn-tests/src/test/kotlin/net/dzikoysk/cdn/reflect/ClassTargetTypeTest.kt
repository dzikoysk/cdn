package net.dzikoysk.cdn.reflect

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ClassTargetTypeTest {

    data class DataClass(
        val immutableProperty: String,
        val immutablePropertyWithDefault: String = "value",
        var mutableProperty: String,
        var mutablePropertyWithDefault: String = "value",
        val nullableImmutableProperty: String?,
        val nullableImmutablePropertyWithDefault: String? = null,
        var nullableMutableProperty: String?,
        var nullableMutablePropertyWithDefault: String? = null,
    )

    @Test
    fun `should find all properties in data classes`() {
        val targetType = ClassTargetType(DataClass::class.java, KotlinMemberResolver())
        targetType.annotatedMembers.forEach { println(it.name) }
        assertEquals(8, targetType.annotatedMembers.size)
    }

}