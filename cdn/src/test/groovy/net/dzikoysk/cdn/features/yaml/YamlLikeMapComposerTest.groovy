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

package net.dzikoysk.cdn.features.yaml

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.composers.MapComposerTest
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class YamlLikeMapComposerTest extends CdnSpec {

    @Test
    void 'should parse and render empty map' () {
        def source = cfg("""
        map: []
        """)

        def configuration = yamlLike.load(Source.of(source), MapComposerTest.ConfigurationWithMaps.class)
        assertEquals(Collections.emptyMap(), configuration.map)

        assertEquals(cfg("""
        map: []
        elements:
          a:
            name: default name
        """), yamlLike.render(configuration))
    }


    @Test
    void 'should parse and compose yaml like maps' () {
        def source = cfg("""
        map:
          1:
            1: a
          2:
            1: a
        """)

        def map = [
                1: [
                        1: "a",
                ],
                2: [
                        1: "a",
                ]
        ]

        def configuration = yamlLike.load(Source.of(source), MapComposerTest.ConfigurationWithMaps.class)
        assertEquals(map, configuration.map)

        assertEquals(cfg("""
        map:
          1:
            1: a
          2:
            1: a
        elements:
          a:
            name: default name
        """), yamlLike.render(configuration))
    }


}
