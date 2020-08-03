package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.entity.SectionLink;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
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
                Section section = new Section(field.getName(), description);
                root.append(section);
                serialize(section, field.get(entity));
                continue;
            }

            Object value = field.get(entity);

            if (Collection.class.isAssignableFrom(field.getType())) {
                Section section = root.append(new Section(field.getName(), description));
                Collection<Object> collection = (Collection<Object>) value;
                Class<?> collectionType = CdnUtils.getGenericType(field);
                Function<Object, String> serializer = cdn.getConfiguration().getSerializers().get(collectionType);

                for (Object element : collection) {
                    String collectionElement = serializer.apply(element);

                    if (cdn.getConfiguration().isIndentationEnabled()) {
                        collectionElement = CdnConstants.LIST + " " + collectionElement;
                    }

                    section.append(Entry.of(collectionElement, Collections.emptyList()));
                }

                continue;
            }

            Function<Object, String> serializer = cdn.getConfiguration().getSerializers().get(value.getClass());

            if (serializer == null) {
                throw new UnsupportedOperationException("Cannot serialize " + value.getClass());
            }

            root.append(Entry.of(field.getName() + ": " +  serializer.apply(value), description));
        }
    }

}
