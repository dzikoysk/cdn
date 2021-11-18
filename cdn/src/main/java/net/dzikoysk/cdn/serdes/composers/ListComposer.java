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

package net.dzikoysk.cdn.serdes.composers;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Entry;
import net.dzikoysk.cdn.model.NamedElement;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import net.dzikoysk.cdn.serdes.Composer;
import net.dzikoysk.cdn.serdes.Deserializer;
import net.dzikoysk.cdn.serdes.Serializer;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ListComposer<T> implements Composer<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(CdnSettings settings, Element<?> source, AnnotatedType type, T defaultValue, boolean entryAsRecord) throws ReflectiveOperationException {
        if (source instanceof Entry) {
            Entry entry = (Entry) source;

            if (entry.getUnitValue().trim().endsWith("[]")) {
                return (T) Collections.emptyList();
            }

            throw new UnsupportedOperationException("Cannot deserialize list of " + entry);
        }

        AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) type;
        AnnotatedType collectionType = annotatedParameterizedType.getAnnotatedActualTypeArguments()[0];
        Deserializer<Object> deserializer = CdnUtils.findComposer(settings, collectionType, null);

        List<Object> result = new ArrayList<>();
        Section section = (Section) source;

        for (Element<?> element : section.getValue()) {
            element = settings.getModules().resolveArrayValue(element);
            result.add(deserializer.deserialize(settings, element, collectionType, null, true));
        }

        return (T) result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public NamedElement<?> serialize(CdnSettings settings, List<String> description, String key, AnnotatedType type, T entity) throws ReflectiveOperationException {
        Collection<Object> collection = (Collection<Object>) entity;

        if (collection.isEmpty()) {
            return new Entry(description, key, "[]");
        }

        AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) type;
        AnnotatedType collectionType = annotatedParameterizedType.getAnnotatedActualTypeArguments()[0];
        Serializer<Object> serializer = CdnUtils.findComposer(settings, collectionType, null);

        Section section = new Section(description, StandardOperators.ARRAY_SEPARATOR, key);

        for (Object element : collection) {
            Element<?> serializedElement = serializer.serialize(settings, Collections.emptyList(), "", collectionType, element);
            serializedElement = settings.getModules().visitArrayValue(serializedElement);
            section.append(serializedElement);
        }

        return section;
    }

}
