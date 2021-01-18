package net.dzikoysk.cdn

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
final class CdnPrettierTest {

    @Test
    void 'should convert json like format to cdn' () {
        def prettier = new CdnPrettier('{"list":[{"key":"value"},{"key":"value"}]}')
        def source = prettier.tryToInsertNewLinesInADumbWay().trim()

        assertEquals("""
        {
        "list":[
        {
        "key":"value"
        },
        {
        "key":"value"
        }
        ]
        }
        """.stripIndent().trim(), source)

        def configuration = CDN.defaultInstance().parse(source)
        def list = configuration.getSection('list').get()
        assertEquals 2, list.size()
    }

    @Test
    void 'should convert json like list' () {
        def source = new CdnPrettier('{ "list": [ "a", "b", "c" ] }').tryToInsertNewLinesInADumbWay()
        println source
        assertEquals([ 'a', 'b', 'c' ], CDN.defaultInstance().parse(source).getList('list', [ 'default' ]))
    }

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
