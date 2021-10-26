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

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.serialization.Composer;
import net.dzikoysk.cdn.serialization.Deserializer;
import net.dzikoysk.cdn.serialization.Serializer;
import panda.std.reactive.Reference;
import panda.std.reactive.ReferenceUtils;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.util.List;

@SuppressWarnings("unchecked")
public final class ReferenceComposer<T> implements Composer<T> {

    @Override
    public T deserialize(CdnSettings settings, Element<?> source, AnnotatedType type, T defaultValue, boolean entryAsRecord) throws ReflectiveOperationException {
        AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) type;
        AnnotatedType referenceType = annotatedParameterizedType.getAnnotatedActualTypeArguments()[0];
        Deserializer<Object> deserializer = CdnUtils.findComposer(settings, referenceType, null);
        Object value = deserializer.deserialize(settings, source, referenceType, defaultValue, entryAsRecord);
        Reference<Object> reference = (Reference<Object>) defaultValue;
        ReferenceUtils.setValue(reference, value);
        return (T) MEMBER_ALREADY_PROCESSED;
    }

    @Override
    public Element<?> serialize(CdnSettings settings, List<String> description, String key, AnnotatedType type, T entity) throws ReflectiveOperationException {
        Reference<Object> reference = (Reference<Object>) entity;
        AnnotatedParameterizedType annotatedParameterizedType = (AnnotatedParameterizedType) type;
        AnnotatedType[] referenceTypes = annotatedParameterizedType.getAnnotatedActualTypeArguments();
        AnnotatedType referenceType = referenceTypes[0];
        Serializer<Object> serializer = CdnUtils.findComposer(settings, referenceType, null);
        return serializer.serialize(settings, description, key, referenceType, reference.get());
    }

}
