package net.dzikoysk.cdn

import net.dzikoysk.cdn.entity.Description
import net.dzikoysk.cdn.entity.SectionLink

class TestConfiguration {

    @Description("# Root entry description")
    public String rootEntry = "default value"

    @Description("")
    @Description("// Section description")
    @SectionLink
    public SectionConfiguration section = new SectionConfiguration()

    static class SectionConfiguration {

        @Description("# Random value")
        public Integer subEntry = -1


        @Description("# List description")
        public List<String> list = [ 'record', 'record : with : semicolons' ]

    }

}
