package net.dzikoysk.cdn

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class CdnReaderTest {

    @Test
    void 'should return entry' () {
        def result = CDN.parse('key: value')
        def entry = result.getValue().get('key')

        assertNotNull entry
        assertEquals 'key', entry.getName()
        assertEquals 'value', entry.getValue()
    }

    @Test
    void 'should return section with comments and entry' () {
        def result = CDN.parse('''
        # comment1
        // comment2
        section {
            entry: value   
        }
        ''')

        def section = result.getSection('section')
        assertNotNull section
        assertEquals([ '# comment1', '// comment2' ], section.getComments())

        def entry = section.getEntry('entry')
        assertNotNull entry
        assertEquals 'value', entry.getValue()
    }

    @Test
    void 'should return nested section' () {
        def result = CDN.parse('''
        # c1
        s1 {
            # c2
            some: entry
            s2 {
            }
        }
        ''')

        assertTrue result.has('s1.s2')
    }

}
