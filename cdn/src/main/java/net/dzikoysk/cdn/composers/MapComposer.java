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
import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.NamedElement;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.model.Unit;
import net.dzikoysk.cdn.serialization.Composer;
import net.dzikoysk.cdn.serialization.Deserializer;
import net.dzikoysk.cdn.serialization.Serializer;
import net.dzikoysk.cdn.utils.GenericUtils;
import org.panda_lang.utilities.commons.ObjectUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MapComposer<T> implements Composer<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(CdnSettings settings, Element<?> source, Type type, T defaultValue, boolean entryAsRecord) throws Exception {
        if (source instanceof Entry) {
            Entry entry = (Entry) source;
            // String value = entryAsRecord ? entry.get() : entry.getValue();
            String value = entry.getUnitValue();

            if (value.equals("[]") /* || entry.getRecord().equals(value) ?what the f is this even doing here? */) {
                return (T) Collections.emptyMap();
            }

            throw new UnsupportedOperationException("Cannot deserialize list of " + value);
        }

        Section section = (Section) source;
        Type[] collectionTypes = GenericUtils.getGenericTypes(type);
        Class<?>[] collectionTypesClasses = GenericUtils.getGenericClasses(type);

        Deserializer<?> keySerializer = CdnUtils.findComposer(settings, collectionTypesClasses[0], null);
        Deserializer<?> valueSerializer = CdnUtils.findComposer(settings, collectionTypesClasses[1], null);

        Map<Object, Object> result = new LinkedHashMap<>();

        for (Element<?> element : section.getValue()) {
            if (element instanceof Entry) {
                Entry entry = (Entry) element;

                result.put(
                        keySerializer.deserialize(settings, new Unit(entry.getName()), collectionTypes[0], null, entryAsRecord),
                        valueSerializer.deserialize(settings, entry.getValue(), collectionTypes[1], null, entryAsRecord)
                );
            }
            else if (element instanceof Section) {
                Section subSection = (Section) element;

                result.put(
                        keySerializer.deserialize(settings, new Unit(subSection.getName()), collectionTypes[0], null, entryAsRecord),
                        valueSerializer.deserialize(settings, subSection, collectionTypes[1], null, entryAsRecord)
                );
            }
            else throw new UnsupportedOperationException("Unsupported element in map: " + element);
        }

        return (T) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NamedElement<? extends Object> serialize(CdnSettings settings, List<String> description, String key, Type genericType, T entity) throws Exception {
        Map<Object, Object> map = (Map<Object, Object>) entity;

        if (map.isEmpty()) {
            return new Entry(description, key, "[]");
        }

        Type[] collectionTypes = GenericUtils.getGenericTypes(genericType);
        Class<?>[] collectionTypesClasses = GenericUtils.getGenericClasses(genericType);

        Serializer<?> keySerializer = CdnUtils.findComposer(settings, collectionTypesClasses[0], null);
        Serializer<?> valueSerializer = CdnUtils.findComposer(settings, collectionTypesClasses[1], null);

        Section section = new Section(description, CdnConstants.OBJECT_SEPARATOR, key);

        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Element<?> keyElement = keySerializer.serialize(settings, Collections.emptyList(), "", collectionTypes[0], ObjectUtils.cast(entry.getKey()));
            Element<?> valueElement = valueSerializer.serialize(settings, Collections.emptyList(), keyElement.getValue().toString(), collectionTypes[1], ObjectUtils.cast(entry.getValue()));

            if (valueElement instanceof Section) {
                section.append(valueElement);
            }
            else if (valueElement instanceof Entry) {
                section.append(valueElement);
            }
            else throw new UnsupportedOperationException("Unsupported value element in map: " + valueElement);
        }

        return section;
    }

}
