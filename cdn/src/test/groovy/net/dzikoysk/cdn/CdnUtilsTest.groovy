package net.dzikoysk.cdn

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class CdnUtilsTest {

    @Test
    void 'should remove string operators' () {
        assertEquals 'test', CdnUtils.destringify('test')
        assertEquals 'test', CdnUtils.destringify('"test"')
        assertEquals 'test', CdnUtils.destringify('`test`')
        assertEquals 'test', CdnUtils.destringify("'test'")
    }

    @Test
    void 'should add string operators if needed' () {
        assertEquals '"test"', CdnUtils.stringify('"test"')
        assertEquals '"test', CdnUtils.stringify('"test')
        assertEquals 'test"', CdnUtils.stringify('test"')

        assertEquals '" "', CdnUtils.stringify(' ')
        assertEquals '","', CdnUtils.stringify(',')
        assertEquals '"test, "', CdnUtils.stringify('test, ')
    }

}
