package net.dzikoysk.cdn.model


import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class EntryTest {

    static final Entry ENTRY = Entry.of('entry-name: entry-value', ['// entry-comment' ])

    @Test
    void 'should return entry name' () {
        assertEquals 'entry-name', ENTRY.getName()
    }

    @Test
    void 'should return entry value' () {
        assertEquals 'entry-value', ENTRY.getValue()
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
