package net.dzikoysk.cdn.model

import groovy.transform.CompileStatic
import org.junit.jupiter.api.Test
import org.panda_lang.utilities.commons.StringUtils

import static org.junit.jupiter.api.Assertions.assertEquals

@CompileStatic
final class ConfigurationTest {

    static final Configuration ROOT = new Configuration()

    static {
        ROOT.append(new Entry([], 'entry', 'value'))
    }

    @Test
    void 'should contain empty name' () {
        assertEquals StringUtils.EMPTY, ROOT.getName()
    }

}
