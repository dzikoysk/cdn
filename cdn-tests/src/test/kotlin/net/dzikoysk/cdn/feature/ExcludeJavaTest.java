package net.dzikoysk.cdn.feature;

import net.dzikoysk.cdn.Cdn;
import net.dzikoysk.cdn.CdnException;
import net.dzikoysk.cdn.CdnFactory;
import net.dzikoysk.cdn.entity.Exclude;
import net.dzikoysk.cdn.reflect.Visibility;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import panda.std.Result;
import static panda.std.ResultAssertions.assertOk;

class ExcludeJavaTest {

    static class ConfigurationWithMultiDescription {

        @Exclude
        private final static String STATIC_VALUE = "static";

        public String value = "test";

    }

    @Test
    @DisplayName("should render value with package-private visibility and exclude static field")
    void  test() {
        Cdn standardJava = CdnFactory.createStandard()
            .getSettings()
            .withMemberResolver(Visibility.PACKAGE_PRIVATE)
            .build();
        Result<String, CdnException> renderResult = standardJava.render(new ConfigurationWithMultiDescription());
        String render = ResultAssertions.assertOk(renderResult);

        Assertions.assertEquals(
                "value: test",
                render
        );
    }

}
