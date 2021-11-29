/*
 * Copyright (c) 2021 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dzikoysk.cdn.kt

import net.dzikoysk.cdn.KCdnFactory
import net.dzikoysk.cdn.entity.Description
import net.dzikoysk.cdn.loadAs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk
import panda.std.reactive.reference

internal class KotlinReaderTest {

    class KotlinConfiguration {

        // Fields

        @Description("# Field")
        @Description("# Extra")
        @JvmField
        var field = "value"

        @Description("# Field reference")
        @JvmField
        val fieldReference = reference("value")

        @Description("# Field with generic signature")
        var genericSignatureReference = reference(emptyMap<String, String>())

        // Standard properties

        @Description("# Description")
        var key = "value"

        @Description("# Description")
        val reference = reference("ref")

    }

    @Test
    fun `should load configuration in kotlin`() {
        val configuration = assertOk(KCdnFactory.createStandard().loadAs<KotlinConfiguration> { "key: custom" })

        assertEquals("custom", configuration.key)
        assertEquals("ref", configuration.reference.get())
        assertEquals("""
        # Field
        # Extra
        field: value
        # Field reference
        fieldReference: value
        # Field with generic signature
        genericSignatureReference: []
        # Description
        key: custom
        # Description
        reference: ref
        """.trimIndent(), assertOk(KCdnFactory.createStandard().render(configuration)))
    }

}