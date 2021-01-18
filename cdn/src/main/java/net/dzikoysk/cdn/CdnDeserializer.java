package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.CustomComposer;
import net.dzikoysk.cdn.entity.DeserializationHandler;
import net.dzikoysk.cdn.entity.SectionLink;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Deserializer;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.function.Option;

import java.lang.reflect.Field;
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

        if (instance instanceof DeserializationHandler) {
            DeserializationHandler<T> handler = ObjectUtils.cast(instance);
            instance = handler.handle(ObjectUtils.cast(instance));
        }

        return instance;
    }

    private void deserialize(Object instance, Section root) throws Exception {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (CdnUtils.isIgnored(field)) {
                continue;
            }

            if (cdn.getSettings().getDeserializers().get(field.getType()) == null && !field.isAnnotationPresent(SectionLink.class) && !field.isAnnotationPresent(CustomComposer.class)) {
                throw new UnsupportedOperationException("Unsupported type, missing deserializer for '" + field.getType().getSimpleName() + " " + field.getName() + "'");
            }

            Option<ConfigurationElement<?>> elementValue = root.get(field.getName());

            if (elementValue.isEmpty()) {
                continue;
            }

            ConfigurationElement<?> element = elementValue.get();
            Object defaultValue = field.get(instance);

            if (field.isAnnotationPresent(CustomComposer.class)) {
                deserialize(instance, field, defaultValue, element);
            }
            else if (element instanceof Section) {
                Section section = (Section) element;

                if (!List.class.isAssignableFrom(field.getType())) {
                    deserialize(field.get(instance), section);
                    continue;
                }

                List<Object> result = new ArrayList<>();
                Class<?> genericType = CdnUtils.getGenericType(field);
                Deserializer<Object> deserializer = getDeserializer(genericType, field);

                root.getList(field.getName()).peek(list -> {
                    list.forEach(record -> result.add(deserializer.deserialize(Entry.of(record, Collections.emptyList()), null, true)));
                });

                field.set(instance, result);
            }
            else if (element instanceof Entry) {
                Option<Entry> entry = root.getEntry(field.getName());

                if (entry.isDefined()) {
                    deserialize(instance, field, defaultValue, entry.get());
                }
            }
            else throw new UnsupportedOperationException("Unknown ConfigurationElement: " + element);
        }
    }

    private void deserialize(Object instance, Field field, Object defaultValue, ConfigurationElement<?> element) throws Exception {
        Deserializer<Object> deserializer = getDeserializer(field.getType(), field);
        Object value = deserializer.deserialize(element, defaultValue, false);
        field.set(instance, value);
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
