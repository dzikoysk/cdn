package net.dzikoysk.cdn.model

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class EntryTest {

    static final Entry ENTRY = new Entry(['// entry-comment' ], 'entry-name', 'entry-value')

    @Test
    void 'should return entry name' () {
        assertEquals 'entry-name', ENTRY.getName()
    }

    @Test
    void 'should return entry value' () {
        assertEquals 'entry-value', ENTRY.getUnitValue()
    }

    @Test
    void 'should return entry comments' () {
        assertEquals([ '// entry-comment' ], ENTRY.getDescription())
    }

    @Test
    void 'should return entry record' () {
        assertEquals 'entry-name: entry-value', ENTRY.getRecord()
    }

}
