package net.dzikoysk.cdn.model

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class CdnSectionTest {

    static final CdnSection SECTION = new CdnSection('name', [ '# comment' ], [
        "sub": new CdnSection("sub", [], [
            "entry": new CdnEntry("entry", [], "value")
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
        CdnSection section = new CdnSection('section', [], [:])
        assertTrue section.getValue().isEmpty()

        section.append(new CdnEntry('entry', [], 'value'))
        assertEquals 1, section.getValue().size()
    }

}
