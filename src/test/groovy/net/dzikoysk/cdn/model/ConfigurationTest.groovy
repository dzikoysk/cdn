package net.dzikoysk.cdn.model


import org.junit.jupiter.api.Test
import org.panda_lang.utilities.commons.StringUtils

import static org.junit.jupiter.api.Assertions.assertEquals

final class ConfigurationTest {

    static final Configuration ROOT = new Configuration()

    static {
        ROOT.append(Entry.of('entry: value', []))
    }

    @Test
    void 'should contain empty name' () {
        assertEquals StringUtils.EMPTY, ROOT.getName()
    }

}
