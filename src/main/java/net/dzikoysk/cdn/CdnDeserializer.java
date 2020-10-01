package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.CustomComposer;
import net.dzikoysk.cdn.entity.SectionLink;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Deserializer;
import org.panda_lang.utilities.commons.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

            if (cdn.getSettings().getDeserializers().get(field.getType()) == null && !field.isAnnotationPresent(SectionLink.class) && !field.isAnnotationPresent(CustomComposer.class)) {
                throw new UnsupportedOperationException("Unsupported type, missing deserializer for '" + field.getType().getSimpleName() + " " + field.getName() + "'");
            }

            ConfigurationElement<?> element = root.get(field.getName());

            if (element == null) {
                continue;
            }

            Object defaultValue = field.get(instance);

            if (field.isAnnotationPresent(CustomComposer.class)) {
                Object value = getDeserializer(field.getType(), field).deserialize(element, defaultValue);
                field.set(instance, value);
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
                Deserializer<Object> deserializer = getDeserializer(genericType, field);

                for (String record : root.getList(field.getName())) {
                    result.add(deserializer.deserialize(Entry.of(record, Collections.emptyList()), null));
                }

                field.set(instance, result);
                continue;
            }

            Entry entry = root.getEntry(field.getName());

            if (entry == null) {
                continue;
            }

            Deserializer<Object> deserializer = getDeserializer(field.getType(), field);
            field.set(instance, deserializer.deserialize(entry, defaultValue));
        }
    }

    private Deserializer<Object> getDeserializer(Class<?> type, Field field) throws Exception {
        Deserializer<Object> deserializer;

        if (field.isAnnotationPresent(CustomComposer.class)) {
            CustomComposer customComposer = field.getAnnotation(CustomComposer.class);
            deserializer = ObjectUtils.cast(customComposer.value().getConstructor().newInstance());
        }
        else {
            deserializer = cdn.getSettings().getDeserializers().get(type);
        }

        if (deserializer == null) {
            throw new UnsupportedOperationException(
                    "Missing deserializer for '" + field.getType().getSimpleName() + " " + field.getName() + "' type. Available deserializers: " +
                            cdn.getSettings().getDeserializers().keySet().toString()
            );
        }

        return deserializer;
    }

}
