package net.dzikoysk.cdn.model

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class SectionTest {

    static final Section SECTION = new Section('name', ['# comment' ], [
        "sub": new Section("sub", [], [
            "entry": new Entry("entry", [], "value")
        ])
    ])

    @Test
    void 'should return section name' () {
        assertEquals 'name', SECTION.getName()
    }

    @Test
    void 'should contain entry' () {
        assertTrue SECTION.has('sub')
    }

    @Test
    void 'should return entry of sub section' () {
        assertEquals 'value', SECTION.getString('sub.entry')
    }

    @Test
    void 'should append sub element' () {
        Section section = new Section('section', [], [:])
        assertTrue section.getValue().isEmpty()

        section.append(new Entry('entry', [], 'value'))
        assertEquals 1, section.getValue().size()
    }

}
