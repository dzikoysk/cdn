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
