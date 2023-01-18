package net.dzikoysk.cdn.feature;

import net.dzikoysk.cdn.Cdn;
import net.dzikoysk.cdn.CdnException;
import net.dzikoysk.cdn.CdnFactory;
import net.dzikoysk.cdn.entity.Description;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import panda.std.Result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static panda.std.ResultAssertions.assertOk;

class MultiDescriptionJavaTest {

    public static class ConfigurationWithMultiDescription {

        @Description("#")
        @Description("# Test multi line banner")
        @Description("#")
        @Description(" ")
        public String value = "test";
    }

    @Test
    @DisplayName("should render multiline banner with empty descriptions on java implementation")
    void  test() {
        Cdn standardJava = CdnFactory.createStandard();
        Result<String, CdnException> renderResult = standardJava.render(new ConfigurationWithMultiDescription());
        String render = assertOk(renderResult);

        assertEquals(
                "#\n" +
                "# Test multi line banner\n" +
                "#\n" +
                " \n" +
                "value: test",
                render
        );
    }

}
