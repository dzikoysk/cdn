package net.dzikoysk.cdn.features

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CdnFactory
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
final class JsonFeatureTest {

    private final JsonFeature converter = new JsonFeature()

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

        def configuration = CdnFactory.createStandard().load(source)
        def list = configuration.getSection('list').get()
        assertEquals 2, list.size()
    }

    @Test
    void 'should convert json like list' () {
        def source = converter.convertToCdn('{ "list": [ "a", "b", "c" ] }')
        assertEquals([ 'a', 'b', 'c' ], CdnFactory.createStandard().load(source).getList('list', ['default' ]))
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

    @Test
    void 'should parse json' () {
        def json = '{ "object": { "key": "value" }, "array": [ "1", "2" ] }'

        def cdn = """
        object: {
          key: value
        }
        array: [
          1
          2
        ]
        """

        def jsonResult = CdnFactory.createJson().load(json)
        def cdnResult = CdnFactory.createStandard().load(cdn)

        assertEquals cdnResult.getString('object.key', 'defaultCDN'), jsonResult.getString('object.key', 'defaultJSON')
        assertEquals cdnResult.getList('array', [ 'defaultCDN' ]), jsonResult.getList('array', [ 'defaultJSON' ])
    }


}
