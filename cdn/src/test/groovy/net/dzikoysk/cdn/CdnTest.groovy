package net.dzikoysk.cdn

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class CdnTest {

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

        def result = Cdn.defaultInstance().parse(source)
        assertEquals source, Cdn.defaultInstance().render(result)
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

        def jsonResult = Cdn.defaultInstance().parseJson(json)
        def cdnResult = Cdn.defaultInstance().parse(cdn)

        assertEquals cdnResult.getString('object.key', 'defaultCDN'), jsonResult.getString('object.key', 'defaultJSON')
        assertEquals cdnResult.getList('array', [ 'defaultCDN' ]), jsonResult.getList('array', [ 'defaultJSON' ])
    }

}
