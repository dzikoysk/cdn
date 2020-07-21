package net.dzikoysk.cdn

import net.dzikoysk.cdn.entity.Description

class TestConfiguration {

    @Description("# Root entry description")
    public String rootEntry = "default value";

    @Description([ "", "// Section description" ])
    public SectionConfiguration section = new SectionConfiguration();

    static class SectionConfiguration {

        @Description([ "# Random value" ])
        public Integer subEntry = 7;

    }

}
