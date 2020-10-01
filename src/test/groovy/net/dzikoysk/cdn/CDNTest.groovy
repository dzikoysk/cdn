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

        def result = CDN.defaultInstance().parse(source)
        assertEquals source, CDN.defaultInstance().compose(result)
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

        def jsonResult = CDN.defaultInstance().parseJson(json)
        def cdnResult = CDN.defaultInstance().parse(cdn)

        assertEquals cdnResult.getString('key'), jsonResult.getString('key')
        assertEquals cdnResult.getList('array'), jsonResult.getList('array')
    }

}
