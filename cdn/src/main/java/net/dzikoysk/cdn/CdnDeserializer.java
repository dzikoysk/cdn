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

import net.dzikoysk.cdn.entity.CustomComposer;
import net.dzikoysk.cdn.entity.DeserializationHandler;
import net.dzikoysk.cdn.entity.SectionLink;
import net.dzikoysk.cdn.entity.SectionValue;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Deserializer;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.function.Option;

import java.lang.reflect.Field;

public final class CdnDeserializer<T> {

    private final CdnSettings settings;

    CdnDeserializer(CdnSettings settings) {
        this.settings = settings;
    }

    protected T deserialize(Class<T> scheme, Section content) throws Exception {
        T instance = scheme.getConstructor().newInstance();
        deserialize(instance, content);

        if (instance instanceof DeserializationHandler) {
            DeserializationHandler<T> handler = ObjectUtils.cast(instance);
            instance = handler.handle(ObjectUtils.cast(instance));
        }

        return instance;
    }

    private Object deserialize(Object instance, Section root) throws Exception {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (CdnUtils.isIgnored(field)) {
                continue;
            }

            if (settings.getDeserializers().get(field.getType()) == null && !field.isAnnotationPresent(SectionLink.class) && !field.isAnnotationPresent(CustomComposer.class)) {
                throw new UnsupportedOperationException("Unsupported type, missing deserializer for '" + field.getType().getSimpleName() + " " + field.getName() + "'");
            }

            Option<Element<?>> elementValue = root.get(field.getName());

            if (elementValue.isEmpty()) {
                continue;
            }

            Element<?> element = elementValue.get();
            Object defaultValue = field.get(instance);

            if (field.isAnnotationPresent(SectionLink.class)) {
                deserialize(defaultValue, (Section) element);
                continue;
            }

            deserialize(settings, instance, field, defaultValue, element);
        }

        return instance;
    }

    private Object deserialize(CdnSettings settings, Object instance, Field field, Object defaultValue, Element<?> element) throws Exception {
        Deserializer<Object> deserializer = getDeserializer(settings, field.getType(), field);
        Object value = deserializer.deserialize(settings, element, field.getGenericType(), defaultValue, false);
        field.set(instance, value);
        return value;
    }

    public static Deserializer<Object> getDeserializer(CdnSettings settings, Class<?> type, @Nullable Field field) throws Exception {
        Deserializer<Object> deserializer;

        if (field != null && field.isAnnotationPresent(CustomComposer.class)) {
            CustomComposer customComposer = field.getAnnotation(CustomComposer.class);
            deserializer = ObjectUtils.cast(customComposer.value().getConstructor().newInstance());
        }
        else {
            deserializer = settings.getDeserializers().get(type);
        }

        if (type.isAnnotationPresent(SectionValue.class)) {
            CdnDeserializer<Object> sectionDeserializer = new CdnDeserializer<>(settings);

            return (s, source, genericType, defaultValue, entryAsRecord) -> {
                //noinspection unchecked
                return sectionDeserializer.deserialize((Class<Object>) type, (Section) source);
            };
        }

        if (deserializer == null) {
            throw new UnsupportedOperationException("Missing deserializer for '" + type + "' type. Available deserializers: " + settings.getDeserializers().keySet().toString());
        }

        return deserializer;
    }

}
