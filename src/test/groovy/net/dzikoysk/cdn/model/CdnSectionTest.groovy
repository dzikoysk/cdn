package net.dzikoysk.cdn.model


import org.junit.jupiter.api.Test
import org.panda_lang.utilities.commons.collection.Maps

import static org.junit.jupiter.api.Assertions.assertEquals

class CdnSectionTest {

    static final CdnSection SECTION = new CdnSection('section-name', Maps.of("entry", new CdnEntry("entry", "value", [])), [ '# comment' ])

    @Test
    void 'should return section name' () {
        assertEquals 'section-name', SECTION.getName()
    }

}
