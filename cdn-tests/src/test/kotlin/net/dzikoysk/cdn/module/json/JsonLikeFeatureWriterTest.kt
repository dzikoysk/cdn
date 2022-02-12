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

package net.dzikoysk.cdn.module.json

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.entity.Contextual
import net.dzikoysk.cdn.entity.Description
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class JsonLikeFeatureWriterTest : CdnSpec() {

    class ConfigurationWithDescription {
        @Description("# Description")
        @JvmField val key = "value"
    }

    @Test
    fun `should ignore description as it is not supported by json`() {
        val configuration = assertOk(json.render(ConfigurationWithDescription()))
        assertEquals("\"key\": \"value\"", configuration)
    }

    class ConfigurationWithLists {
        @JvmField val key1 = listOf("a", "b")
        @JvmField val key2 = listOf("a", "b")
    }

    @Test
    fun `should render lists`() {
        assertEquals("""
        "key1": [
          "a",
          "b"
        ],
        "key2": [
          "a",
          "b"
        ]
        """.trimIndent(), assertOk(json.render(ConfigurationWithLists())))
    }

    class ConfigurationWithSections {
        @JvmField val section1 = Section()
        @JvmField val section2 = Section()

        @Contextual class Section {
            @JvmField val key1 = "value1"
            @JvmField val key2 = "value2"
        }
    }

    @Test
    fun `should render sections`() {
        assertEquals("""
        "section1": {
          "key1": "value1",
          "key2": "value2"
        },
        "section2": {
          "key1": "value1",
          "key2": "value2"
        }
        """.trimIndent(), assertOk(json.render(ConfigurationWithSections())))
    }

}