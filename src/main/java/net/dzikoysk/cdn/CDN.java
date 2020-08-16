package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.ConfigurationElement;

public final class CDN {

    private final CdnConfiguration configuration;

    CDN(CdnConfiguration configuration) {
        this.configuration = configuration;
    }

    public Configuration parse(String source) {
        return new CdnReader(this).read(source);
    }

    public Configuration parseJson(String source) {
        return parse(new CdnPrettier(source).tryToInsertNewLinesInADumbWay());
    }

    public <T> T parse(Class<T> scheme, String source) throws Exception {
        return new CdnDeserializer<T>(this).deserialize(scheme, parse(source));
    }

    public String compose(ConfigurationElement<?> element) {
        return new CdnWriter(this).render(element);
    }

    public String compose(Object entity) {
        return compose(new CdnSerializer(this).serialize(entity));
    }

    public CdnConfiguration getConfiguration() {
        return configuration;
    }

    public static CdnConfiguration configure() {
        return new CdnConfiguration();
    }

    public static CDN defaultInstance() {
        return configure().build();
    }

}
