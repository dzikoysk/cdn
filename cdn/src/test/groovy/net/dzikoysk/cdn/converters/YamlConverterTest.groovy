package net.dzikoysk.cdn.formats

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.converters.YamlConverter
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
final class YamlConverterTest {

    private final YamlConverter converter = new YamlConverter()

    @Test
    void 'should convert indentation to brackets' () {
        def source = """
        key: value
        
        section:
          subSection:
            subKey: value
          # comment
          key: value
        """.stripIndent().trim()

        assertEquals("""\
        key: value
        
        section: {
          subSection: {
            subKey: value
          }
          # comment
          key: value
        }
        """.stripIndent(), converter.convertToCdn(source))
    }

}
