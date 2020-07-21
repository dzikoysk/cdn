package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.CdnElement;
import net.dzikoysk.cdn.model.CdnRoot;

public final class Cdn {

    private final CdnConfiguration configuration;

    Cdn(CdnConfiguration configuration) {
        this.configuration = configuration;
    }

    public CdnRoot parse(String source) {
        return new CdnReader(this).read(source);
    }

    public <T> T parse(Class<T> scheme, String source) throws Exception {
        return new CdnDeserializer<T>(this).deserialize(scheme, parse(source));
    }

    public String compose(CdnElement<?> element) {
        return new CdnWriter().render(element);
    }

    public CdnConfiguration getConfiguration() {
        return configuration;
    }

    public static CdnConfiguration configure() {
        return new CdnConfiguration();
    }

    public static Cdn defaultInstance() {
        return configure().build();
    }

}
