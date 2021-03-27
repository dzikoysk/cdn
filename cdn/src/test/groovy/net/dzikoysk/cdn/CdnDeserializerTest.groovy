package net.dzikoysk.cdn

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class CdnDeserializerTest {

    @Test
    void 'should deserialize source into scheme' () {
        def configuration = CdnFactory.createStandard().load("""
        rootEntry: custom value
        section {
          subEntry: 7
          list [
            key: value
            value
          ],
          custom {
            count: 3080
          }
        }
        """, TestConfiguration.class)

        assertEquals 'custom value', configuration.rootEntry
        assertEquals 7, configuration.section.subEntry
        assertEquals Arrays.asList('key: value', 'value'), configuration.section.list
        assertEquals 'rtx', configuration.section.custom.id
        assertEquals 3080, configuration.section.custom.count
    }

}
