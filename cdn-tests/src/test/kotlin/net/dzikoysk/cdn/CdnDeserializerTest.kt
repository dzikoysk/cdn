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

package net.dzikoysk.cdn

import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class CdnDeserializerTest : CdnSpec() {

    @Test
    fun `should deserialize source into scheme`() {
        val configuration = assertOk(standard.load(Source.of("""
        rootEntry: custom value
        strangeValue: "
        section {
          subEntry: 7
          list [
            key: value
            value
          ],
          custom {
            count: 3080
          }
        }
        """), TestConfiguration::class.java))

        assertEquals("custom value", configuration.rootEntry)
        assertEquals(7, configuration.section.subEntry)
        assertEquals(listOf("key: value", "value"), configuration.section.list)
        assertEquals("rtx", configuration.section.custom.id)
        assertEquals(3080, configuration.section.custom.count)
    }

}
