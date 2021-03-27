package net.dzikoysk.cdn.composers

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CdnFactory
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
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

        def configuration = CdnFactory.createStandard().load(source, Configuration.class)
        assertEquals(list, configuration.list)
        assertEquals(source, CdnFactory.createStandard().render(configuration))
    }

    @Test
    void 'should parse and render empty list' () {
        def source = """
        list: []
        """.stripIndent().trim()

        def configuration = CdnFactory.createYamlLike().load(source, Configuration.class)
        assertEquals(Collections.emptyList(), configuration.list)
        assertEquals(source, CdnFactory.createYamlLike().render(configuration))
    }

}
