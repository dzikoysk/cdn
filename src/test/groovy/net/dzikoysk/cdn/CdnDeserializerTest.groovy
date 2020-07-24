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
        }
        """)

        assertEquals 'custom value', configuration.rootEntry
        assertEquals 7, configuration.section.subEntry
    }

}
