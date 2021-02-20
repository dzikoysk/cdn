package net.dzikoysk.cdn.defaults

import net.dzikoysk.cdn.CDN
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

}
