package net.dzikoysk.cdn

import net.dzikoysk.cdn.model.CdnEntry
import net.dzikoysk.cdn.model.CdnSection
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class CdnWriterTest {

    @Test
    void 'should compose simple entry' () {
        def entry = new CdnEntry('key', [ '# description' ], 'value')

        assertEquals """
            # description
            key: value
        """.stripIndent().trim(), CDN.compose(entry)
    }

    @Test
    void 'should compose simple section' () {
        def section = new CdnSection('section', ['# description' ], [:])

        assertEquals """
            # description
            section {
            }
        """.stripIndent().trim(), CDN.compose(section)
    }

    @Test
    void 'should compose section with sub section and entry' () {
        def section = new CdnSection('section', ['# description' ], [
            'sub': new CdnSection('sub', [], [
                'entry': new CdnEntry('entry', [ '# entry comment' ], 'value')
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
        """.stripIndent().trim(), CDN.compose(section)
    }


}
