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

package net.dzikoysk.cdn.module.yaml

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.entity.Contextual
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class YamlLikeFeatureTest : CdnSpec() {

    private val converter = YamlLikeModule(true)

    @Test
    fun `should convert indentation to brackets`() {
        val source = cfg("""
        key: value
        section:
          subSection:
            subKey: value
          # comment
          key: value
        """)

        assertEquals("""
        key: value
        section {
          subSection {
            subKey: value
          }
          # comment
          key: value
        }
        
        """.trimIndent(), converter.convertToCdn(source))
    }

    @Test
    fun `should parse and render yaml like lists`() {
        val source = cfg("""
        list:
          - a
          - b
        """)

        val configuration = assertOk(yamlLike.load(Source.of(source)))

        assertEquals(listOf("a", "b"), configuration.getArray("list").get().list)
        assertEquals(source, assertOk(yamlLike.render(configuration)))
    }

    @Test
    fun `should read lines with brackets`() {
        val result = assertOk(yamlLike.load(Source.of("""
        section:
            key: {value}
        """)))

        assertEquals("{value}", result.getString("section.key", ""))
    }

    @Test
    fun `should use prettier`() {
        val configuration = assertOk(yamlLike.load(Source.of("""
        section :
          key : value
        """.trimIndent())))

        assertEquals("value", configuration.getString("section.key", "value"))
    }

    class IndentationTestConfiguration {
        @Contextual
        var section: Any = object : Any() {
            var key = "default value"
        }
    }

    @Test
    fun `should compose indentation based source`() {
        assertEquals(cfg("""
        section:
          key: "default value"
        """), assertOk(yamlLike.render(IndentationTestConfiguration())))
    }

}
