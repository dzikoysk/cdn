package net.dzikoysk.cdn.model

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

@CompileStatic
final class SectionTest {

    static final Section SECTION = new Section('name', ['# comment' ], [
        new Section("sub", [], [
            Entry.of("entry: value", []),
            Entry.of("int-entry: 7", []),
            Entry.ofPair('boolKey', 'true', [])
        ])
    ])

    @Test
    void 'should return section name' () {
        assertEquals 'name', SECTION.getName()
    }

    @Test
    void 'should return int' () {
        assertEquals 7, SECTION.getInt('sub.int-entry', 0)
    }

    @Test
    void 'should return boolean' () {
        assertEquals true, SECTION.getBoolean('sub.boolKey', false)
    }

    @Test
    void 'should return null' () {
        assertTrue SECTION.get('sub.unknown').isEmpty()
        assertTrue SECTION.get('unknown.entry').isEmpty()
    }

    @Test
    void 'should contain entry' () {
        assertTrue SECTION.has('sub')
        assertFalse SECTION.has('unknown')
    }

    @Test
    void 'should return entry of sub section' () {
        assertEquals 'value', SECTION.getString('sub.entry', 'default')
    }

    @Test
    void 'should return entry' () {
        assertEquals SECTION.getEntry('sub.int-entry').get(), SECTION.getSection(0).get().getEntry(1).get()
    }

    @Test
    void 'should append sub element' () {
        Section section = new Section('section', [])
        assertTrue section.getValue().isEmpty()

        section.append(Entry.of('entry: value', []))
        assertEquals 1, section.getValue().size()
    }

    @Test
    void 'should return list' () {
        Section section = new Section('section', [], [
            new Section('list', [], [
                Entry.of('record 1', []),
                Entry.of('- record 2 : with semicolon', [])
            ])
        ])

        assertEquals(['record 1', '- record 2 : with semicolon'], section.getList('list', [ 'default' ]))
    }

    @Test
    void 'should remove list operators' () {
        Section section = new Section('list', [], [
            Entry.of('- val1 ', []),
            Entry.of('- " val2 "', [])
        ])

        assertEquals(['val1', ' val2 '], section.getList())
    }

}
