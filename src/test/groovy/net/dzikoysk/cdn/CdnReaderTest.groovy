package net.dzikoysk.cdn


import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class CdnReaderTest {

    @Test
    void 'should return entry' () {
        def result = CDN.defaultInstance().parse('key: value')
        def entry = result.get('key')

        assertNotNull entry
        assertEquals 'key', entry.getName()
        assertEquals 'value', entry.getValue()
    }

    @Test
    void 'should return section with comments and entry' () {
        def result = CDN.defaultInstance().parse('''
        # comment1
        // comment2
        section {
            entry: value
        }
        ''')

        def section = result.getSection('section')
        assertNotNull section
        assertEquals([ '# comment1', '// comment2' ], section.getDescription())

        def entry = section.getEntry('entry')
        assertNotNull entry
        assertEquals 'value', entry.getValue()
    }

    @Test
    void 'should return nested section' () {
        def result = CDN.defaultInstance().parse('''
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

    @Test
    void 'should skip empty lines' () {
        def result = CDN.defaultInstance().parse('')
        assertTrue result.getValue().isEmpty()
    }

    @Test
    void 'should use prettier' () {
        def cdn = CDN.configure()
            .enableIndentationFormatting()
            .build()

        def configuration = cdn.parse("""
        section :
          key : value
        """.stripIndent())

        assertEquals "value", configuration.getString("section.key")
    }

    @Test
    void 'should read quoted key and value' () {
        def result = CDN.defaultInstance().parse('''
        " key ": " value "
        ''')

        assertEquals ' key ', result.get(' key ').getName()
        assertEquals ' value ', result.getString(' key ')
    }

}
