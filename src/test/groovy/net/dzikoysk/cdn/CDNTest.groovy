package net.dzikoysk.cdn


import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class CDNTest {

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

        def result = CDN.parse(source)
        assertEquals source, CDN.compose(result)
    }

}
