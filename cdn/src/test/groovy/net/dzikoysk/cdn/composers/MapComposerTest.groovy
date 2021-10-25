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

package net.dzikoysk.cdn.composers

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import net.dzikoysk.cdn.CdnSpec
import net.dzikoysk.cdn.entity.Contextual
import net.dzikoysk.cdn.source.Source
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class MapComposerTest extends CdnSpec {

    static class ConfigurationWithMaps {

        public Map<Integer, Map<Integer, String>> map = Collections.emptyMap()

        public Map<String, Element> elements = [
                'a': new Element(),
        ]

        @Contextual
        static class Element {

            public String name = "default name"

        }

    }

    static class ConfigurationWithSection {

        public Map<String, Group> groups = [ "default": new Group() ]

        @Contextual
        @EqualsAndHashCode
        static class Group {

            public String key = "value"

        }

    }

    @Test
    void 'should parse and compose maps' () {
        def source = cfg("""
        map {
          1 {
            1: a
          }
          2 {
            1: a
          }
        }
        elements {
          a {
            name: default name
          }
        }
        """)

        def map = [
            1: [
                1: "a",
            ],
            2: [
                1: "a",
            ]
        ]

        def configuration = standard.load(Source.of(source), ConfigurationWithMaps.class)
        assertEquals(map, configuration.map)
        assertEquals(source, standard.render(configuration))
    }

    @Test
    void 'should support sections as values' () {
        def source = cfg("""
        groups {
          first {
            key: 1st
          }
          second {
            key: 2nd
          }
        }
        """)

        def first = new ConfigurationWithSection.Group()
        first.key = '1st'

        def second = new ConfigurationWithSection.Group()
        second.key = '2nd'

        def map = [
                "first": first,
                "second": second
        ]

        def configuration = standard.load(Source.of(source), ConfigurationWithSection.class)
        assertEquals(map, configuration.groups)
        assertEquals(source, standard.render(configuration))
    }

}
