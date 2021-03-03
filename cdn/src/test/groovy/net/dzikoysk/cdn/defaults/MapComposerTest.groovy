package net.dzikoysk.cdn.defaults

import groovy.transform.EqualsAndHashCode
import net.dzikoysk.cdn.CDN
import net.dzikoysk.cdn.entity.SectionValue
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class MapComposerTest {

    static class Configuration {
    
        public Map<Integer, Map<Integer, String>> map = Collections.emptyMap()
        
    }
    
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

        def configuration = CDN.defaultInstance().parse(Configuration.class, source)
        assertEquals(map, configuration.map)
        assertEquals(source, CDN.defaultInstance().render(configuration))
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

        def configuration = CDN.defaultYamlLikeInstance().parse(Configuration.class, source)
        assertEquals(map, configuration.map)
        assertEquals(source, CDN.defaultYamlLikeInstance().render(configuration))
    }


    @Test
    void 'should parse and render empty map' () {
        def source = """
        map: []
        """.stripIndent().trim()

        def configuration = CDN.defaultYamlLikeInstance().parse(Configuration.class, source)
        assertEquals(Collections.emptyMap(), configuration.map)
        assertEquals(source, CDN.defaultYamlLikeInstance().render(configuration))
    }

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

        def configuration = CDN.defaultInstance().parse(Configuration.class, source)
        println CDN.defaultYamlLikeInstance().render(configuration)
    }


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

        def configuration = CDN.defaultInstance().parse(ConfigurationWithSection.class, source)
        assertEquals(map, configuration.groups)
        assertEquals(source, CDN.defaultInstance().render(configuration))
    }

}
