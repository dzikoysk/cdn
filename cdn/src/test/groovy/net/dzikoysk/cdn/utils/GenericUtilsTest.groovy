package net.dzikoysk.cdn.utils

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertThrows

@CompileStatic
class GenericUtilsTest {

    @SuppressWarnings('unused')
    public static final Collection WITHOUT_GENERIC_TYPE = Collections.emptyList()
    @SuppressWarnings('unused')
    public static final Collection<String> WITH_GENERIC_TYPE = Collections.emptyList()

    @Test
    void 'should fetch generic type' () {
        assertEquals String.class, GenericUtils.getGenericClasses(GenericUtilsTest.class.getField('WITH_GENERIC_TYPE'))[0]
    }

    @Test
    void 'should throw missing generic signature' () {
        assertThrows IllegalArgumentException.class, { GenericUtils.getGenericTypes(GenericUtilsTest.class.getField('WITHOUT_GENERIC_TYPE')) }
    }

}
