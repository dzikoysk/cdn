package net.dzikoysk.cdn.composers

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.CdnFactory
import net.dzikoysk.cdn.entity.Description
import org.junit.jupiter.api.Test

@CompileStatic
class SimpleComposerTest {

    static class Configuration {

        @Description('# Separator with space')
        public String separator = ", "

        public String empty = ""

    }

    // Make sure that these tricky values are properly handled by CDN
    // ~ https://github.com/dzikoysk/cdn/issues/40 - comma operator
    // ~ https://github.com/dzikoysk/cdn/issues/43 - empty strings
    @Test
    void 'should properly serialize and deserialize empty value, value with comma and values with spaces' () {
        String currentSource = CdnFactory.createYamlLike().render(new Configuration())

        1.upto(3) {
            println(currentSource)
            def configuration = CdnFactory.createYamlLike().load(currentSource, Configuration.class)
            currentSource = CdnFactory.createYamlLike().render(configuration)
        }
    }

}
