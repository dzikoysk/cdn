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
import org.panda_lang.utilities.commons.ObjectUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MapComposer<T> implements Composer<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(CdnSettings settings, ConfigurationElement<?> source, Type genericType, T defaultValue, boolean entryAsRecord) throws Exception {
        if (source instanceof Entry) {
            Entry entry = (Entry) source;
            String value = entryAsRecord ? entry.getRecord() : entry.getValue();

            if (value.equals("[]") || entry.getRecord().equals(value)) {
                return (T) Collections.emptyMap();
            }

            throw new UnsupportedOperationException("Cannot deserialize list of " + value);
        }

        Section section = (Section) source;
        Type[] collectionTypes = CdnUtils.getGenericTypes(genericType);
        Class<?>[] collectionTypesClasses = CdnUtils.getGenericClasses(genericType);

        Deserializer<?> keySerializer = CdnDeserializer.getDeserializer(settings, collectionTypesClasses[0], null);
        Deserializer<?> valueSerializer = CdnDeserializer.getDeserializer(settings, collectionTypesClasses[1], null);

        Map<Object, Object> result = new LinkedHashMap<>();

        for (ConfigurationElement<?> element : section.getValue()) {
            if (element instanceof Entry) {
                Entry entry = (Entry) element;

                result.put(
                        keySerializer.deserialize(settings, Entry.of(entry.getName(), entry.getDescription()), collectionTypes[0], null, entryAsRecord),
                        valueSerializer.deserialize(settings, Entry.of(entry.getValue(), Collections.emptyList()), collectionTypes[1], null, entryAsRecord)
                );
            }
            else if (element instanceof Section) {
                Section subSection = (Section) element;

                result.put(
                        keySerializer.deserialize(settings, Entry.of(subSection.getName(), subSection.getDescription()), collectionTypes[0], null, entryAsRecord),
                        valueSerializer.deserialize(settings, subSection, collectionTypes[1], null, entryAsRecord)
                );
            }
            else throw new UnsupportedOperationException("Unsupported section map");
        }

        return (T) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ConfigurationElement<? extends Object> serialize(CdnSettings settings, List<String> description, String key, Type genericType, T entity) throws Exception {
        Map<Object, Object> map = (Map<Object, Object>) entity;

        if (map.isEmpty()) {
            return Entry.ofPair(key, "[]", description);
        }

        Type[] collectionTypes = CdnUtils.getGenericTypes(genericType);
        Class<?>[] collectionTypesClasses = CdnUtils.getGenericClasses(genericType);

        Serializer<?> keySerializer = CdnSerializer.getSerializer(settings, collectionTypesClasses[0], null);
        Serializer<?> valueSerializer = CdnSerializer.getSerializer(settings, collectionTypesClasses[1], null);

        Section section = new Section(CdnConstants.OBJECT_SEPARATOR, key, description);

        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            ConfigurationElement<?> keyElement = keySerializer.serialize(settings, Collections.emptyList(), "", collectionTypes[0], ObjectUtils.cast(entry.getKey()));
            ConfigurationElement<?> valueElement = valueSerializer.serialize(settings, Collections.emptyList(), keyElement.getValue().toString(), collectionTypes[1], ObjectUtils.cast(entry.getValue()));

            if (valueElement instanceof Section) {
                section.append(valueElement);
            }
            else if (valueElement instanceof Entry) {
                section.append(Entry.ofPair(keyElement.getValue().toString(), valueElement.getValue(), keyElement.getDescription()));
            }
            else throw new UnsupportedOperationException("Unsupported section map");
        }

        return section;
    }

}
