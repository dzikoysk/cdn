package net.dzikoysk.cdn.model

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class SectionTest {

    static final Section SECTION = new Section('name', ['# comment' ], [
        "sub": new Section("sub", [], [
            "entry": new Entry("entry", [], "value"),
            "int-entry": new Entry("int-entry", [], "7")
        ])
    ])

    @Test
    void 'should return section name' () {
        assertEquals 'name', SECTION.getName()
    }

    @Test
    void 'should return int' () {
        assertEquals 7, SECTION.getInt('sub.int-entry')
    }

    @Test
    void 'should return null' () {
        assertNull SECTION.get('sub.unknown')
        assertNull SECTION.get('unknown.entry')
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
