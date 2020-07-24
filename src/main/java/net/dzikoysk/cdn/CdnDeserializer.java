package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Section;

import java.lang.reflect.Field;
import java.util.function.Function;

final class CdnDeserializer<T> {

    private final CDN cdn;

    public CdnDeserializer(CDN cdn) {
        this.cdn = cdn;
    }

    protected T deserialize(Class<T> scheme, Configuration content) throws Exception {
        T instance = scheme.getConstructor().newInstance();
        deserialize(instance, content);
        return instance;
    }

    private void deserialize(Object instance, Section section) throws Exception {
        for (Field field : instance.getClass().getDeclaredFields()) {
            ConfigurationElement<?> element = section.get(field.getName());

            if (element == null) {
                continue;
            }

            if (element instanceof Section) {
                deserialize(field.get(instance), (Section) element);
                continue;
            }

            Entry entry = section.getEntry(field.getName());

            if (entry == null) {
                continue;
            }

            String value = entry.getValue();
            Function<String, Object> deserializer = cdn.getConfiguration().getDeserializers().get(field.getType());

            if (deserializer == null) {
                throw new UnsupportedOperationException("Missing deserializer for " + value.getClass() + " type");
            }

            field.set(instance, deserializer.apply(value));
        }
    }

}
