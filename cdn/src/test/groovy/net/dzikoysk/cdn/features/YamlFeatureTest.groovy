package net.dzikoysk.cdn.features

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CdnFactory
import net.dzikoysk.cdn.entity.SectionLink
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
final class YamlFeatureTest {

    private final YamlFeature converter = new YamlFeature()

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
        
        section {
          subSection {
            subKey: value
          }
          # comment
          key: value
        }
        """.stripIndent(), converter.convertToCdn(source))
    }


    @Test
    void 'should parse and render yaml like lists' () {
        def source = """
        list:
          - a
          - b
        """.stripIndent().trim()

        def configuration = CdnFactory.createYamlLike().load(source)

        assertEquals([ "a", "b" ], configuration.getArray('list').get().getList())
        assertEquals(source, CdnFactory.createYamlLike().render(configuration))
    }

    @Test
    void 'should read lines with brackets' () {
        def result = CdnFactory.createYamlLike().load('''
        section:
            key: {value}
        ''')

        assertEquals '{value}', result.getString('section.key', '')
    }

    @Test
    void 'should use prettier' () {
        def configuration = CdnFactory.createYamlLike().load("""
        section :
          key : value
        """.stripIndent())

        assertEquals "value", configuration.getString("section.key", 'default')
    }

    @Test
    void 'should compose indentation based source' () {
        assertEquals """
        section:
          key: value
        """.stripIndent().trim(), CdnFactory.createYamlLike().render(new Object() {
            @SectionLink
            public Object section = new Object() {
                public String key = "value"
            }
        })
    }

}
