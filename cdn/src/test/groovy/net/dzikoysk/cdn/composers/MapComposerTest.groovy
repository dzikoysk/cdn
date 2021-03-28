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
import net.dzikoysk.cdn.Cdn
import net.dzikoysk.cdn.CdnFactory
import net.dzikoysk.cdn.entity.SectionValue
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class MapComposerTest {

    static class Configuration {
    
        public Map<Integer, Map<Integer, String>> map = Collections.emptyMap()
        
    }

    private final Cdn cdn = CdnFactory.createStandard()
    private final Cdn yaml = CdnFactory.createYamlLike()

    @Test
    void 'should parse and compose maps' () {
        def source = """
        map {
          1 {
            1: a
            2: b
          }
          2 {
            1: a
            2: b
          }
        }
        """.stripIndent().trim()

        def map = [
            1: [
                1: "a",
                2: "b"
            ],
            2: [
                1: "a",
                2: "b"
            ]
        ]

        def configuration = cdn.load(source, Configuration.class)
        assertEquals(map, configuration.map)
        assertEquals(source, cdn.render(configuration))
    }


    @Test
    void 'should parse and compose yaml like maps' () {
        def source = """
        map:
          1:
            1: a
            2: b
          2:
            1: a
            2: b
        """.stripIndent().trim()

        def map = [
            1: [
                1: "a",
                2: "b"
            ],
            2: [
                1: "a",
                2: "b"
            ]
        ]

        def configuration = yaml.load(source, Configuration.class)
        assertEquals(map, configuration.map)
        assertEquals(source, yaml.render(configuration))
    }


    @Test
    void 'should parse and render empty map' () {
        def source = """
        map: []
        """.stripIndent().trim()

        def configuration = yaml.load(source, Configuration.class)
        assertEquals(Collections.emptyMap(), configuration.map)
        assertEquals(source, yaml.render(configuration))
    }

    /* ???
    @Test
    void 'should generate only with keys' () {
        def source = """
        map [
          1
          2 {
            1: a
          }
        ]
        """.stripIndent().trim()

        def configuration = cdn.load(source, Configuration.class)
        println yaml.render(configuration)
    }
    */

    static class ConfigurationWithSection {

        public Map<String, Group> groups = [ "default": new Group() ]

        @SectionValue
        @EqualsAndHashCode
        static class Group {

            public String key = "value"

        }

    }

    @Test
    void 'should support sections as values' () {
        def source = """
        groups {
          first {
            key: 1st
          }
          second {
            key: 2nd
          }
        }
        """.stripIndent().trim()

        def first = new ConfigurationWithSection.Group()
        first.key = '1st'

        def second = new ConfigurationWithSection.Group()
        second.key = '2nd'

        def map = [
                "first": first,
                "second": second
        ]

        def configuration = cdn.load(source, ConfigurationWithSection.class)
        assertEquals(map, configuration.groups)
        assertEquals(source, cdn.render(configuration))
    }

}
