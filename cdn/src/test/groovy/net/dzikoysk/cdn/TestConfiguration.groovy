package net.dzikoysk.cdn

import groovy.transform.CompileStatic
import net.dzikoysk.cdn.entity.Description
import net.dzikoysk.cdn.entity.SectionLink
import net.dzikoysk.cdn.entity.CustomComposer

@CompileStatic
class TestConfiguration {

    @Description('# Root entry description')
    public String rootEntry = "default value"

    @Description(['', '// Section description'])
    @SectionLink
    public SectionConfiguration section = new SectionConfiguration()

    static class SectionConfiguration {

        @Description('# Random value')
        public Integer subEntry = -1

        @Description('# List description')
        public List<String> list = [ 'record', 'record : with : semicolons' ]

        @Description('# Custom object')
        @CustomComposer(TestConfigurationCustomObject.CustomObjectComposer.class)
        public TestConfigurationCustomObject custom = new TestConfigurationCustomObject('rtx', 3070)

    }

}