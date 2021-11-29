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
import net.dzikoysk.cdn.loadAs
import net.dzikoysk.cdn.serdes.ElementConfiguration
import net.dzikoysk.cdn.serdes.composers.ListComposerTest.ConfigurationWithEmptyList
import net.dzikoysk.cdn.serdes.composers.ListComposerTest.ConfigurationWithListOfValues
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import panda.std.ResultAssertions.assertOk

internal class YamlLikeListComposerTest : CdnSpec() {

    @Test
    fun `should load empty list`() {
        val source = cfg("""
        list: []
        """)

        val configuration = assertOk(yamlLike.loadAs<ConfigurationWithEmptyList>(Source.of(source)))
        assertEquals(emptyList<String>(), configuration.list)

        val expectedRender = cfg("""
        list: []
        """)

        assertEquals(expectedRender, assertOk(yamlLike.render(configuration)))
    }

    @Test
    fun `should render objects in list`() {
        val source = cfg("""
        elements:
          - :
            name: custom 1
          - :
            name: custom 2
        """)

        val configuration = assertOk(yamlLike.loadAs<ConfigurationWithListOfValues>(Source.of(source)))
        assertEquals(listOf(ElementConfiguration("custom 1"), ElementConfiguration("custom 2")), configuration.elements)
        assertEquals(source, assertOk(yamlLike.render(configuration)))
    }

    @Test
    fun `should escape problematic characters`() {
        val source = cfg("""
        list:
          - a:
          - b {
          - c
        """)

        val configuration = assertOk(yamlLike.loadAs<ConfigurationWithEmptyList>(Source.of(source)))
        assertEquals(listOf("a:", "b {", "c"), configuration.list)

        val expectedRender = cfg("""
        list:
          - "a:"
          - "b {"
          - c
        """)

        assertEquals(expectedRender, assertOk(yamlLike.render(configuration)))
    }

}

