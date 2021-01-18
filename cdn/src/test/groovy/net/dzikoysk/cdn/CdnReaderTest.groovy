package net.dzikoysk.cdn

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

@CompileStatic
final class CdnReaderTest {

    @Test
    void 'should return entry' () {
        def result = CDN.defaultInstance().parse('key: value')
        def entry = result.getEntry('key').get()
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

        def section = result.getSection('section').get()
        assertNotNull section
        assertEquals([ '# comment1', '// comment2' ], section.getDescription())

        def entry = section.getEntry('entry').get()
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
            .enableYamlLikeFormatting()
            .build()

        def configuration = cdn.parse("""
        section :
          key : value
        """.stripIndent())

        assertEquals "value", configuration.getString("section.key", 'default')
    }

    @Test
    void 'should read quoted key and value' () {
        def result = CDN.defaultInstance().parse('''
        " key ": " value "
        ''')

        assertEquals ' key ', result.get(' key ').get().getName()
        assertEquals ' value ', result.getString(' key ', 'default')
    }

    static class ConfigTest {
        public List<String> list = Collections.singletonList("element")
    }

    @Test
    void 'should read empty yaml list' () {
        def result = CDN.defaultInstance().parse(ConfigTest.class, '''
        list: []
        ''')

        assertEquals Collections.emptyList(), result.list
    }

    @Test
    void 'should remove semicolons' () {
        def result = CDN.defaultInstance().parse("""
        a: b,
        c: d
        """)

        assertEquals 'b', result.getString('a', 'default')
    }

    @Test
    void 'should ignore empty root section' () {
        def result = CDN.defaultInstance().parse("""
        {
          "a": "b",
          "c": "d"
        }
        """)

        assertEquals 'b', result.getString('a', 'default')
        assertEquals 'd', result.getString('c', 'default')
    }

}
