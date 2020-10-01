package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.SectionLink;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
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

    private void deserialize(Object instance, Section root) throws Exception {
        for (Field field : instance.getClass().getDeclaredFields()) {
            // ignore Groovy properties
            if (!Modifier.isPublic(field.getModifiers()) || field.getName().startsWith("__$") || field.getName().startsWith("$")) {
                continue;
            }

            if (cdn.getConfiguration().getDeserializers().get(field.getType()) == null && !field.isAnnotationPresent(SectionLink.class)) {
                throw new UnsupportedOperationException("Unsupported type, missing deserializer for '" + field.getType().getSimpleName() + " " + field.getName() + "'");
            }

            ConfigurationElement<?> element = root.get(field.getName());

            if (element == null) {
                continue;
            }

            if (element instanceof Section) {
                Section section = (Section) element;

                if (!List.class.isAssignableFrom(field.getType())) {
                    deserialize(field.get(instance), section);
                    continue;
                }

                List<Object> result = new ArrayList<>();
                Class<?> genericType = CdnUtils.getGenericType(field);
                Function<String, Object> deserializer = cdn.getConfiguration().getDeserializers().get(genericType);

                for (String record : root.getList(field.getName())) {
                    result.add(deserializer.apply(record));
                }

                field.set(instance, result);
                continue;
            }

            Entry entry = root.getEntry(field.getName());

            if (entry == null) {
                continue;
            }

            String value = entry.getValue();
            Function<String, Object> deserializer = cdn.getConfiguration().getDeserializers().get(field.getType());

            if (deserializer == null) {
                throw new UnsupportedOperationException(
                        "Missing deserializer for " + field.getType() + " type, value: " + value + ". Available deserializers: " +
                        cdn.getConfiguration().getDeserializers().keySet().toString()
                );
            }

            field.set(instance, deserializer.apply(value));
        }
    }

}
