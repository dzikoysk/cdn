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
import net.dzikoysk.cdn.serdes.composers.MapComposerTest.ConfigurationWithMaps
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class YamlLikeMapComposerTest : CdnSpec(){

    class EmptyMapConfiguration {
        var map = mapOf("key" to "value")
    }

    @Test
    fun `should parse and render empty map`() {
        val source = cfg("""
        map: []
        """)

        val configuration = yamlLike.loadAs<EmptyMapConfiguration>(Source.of(source))
        assertEquals(emptyMap<Int, Map<Int, String>>(), configuration.map)

        assertEquals(cfg("map: []"), yamlLike.render(configuration))
    }


    @Test
    fun `should parse and compose yaml like maps`() {
        val source = cfg("""
        map:
          1:
            1: a
          2:
            1: a
        """)

        val expectedMap = mapOf(
            1 to mapOf(1 to "a"),
            2 to mapOf(1 to "a")
        )

        val configuration = yamlLike.loadAs<ConfigurationWithMaps>(Source.of(source))
        assertEquals(expectedMap, configuration.map)

        assertEquals(cfg("""
        map:
          1:
            1: a
          2:
            1: a
        elements:
          a:
            name: default value
        """), yamlLike.render(configuration))
    }


}
