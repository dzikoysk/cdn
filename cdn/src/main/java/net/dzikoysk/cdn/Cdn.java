package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.NamedElement;

public final class Cdn {

    private final CdnSettings settings;

    Cdn(CdnSettings settings) {
        this.settings = settings;
    }

    public Configuration load(String source) {
        return new CdnReader(settings).read(source);
    }

    public <T> T load(String source, Class<T> scheme) throws Exception {
        return new CdnDeserializer<T>(settings).deserialize(scheme, load(source));
    }

    public String render(NamedElement<?> element) {
        return new CdnWriter(settings).render(element);
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

}
