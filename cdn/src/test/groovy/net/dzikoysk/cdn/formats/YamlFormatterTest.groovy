package net.dzikoysk.cdn.formats

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
final class YamlFormatterTest {

    @Test
    void 'should convert indentation to brackets' () {
        def prettier = new YamlFormatter("""
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
