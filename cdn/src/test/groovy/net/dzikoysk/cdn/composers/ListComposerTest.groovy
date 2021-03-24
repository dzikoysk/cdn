package net.dzikoysk.cdn.defaults

import net.dzikoysk.cdn.CDN
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class ListComposerTest {

    static class Configuration {
    
        public List<String> list = Collections.emptyList()
        
    }
    
    @Test
    void 'should parse and render lists' () {
        def source = """
        list [
          a
          b
        ]
        """.stripIndent().trim()

        def list = [
            "a",
            "b"
        ]

        def configuration = CDN.defaultInstance().parse(Configuration.class, source)
        assertEquals(list, configuration.list)
        assertEquals(source, CDN.defaultInstance().render(configuration))
    }


    @Test
    void 'should parse and render yaml like lists' () {
        def source = """
        list:
          - a
          - b
        """.stripIndent().trim()

        def list = [
            "a",
            "b"
        ]

        def configuration = CDN.defaultYamlLikeInstance().parse(Configuration.class, source)
        assertEquals(list, configuration.list)
        assertEquals(source, CDN.defaultYamlLikeInstance().render(configuration))
    }

    @Test
    void 'should parse and render empty list' () {
        def source = """
        list: []
        """.stripIndent().trim()

        def configuration = CDN.defaultYamlLikeInstance().parse(Configuration.class, source)
        assertEquals(Collections.emptyList(), configuration.list)
        assertEquals(source, CDN.defaultYamlLikeInstance().render(configuration))
    }

}
