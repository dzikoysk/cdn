package net.dzikoysk.cdn;

import net.dzikoysk.cdn.model.CdnElement;
import net.dzikoysk.cdn.model.CdnEntry;
import net.dzikoysk.cdn.model.CdnRoot;
import net.dzikoysk.cdn.model.CdnSection;

import java.lang.reflect.Field;
import java.util.function.Function;

final class CdnDeserializer<T> {

    private final Cdn cdn;

    public CdnDeserializer(Cdn cdn) {
        this.cdn = cdn;
    }

    protected T deserialize(Class<T> scheme, CdnRoot content) throws Exception {
        T instance = scheme.getConstructor().newInstance();
        deserialize(instance, content);
        return instance;
    }

    private void deserialize(Object instance, CdnSection section) throws Exception {
        for (Field field : instance.getClass().getDeclaredFields()) {
            CdnElement<?> element = section.get(field.getName());

            if (element == null) {
                continue;
            }

            if (element instanceof CdnSection) {
                deserialize(field.get(instance), (CdnSection) element);
                continue;
            }

            CdnEntry entry = section.getEntry(field.getName());

            if (entry == null) {
                continue;
            }

            String value = entry.getValue();
            Function<String, Object> deserializer = cdn.getConfiguration().getDeserializers().get(value.getClass());

            if (deserializer == null) {
                throw new UnsupportedOperationException("Missing deserializer for " + value.getClass() + " type");
            }

            field.set(instance, deserializer.apply(value));
        }
    }

}
