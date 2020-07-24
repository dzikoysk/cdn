package net.dzikoysk.cdn


import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class CdnPrettierTest {

    @Test
    void 'should convert indentation to brackets' () {
        def prettier = new CdnPrettier("""
        key: value
        
        section:
          subSection:
            subKey: value
          # comment
          key: value
        """.stripIndent().trim())

        assertEquals("""\
        key: value
        
        section: {
          subSection: {
            subKey: value
          }
          # comment
          key: value
        }
        """.stripIndent(), prettier.tryToConvertIndentationInADumbWay())
    }

}
