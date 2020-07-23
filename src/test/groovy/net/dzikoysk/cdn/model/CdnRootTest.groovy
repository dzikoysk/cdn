package net.dzikoysk.cdn.model


import org.junit.jupiter.api.Test
import org.panda_lang.utilities.commons.StringUtils

import static org.junit.jupiter.api.Assertions.assertEquals

final class CdnRootTest {

    static final CdnRoot ROOT = new CdnRoot()

    static {
        ROOT.append(new CdnEntry('entry', [], 'value'))
    }

    @Test
    void 'should contain empty name' () {
        assertEquals StringUtils.EMPTY, ROOT.getName()
    }

}
