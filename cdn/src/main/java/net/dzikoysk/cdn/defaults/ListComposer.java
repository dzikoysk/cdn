package net.dzikoysk.cdn.defaults;

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
    public T deserialize(CdnSettings settings, ConfigurationElement<?> source, Type genericType, T defaultValue, boolean listEntry) throws Exception {
        if (source instanceof Entry) {
            Entry entry = (Entry) source;
            String value = listEntry ? entry.getRecord() : entry.getValue();

            if (value.equals("[]")) {
                return (T) Collections.emptyList();
            }

            throw new UnsupportedOperationException("Cannot deserialize list of " + value);
        }

        Section section = (Section) source;

        Type collectionType = CdnUtils.getGenericTypes(genericType)[0];
        Class<?> collectionTypeClass = CdnUtils.toClass(collectionType);

        Deserializer<Object> deserializer = CdnDeserializer.getDeserializer(settings, collectionTypeClass, null);
        List<Object> result = new ArrayList<>();

        for (ConfigurationElement<?> element : section.getValue()) {
            result.add(deserializer.deserialize(settings, element, collectionType, null, true));
        }

        return (T) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConfigurationElement<?> serialize(CdnSettings settings, List<String> description, String key, Type genericType, T entity) throws Exception {
        Section section = new Section(CdnConstants.ARRAY_SEPARATOR, key, description);

        Collection<Object> collection = (Collection<Object>) entity;
        Type collectionType = CdnUtils.getGenericTypes(genericType)[0];
        Class<?> collectionTypeClass = CdnUtils.toClass(collectionType);
        Serializer<Object> serializer = CdnSerializer.getSerializer(settings, collectionTypeClass, null);

        for (Object element : collection) {
            ConfigurationElement<?> configurationElement = serializer.serialize(settings, Collections.emptyList(), "", collectionType, element);

            if (configurationElement instanceof Entry) {
                if (settings.isYamlLikeEnabled()) {
                    configurationElement = Entry.of(CdnConstants.LIST + " " + ((Entry) configurationElement).getRecord(), configurationElement.getDescription());
                }
            }
            else {
                throw new UnsupportedOperationException("#todo @makub");
            }

            section.append(configurationElement);
        }

        return section;
    }

}
