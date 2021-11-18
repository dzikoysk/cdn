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
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JsonLikeFeatureTest : CdnSpec() {

    private val converter = JsonLikeModule()

    @Test
    fun `should convert json like format to cdn`() {
        val source = converter.convertToCdn(cfg("""{"list":[{"key":"value"},{"key":"value"}]}"""))

        assertEquals(cfg("""
        {
          "list": [
            {
              "key": "value"
            },
            {
              "key": "value"
            }
          ]
        }
        """), source)

        val configuration = json.load(Source.of(source))
        val list = configuration.getSection("list").get()
        assertEquals(2, list.size())
    }

    @Test
    fun `should convert json like list`() {
        val source = converter.convertToCdn("""{ "list": [ "a", "b", "c" ] }""")
        assertEquals(listOf("a", "b", "c"), standard.load(Source.of(source)).getList("list", listOf("value")))
    }

    @Test
    fun `should escape operators in strings`() {
        val source = converter.convertToCdn(cfg("""{"key":"{value}"}"""))

        assertEquals(cfg("""
        {
          "key": "{value}"
        }
        """), source)
    }

    @Test
    fun `should parse json`() {
        val source = """{ "object": { "key": "value" }, "array": [ "1", "2" ] }"""

        val cdn = cfg("""
        object: {
          key: value
        }
        array: [
          1
          2
        ]
        """)

        val jsonResult = json.load(Source.of(source))
        val standardResult = standard.load(Source.of(cdn))

        assertEquals(standardResult.getString("object.key", "valueCDN"), jsonResult.getString("object.key", "valueJSON"))
        assertEquals(standardResult.getList("array", listOf("valueCDN")), jsonResult.getList("array", listOf("valueJSON")))
    }

}
