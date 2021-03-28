/*
 * Copyright (c) 2021 dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dzikoysk.cdn.composers;

import net.dzikoysk.cdn.CdnConstants;
import net.dzikoysk.cdn.CdnDeserializer;
import net.dzikoysk.cdn.CdnFeature;
import net.dzikoysk.cdn.CdnSerializer;
import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.NamedElement;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Composer;
import net.dzikoysk.cdn.serialization.Deserializer;
import net.dzikoysk.cdn.serialization.Serializer;
import net.dzikoysk.cdn.utils.GenericUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ListComposer<T> implements Composer<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(CdnSettings settings, Element<?> source, Type genericType, T defaultValue, boolean entryAsRecord) throws Exception {
        if (source instanceof Entry) {
            Entry entry = (Entry) source;

            if (entry.getUnitValue().trim().endsWith("[]")) {
                return (T) Collections.emptyList();
            }

            throw new UnsupportedOperationException("Cannot deserialize list of " + entry);
        }

        Section section = (Section) source;

        Type collectionType = GenericUtils.getGenericTypes(genericType)[0];
        Class<?> collectionTypeClass = GenericUtils.toClass(collectionType);

        Deserializer<Object> deserializer = CdnDeserializer.getDeserializer(settings, collectionTypeClass, null);
        List<Object> result = new ArrayList<>();

        for (Element<?> element : section.getValue()) {
            for (CdnFeature feature : settings.getFeatures()) {
                element = feature.resolveArrayValue(element);
            }

            result.add(deserializer.deserialize(settings, element, collectionType, null, true));
        }

        return (T) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NamedElement<?> serialize(CdnSettings settings, List<String> description, String key, Type genericType, T entity) throws Exception {
        Collection<Object> collection = (Collection<Object>) entity;

        if (collection.isEmpty()) {
            return new Entry(description, key, "[]");
        }

        Type collectionType = GenericUtils.getGenericTypes(genericType)[0];
        Class<?> collectionTypeClass = GenericUtils.toClass(collectionType);
        Serializer<Object> serializer = CdnSerializer.getSerializer(settings, collectionTypeClass, null);

        Section section = new Section(description, CdnConstants.ARRAY_SEPARATOR, key);

        for (Object element : collection) {
            Element<?> serializedElement = serializer.serialize(settings, Collections.emptyList(), "", collectionType, element);

            for (CdnFeature feature : settings.getFeatures()) {
                serializedElement = feature.visitArrayValue(serializedElement);
            }

            section.append(serializedElement);
        }

        return section;
    }

}
