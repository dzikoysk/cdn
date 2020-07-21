package net.dzikoysk.cdn.model


import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class CdnEntryTest {

    static final CdnEntry ENTRY = new CdnEntry('entry-name', [ '// entry-comment' ], 'entry-value')

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
        assertEquals([ '// entry-comment' ], ENTRY.getComments())
    }

}
