package net.dzikoysk.cdn

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class CdnParserTest {

    static final CdnParser PARSER = new CdnParser()

    @Test
    void 'should return entry' () {
        def result = PARSER.parse('key: value')
        def entry = result.getValue().get('key')

        assertNotNull entry
        assertEquals 'key', entry.getName()
        assertEquals 'value', entry.getValue()
    }

    @Test
    void 'should return section with comments and entry' () {
        def result = PARSER.parse('''
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
        def result = PARSER.parse('''
        # c1
        s1 {
            # c2
            s2 {
                # c3
                some: entry
                
                s3 {
                    
                }
            }
        }
        ''')

        assertTrue result.has('s1.s2.s3')
    }

}
