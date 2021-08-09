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

package net.dzikoysk.cdn;

import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.DeserializationHandler;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Deserializer;
import panda.std.Option;
import panda.utilities.ObjectUtils;

import java.lang.reflect.Field;

public final class CdnDeserializer<T> {

    private final CdnSettings settings;

    public CdnDeserializer(CdnSettings settings) {
        this.settings = settings;
    }

    public T deserialize(Class<T> scheme, Section content) throws Exception {
        T instance = scheme.getConstructor().newInstance();
        deserialize(instance, content);

        if (instance instanceof DeserializationHandler) {
            DeserializationHandler<T> handler = ObjectUtils.cast(instance);
            instance = handler.handle(ObjectUtils.cast(instance));
        }

        return instance;
    }

    private Object deserialize(Object instance, Section root) throws Exception {
        for (Field field : instance.getClass().getFields()) {
            if (CdnUtils.isIgnored(field)) {
                continue;
            }

            Option<Element<?>> elementValue = root.get(field.getName());

            if (elementValue.isEmpty()) {
                continue;
            }

            Element<?> element = elementValue.get();
            Object defaultValue = field.get(instance);

            if (field.isAnnotationPresent(Contextual.class)) {
                deserialize(defaultValue, (Section) element);
                continue;
            }

            deserialize(settings, instance, field, defaultValue, element);
        }

        return instance;
    }

    private Object deserialize(CdnSettings settings, Object instance, Field field, Object defaultValue, Element<?> element) throws Exception {
        Deserializer<Object> deserializer = CdnUtils.findComposer(settings, field.getType(), field.getAnnotatedType(), field);
        Object value = deserializer.deserialize(settings, element, field.getAnnotatedType(), defaultValue, false);
        field.set(instance, value);
        return value;
    }

}
