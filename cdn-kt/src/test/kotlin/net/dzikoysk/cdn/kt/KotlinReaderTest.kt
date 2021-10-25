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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KotlinReaderTest {

    @Test
    fun `should load configuration in kotlin`() {
        val configuration = KCdnFactory.createStandard().load<KotlinConfiguration> { "key: custom" }

        assertEquals("custom", configuration.key)
        assertEquals("ref", configuration.reference.get())
        assertEquals("""
        # Description
        key: custom
        # Description
        reference: ref
        """.trimIndent(), KCdnFactory.createStandard().render(configuration))
    }

}