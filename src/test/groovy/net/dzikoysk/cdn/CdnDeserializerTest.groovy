package net.dzikoysk.cdn

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class CdnDeserializerTest {

    @Test
    void 'should deserialize source into scheme' () {
        def configuration = CDN.defaultInstance().parse(TestConfiguration.class, """
        rootEntry: custom value
        section: {
          subEntry: 7
          list: [
            value
          ],
          custom: {
            count: 3080
          }
        }
        """)

        assertEquals 'custom value', configuration.rootEntry
        assertEquals 7, configuration.section.subEntry
        assertEquals Collections.singletonList('value'), configuration.section.list
        assertEquals 'rtx', configuration.section.custom.id
        assertEquals 3080, configuration.section.custom.count
    }

}
