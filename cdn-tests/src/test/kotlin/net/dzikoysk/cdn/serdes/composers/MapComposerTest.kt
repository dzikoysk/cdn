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

package net.dzikoysk.cdn.serdes.composers

import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.loadAs
import net.dzikoysk.cdn.serdes.ElementConfiguration
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

class MapComposerTest : CdnSpec() {

    class ConfigurationWithMaps {
        var map = emptyMap<Int, Map<String, String>>()
        var elements = mapOf("a" to ElementConfiguration())
    }

    class ConfigurationWithSection {
        var groups = mapOf("default" to ElementConfiguration())
    }

    @Test
    fun `should parse and compose maps`() {
        val source = cfg("""
        map {
          1 {
            1: a
          }
          2 {
            1: a
            "1:2": b
          }
        }
        elements {
          a {
            name: default name
          }
        }
        """)

        val map = mapOf(
            1 to mapOf("1" to "a"),
            2 to mapOf("1" to "a", "1:2" to "b"),
        )

        val configuration = assertOk(standard.loadAs<ConfigurationWithMaps>(Source.of(source)))
        assertEquals(map, configuration.map)
        assertEquals(source, assertOk(standard.render(configuration)))
    }

    @Test
    fun `should support sections as values`() {
        val source = cfg("""
        groups {
          first {
            name: 1st
          }
          second {
            name: 2nd
          }
        }
        """)

        val first = ElementConfiguration("1st")
        val second = ElementConfiguration("2nd")
        val map = mapOf("first" to first, "second" to second)

        val configuration = assertOk(standard.loadAs<ConfigurationWithSection>(Source.of(source)))
        assertEquals(map, configuration.groups)
        assertEquals(source, assertOk(standard.render(configuration)))
    }

}
