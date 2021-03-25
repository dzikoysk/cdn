package net.dzikoysk.cdn.composers;

import net.dzikoysk.cdn.CdnConstants;
import net.dzikoysk.cdn.CdnDeserializer;
import net.dzikoysk.cdn.CdnSerializer;
import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.ConfigurationElement;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Composer;
import net.dzikoysk.cdn.serialization.Deserializer;
import net.dzikoysk.cdn.serialization.Serializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ListComposer<T> implements Composer<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(CdnSettings settings, ConfigurationElement<?> source, Type genericType, T defaultValue, boolean entryAsRecord) throws Exception {
        if (source instanceof Entry) {
            Entry entry = (Entry) source;

            if (entry.getRecord().trim().endsWith("[]")) {
                return (T) Collections.emptyList();
            }

            throw new UnsupportedOperationException("Cannot deserialize list of " + entry);
        }

        Section section = (Section) source;

        Type collectionType = CdnUtils.getGenericTypes(genericType)[0];
        Class<?> collectionTypeClass = CdnUtils.toClass(collectionType);

        Deserializer<Object> deserializer = CdnDeserializer.getDeserializer(settings, collectionTypeClass, null);
        List<Object> result = new ArrayList<>();

        for (ConfigurationElement<?> element : section.getValue()) {
            if (settings.isYamlLikeEnabled()) {
                if (element instanceof Entry) {
                    element = Entry.of(((Entry) element).getRecord().replaceFirst(CdnConstants.LIST, "").trim(), element.getDescription());
                }
                /*
                else if (element instanceof Section) {
                    Section sectionElement = (Section) element;
                    element = new Section(sectionElement.getOperators(), CdnConstants.LIST + " " + element.getName(), sectionElement.getDescription(), sectionElement.getValue());
                }
                else throw new UnsupportedOperationException("Unsupported list component: " + element);
                 */
            }

            result.add(deserializer.deserialize(settings, element, collectionType, null, true));
        }

        return (T) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConfigurationElement<?> serialize(CdnSettings settings, List<String> description, String key, Type genericType, T entity) throws Exception {
        Collection<Object> collection = (Collection<Object>) entity;

        if (collection.isEmpty()) {
            return Entry.ofPair(key, "[]", description);
        }

        Type collectionType = CdnUtils.getGenericTypes(genericType)[0];
        Class<?> collectionTypeClass = CdnUtils.toClass(collectionType);
        Serializer<Object> serializer = CdnSerializer.getSerializer(settings, collectionTypeClass, null);

        Section section = new Section(CdnConstants.ARRAY_SEPARATOR, key, description);

        for (Object element : collection) {
            ConfigurationElement<?> configurationElement = serializer.serialize(settings, Collections.emptyList(), "", collectionType, element);

            if (settings.isYamlLikeEnabled()) {
                if (configurationElement instanceof Entry) {
                    configurationElement = Entry.of(CdnConstants.LIST + " " + ((Entry) configurationElement).getRecord().trim(), configurationElement.getDescription());
                }
                /*
                else if (configurationElement instanceof Section) {
                    Section sectionElement = (Section) configurationElement;
                    configurationElement = new Section(sectionElement.getOperators(), CdnConstants.LIST + " " + configurationElement.getName(), sectionElement.getDescription(), sectionElement.getValue());
                }
                else throw new UnsupportedOperationException("Unsupported list component: " + configurationElement);
                 */
            }

            section.append(configurationElement);
        }

        return section;
    }

}
