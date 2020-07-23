package net.dzikoysk.cdn

import net.dzikoysk.cdn.model.Entry
import net.dzikoysk.cdn.model.Section
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class CdnWriterTest {

    @Test
    void 'should compose simple entry' () {
        def entry = new Entry('key', ['# description' ], 'value')

        assertEquals """
            # description
            key: value
        """.stripIndent().trim(), CDN.defaultInstance().compose(entry)
    }

    @Test
    void 'should compose simple section' () {
        def section = new Section('section', ['# description' ])

        assertEquals """
            # description
            section {
            }
        """.stripIndent().trim(), CDN.defaultInstance().compose(section)
    }

    @Test
    void 'should compose section with sub section and entry' () {
        def section = new Section('section', ['# description' ], [
            'sub': new Section('sub', [], [
                'entry': new Entry('entry', ['# entry comment' ], 'value')
            ])
        ])

        assertEquals """
            # description
            section {
              sub {
                # entry comment
                entry: value
              }
            }
        """.stripIndent().trim(), CDN.defaultInstance().compose(section)
    }


}
