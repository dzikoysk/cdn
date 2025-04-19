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

class ListComposerTest : CdnSpec() {

    class ConfigurationWithEmptyList {
        var list = emptyList<String>()
    }

    class ConfigurationWithListOfValues {
        var elements = listOf(ElementConfiguration(), ElementConfiguration())
    }

    @Test
    fun `should load list`() {
        val source = cfg("""
        list [
          a:1
          b:2
        ]
        """)

        val configuration = assertOk(standard.loadAs<ConfigurationWithEmptyList>(Source.of(source)))
        assertEquals(listOf("a:1", "b:2"), configuration.list)

        val expectedSource = cfg("""
        list [
          "a:1"
          "b:2"
        ]
        """)

        assertEquals(expectedSource, assertOk(standard.render(configuration)))
    }

    @Test
    fun `should load object based list`() {
        val configuration = assertOk(standard.loadAs<ConfigurationWithListOfValues>(Source.empty()))

        val formattedSource = cfg("""
        elements [
          {
            name: default value
          }
          {
            name: default value
          }
        ]
        """)

        assertEquals(formattedSource, assertOk(standard.render(configuration)))
    }

}
