package net.dzikoysk.cdn.defaults

import net.dzikoysk.cdn.CDN
import net.dzikoysk.cdn.entity.Description
import org.junit.jupiter.api.Test

class SimpleComposerTest {

    static class Configuration {

        @Description('# Separator with space')
        public String separator = ", "

    }

    @Test
    void 'should' () {
        String currentSource = CDN.defaultYamlLikeInstance().render(new Configuration())

        1.upto(5) {
            println(currentSource)
            def configuration = CDN.defaultYamlLikeInstance().parse(Configuration.class, currentSource)
            currentSource = CDN.defaultYamlLikeInstance().render(configuration)
        }
    }

}
