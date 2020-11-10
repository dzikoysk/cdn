package net.dzikoysk.cdn


import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows

class CdnUtilsTest {

    @SuppressWarnings('unused')
    public static final Collection WITHOUT_GENERIC_TYPE = Collections.emptyList()
    @SuppressWarnings('unused')
    public static final Collection<String> WITH_GENERIC_TYPE = Collections.emptyList()

    @Test
    void 'should fetch generic type' () {
        assertEquals String.class, CdnUtils.getGenericType(CdnUtilsTest.class.getField('WITH_GENERIC_TYPE'))
    }

    @Test
    void 'should throw missing generic signature' () {
        assertThrows IllegalArgumentException.class, { CdnUtils.getGenericType(CdnUtilsTest.class.getField('WITHOUT_GENERIC_TYPE')) }
    }

    @Test
    void 'should remove string operators' () {
        assertEquals 'test', CdnUtils.destringify('test')
        assertEquals 'test', CdnUtils.destringify('"test"')
        assertEquals 'test', CdnUtils.destringify('`test`')
        assertEquals 'test', CdnUtils.destringify("'test'")
    }

}
