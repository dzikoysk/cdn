package net.dzikoysk.cdn.converters

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CDN
import net.dzikoysk.cdn.converters.JsonConverter
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
final class JsonConverterTest {

    private final JsonConverter converter = new JsonConverter()

    @Test
    void 'should convert json like format to cdn' () {
        def source = converter.convertToCdn('{"list":[{"key":"value"},{"key":"value"}]}').trim()

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
        def source = converter.convertToCdn('{ "list": [ "a", "b", "c" ] }')
        assertEquals([ 'a', 'b', 'c' ], CDN.defaultInstance().parse(source).getList('list', [ 'default' ]))
    }

    @Test
    void 'should escape operators in strings' () {
        def source = converter.convertToCdn('{"key":"{value}"}').trim()

        assertEquals("""\
        {
          "key": "{value}"
        }
        """.stripIndent().trim(), source)
    }

}
