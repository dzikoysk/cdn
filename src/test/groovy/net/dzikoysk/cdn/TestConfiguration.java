package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.Description;

public class TestConfiguration {

    @Description("# Root entry description")
    public String rootEntry = "default value";

    @Description({ "", "// Section description" })
    public SectionConfiguration section = new SectionConfiguration();

    public static class SectionConfiguration {

        @Description({ "# Random value" })
        public Integer subEntry = 7;

    }

}
