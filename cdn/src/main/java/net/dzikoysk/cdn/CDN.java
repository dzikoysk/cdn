package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.ConfigurationElement;

public final class CDN {

    private final CdnSettings settings;

    CDN(CdnSettings settings) {
        this.settings = settings;
    }

    public Configuration parse(String source) {
        return new CdnReader(settings).read(source);
    }

    public Configuration parseJson(String source) {
        return parse(new CdnPrettier(source).tryToInsertNewLinesInADumbWay());
    }

    public <T> T parse(Class<T> scheme, String source) throws Exception {
        return new CdnDeserializer<T>(settings).deserialize(scheme, parse(source));
    }

    public String render(ConfigurationElement<?> element) {
        return new CdnWriter(this).render(element);
    }

    public String render(Object entity) {
        return render(new CdnSerializer(settings).serialize(entity));
    }

    public CdnSettings getSettings() {
        return settings;
    }

    public static CdnSettings configure() {
        return new CdnSettings();
    }

    public static CDN defaultInstance() {
        return configure().build();
    }

    public static CDN defaultYamlLikeInstance() {
        return configure()
                .enableYamlLikeFormatting()
                .build();
    }

}
