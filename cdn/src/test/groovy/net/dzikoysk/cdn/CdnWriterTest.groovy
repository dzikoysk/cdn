package net.dzikoysk.cdn

import net.dzikoysk.cdn.entity.SectionLink
import net.dzikoysk.cdn.model.Entry
import net.dzikoysk.cdn.model.Section
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class CdnWriterTest {

    @Test
    void 'should compose simple entry' () {
        def entry = Entry.of('key: value', ['# description' ])

        assertEquals """
        # description
        key: value
        """.stripIndent().trim(), Cdn.defaultInstance().render(entry)
    }

    @Test
    void 'should compose simple section' () {
        def section = new Section('section', ['# description' ])

        assertEquals """
        # description
        section {
        }
        """.stripIndent().trim(), Cdn.defaultInstance().render(section)
    }

    @Test
    void 'should compose section with sub section and entry' () {
        def section = new Section('section', ['# description' ], [
            new Section('sub', [], [
                Entry.of('entry: value', ['# entry comment' ])
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
        """.stripIndent().trim(), Cdn.defaultInstance().render(section)
    }

    @Test
    void 'should compose indentation based source' () {
        def cdn = Cdn.configure()
            .enableYamlLikeFormatting()
            .build()

        assertEquals """
        section:
          key: value
        """.stripIndent().trim(), cdn.render(new Object() {
            @SectionLink
            public Object section = new Object() {
                public String key = "value"
            }
        })
    }

    @Test
    void 'should replace simple placeholder' () {
        def entry = Entry.of('key: value', ['# ${{placeholder}}' ])
        def cdn = Cdn.configure()
            .registerPlaceholders([ 'placeholder': 'dance with me' ])
            .build()

        assertEquals """
        # dance with me
        key: value
        """.stripIndent().trim(), cdn.render(entry)
    }

}
