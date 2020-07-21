package net.dzikoysk.cdn

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class CdnDeserializerTest {

    @Test
    void 'should deserialize source into scheme' () {
        def configuration = Cdn.defaultInstance().parse(TestConfiguration.class, 'rootEntry: custom value')
        assertEquals 'custom value', configuration.rootEntry
    }

}
