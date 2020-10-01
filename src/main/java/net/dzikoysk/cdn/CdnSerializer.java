package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.CustomComposer;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.entity.SectionLink;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Serializer;
import org.panda_lang.utilities.commons.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class CdnSerializer {

    private final CDN cdn;

    CdnSerializer(CDN cdn) {
        this.cdn = cdn;
    }

    public Configuration serialize(Object entity) {
        Configuration root = new Configuration();

        try {
            serialize(root, entity);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot access serialize member", e);
        }

        return root;
    }

    @SuppressWarnings("unchecked")
    private void serialize(Section root, Object entity) throws Exception {
        Class<?> scheme = entity.getClass();

        for (Field field : scheme.getDeclaredFields()) {
            if (!Modifier.isPublic(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            List<String> description = Arrays.stream(field.getAnnotationsByType(Description.class))
                    .flatMap(annotation -> Arrays.stream(annotation.value()))
                    .collect(Collectors.toList());

            if (field.isAnnotationPresent(SectionLink.class)) {
                Section section = new Section(CdnConstants.OBJECT_SEPARATOR, field.getName(), description);
                root.append(section);
                serialize(section, field.get(entity));
                continue;
            }

            Object value = field.get(entity);

            if (List.class.isAssignableFrom(field.getType())) {
                Section section = root.append(new Section(CdnConstants.ARRAY_SEPARATOR, field.getName(), description));
                Collection<Object> collection = (Collection<Object>) value;
                Class<?> collectionType = CdnUtils.getGenericType(field);
                Serializer<Object> serializer = cdn.getConfiguration().getSerializers().get(collectionType);

                for (Object element : collection) {
                    ConfigurationElement<?> configurationElement = serializer.serialize("", element, Collections.emptyList());

                    if (configurationElement instanceof Entry) {
                        if (cdn.getConfiguration().isIndentationEnabled()) {
                            configurationElement = Entry.of(CdnConstants.LIST + " " + ((Entry) configurationElement).getRecord(), configurationElement.getDescription());
                        }
                    }
                    else {
                        throw new UnsupportedOperationException("#todo @makub");
                    }

                    section.append(configurationElement);
                }

                continue;
            }

            Serializer<Object> serializer = cdn.getConfiguration().getSerializers().get(value.getClass());

            if (serializer == null && field.isAnnotationPresent(CustomComposer.class)) {
                CustomComposer customComposer = field.getAnnotation(CustomComposer.class);
                serializer = (Serializer<Object>) customComposer.value().getConstructor().newInstance();
            }

            if (serializer == null) {
                throw new UnsupportedOperationException("Cannot serialize " + value.getClass() + " - missing serializer");
            }

            root.append(serializer.serialize(field.getName(), value, description));
        }
    }

    private Serializer<Object> getSerializer(Class<?> type, Field field) throws Exception {
        Serializer<Object> serializer;

        if (field.isAnnotationPresent(CustomComposer.class)) {
            CustomComposer customComposer = field.getAnnotation(CustomComposer.class);
            serializer = ObjectUtils.cast(customComposer.value().getConstructor().newInstance());
        }
        else {
             serializer = cdn.getConfiguration().getSerializers().get(type);
        }

        if (serializer == null) {
            throw new UnsupportedOperationException("Cannot serialize field '" + field.getType().getSimpleName() + " " + field.getName() + "' - missing serializer");
        }

        return serializer;
    }

}
