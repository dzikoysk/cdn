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

import net.dzikoysk.cdn.annotation.AnnotatedMember;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.DeserializationHandler;
import net.dzikoysk.cdn.model.Element;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Deserializer;
import panda.std.Option;
import panda.utilities.ObjectUtils;
import panda.utilities.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class CdnDeserializer<T> {

    private final CdnSettings settings;

    public CdnDeserializer(CdnSettings settings) {
        this.settings = settings;
    }

    public T deserialize(Section source, Class<T> template) throws ReflectiveOperationException {
        return deserialize(source, template.getConstructor().newInstance());
    }

    public T deserialize(Section source, T instance) throws ReflectiveOperationException {
        deserializeToSection(source, instance);

        if (instance instanceof DeserializationHandler) {
            DeserializationHandler<T> handler = ObjectUtils.cast(instance);
            instance = handler.handle(ObjectUtils.cast(instance));
        }

        return instance;
    }

    private Object deserializeToSection(Section source, Object instance) throws ReflectiveOperationException {
        for (Field field : instance.getClass().getFields()) {
            deserializeField(source, instance, field);
        }

        for (String propertyName : settings.getAnnotationResolver().getProperties(instance.getClass())) {
            deserializeProperty(source, instance, propertyName);
        }

        return instance;
    }

    private void deserializeField(Section source, Object instance, Field field) throws ReflectiveOperationException {
        if (!CdnUtils.isIgnored(field)) {
            deserializeMember(source, settings.getAnnotationResolver().fromField(instance, field));
        }
    }

    private void deserializeProperty(Section source, Object instance, String propertyName) throws ReflectiveOperationException {
        try {
            Method getter = instance.getClass().getMethod("get" + StringUtils.capitalize(propertyName));

            if (CdnUtils.isIgnored(getter)) {
                return;
            }

            deserializeMember(source, settings.getAnnotationResolver().fromProperty(instance, propertyName));
        }
        catch (NoSuchMethodException ignored) {
            // cannot set this property, ignore
        }
    }

    private void deserializeMember(Section source, AnnotatedMember member) throws ReflectiveOperationException {
        Option<Element<?>> elementValue = source.get(member.getName());

        if (elementValue.isEmpty()) {
            return;
        }

        Element<?> element = elementValue.get();
        Object defaultValue = member.getValue();

        if (member.isAnnotationPresent(Contextual.class)) {
            deserializeToSection((Section) element, defaultValue);
            return;
        }

        Deserializer<Object> deserializer = CdnUtils.findComposer(settings, member.getType(), member.getAnnotatedType(), member);
        Object value = deserializer.deserialize(settings, element, member.getAnnotatedType(), defaultValue, false);
        member.setValue(value);
    }

}
