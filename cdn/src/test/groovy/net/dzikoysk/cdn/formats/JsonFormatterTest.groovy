package net.dzikoysk.cdn.formats

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CDN
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
final class JsonFormatterTest {

    @Test
    void 'should convert json like format to cdn' () {
        def prettier = new JsonFormatter('{"list":[{"key":"value"},{"key":"value"}]}')
        def source = prettier.convertJsonToCdn().trim()

        assertEquals("""
        {
          "list": [
            {
              "key": "value"
            },
            {
              "key": "value"
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
        def source = new JsonFormatter('{ "list": [ "a", "b", "c" ] }').convertJsonToCdn()
        assertEquals([ 'a', 'b', 'c' ], CDN.defaultInstance().parse(source).getList('list', [ 'default' ]))
    }

    @Test
    void 'should escape operators in strings' () {
        def source = new JsonFormatter('{"key":"{value}"}').convertJsonToCdn().trim()

        assertEquals("""\
        {
          "key": "{value}"
        }
        """.stripIndent().trim(), source)
    }

}
