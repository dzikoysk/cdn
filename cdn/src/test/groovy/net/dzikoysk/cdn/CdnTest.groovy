package net.dzikoysk.cdn

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class CdnTest {

    private final Cdn cdn = CdnFactory.createStandard()

    @Test
    void 'should parse and compose bidirectional input' () {
        def source = """
        # root description
        root {
          sub {
            // sub entry description
            subEntry: subValue
          }
          # root
          // entry
          # description
          rootEntry: rootValue
        }
        """.stripIndent().trim()

        def result = cdn.load(source)
        assertEquals source, cdn.render(result)
    }

}
