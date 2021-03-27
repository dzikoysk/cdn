package net.dzikoysk.cdn

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.model.Entry
import net.dzikoysk.cdn.model.Section
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
class CdnWriterTest {

    private final Cdn cdn = CdnFactory.createStandard()

    @Test
    void 'should compose simple entry' () {
        def entry = new Entry(['# description' ], 'key', 'value')

        assertEquals """
        # description
        key: value
        """.stripIndent().trim(), cdn.render(entry)
    }

    @Test
    void 'should compose simple section' () {
        def section = new Section(['# description' ], 'section')

        assertEquals """
        # description
        section {
        }
        """.stripIndent().trim(), cdn.render(section)
    }

    @Test
    void 'should compose section with sub section and entry' () {
        def section = new Section(['# description' ], 'section', [
            new Section([], 'sub', [
                new Entry(['# entry comment'], 'entry', 'value')
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
        """.stripIndent().trim(), cdn.render(section)
    }

    @Test
    void 'should replace simple placeholder' () {
        def entry = new Entry([ '# ${{placeholder}}' ], 'key', 'value')
        def cdn = Cdn.configure()
            .registerPlaceholders([ 'placeholder': 'dance with me' ])
            .build()

        assertEquals """
        # dance with me
        key: value
        """.stripIndent().trim(), cdn.render(entry)
    }

}
