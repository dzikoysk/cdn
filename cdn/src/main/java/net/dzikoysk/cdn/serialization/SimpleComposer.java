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

package net.dzikoysk.cdn.serialization;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.model.Element;

import java.lang.reflect.AnnotatedType;
import java.util.List;

public final class SimpleComposer<T> implements Composer<T> {

    private final Serializer<T> serializer;
    private final Deserializer<T> deserializer;

    public SimpleComposer(Serializer<T> serializer, Deserializer<T> deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public T deserialize(CdnSettings settings, Element<?> source, AnnotatedType type, T defaultValue, boolean entryAsRecord) throws Exception {
        return deserializer.deserialize(settings, source, type, defaultValue, entryAsRecord);
    }

    @Override
    public Element<?> serialize(CdnSettings settings, List<String> description, String key, AnnotatedType type, T entity) throws Exception {
        return serializer.serialize(settings, description, key, type, entity);
    }

}
