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
import net.dzikoysk.cdn.shared.AnnotatedMember;
import net.dzikoysk.cdn.shared.AnnotatedMember.FieldMember;
import net.dzikoysk.cdn.shared.AnnotatedMember.MethodMember;
import panda.std.Option;
import panda.utilities.ObjectUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class CdnDeserializer<T> {

    private final CdnSettings settings;

    public CdnDeserializer(CdnSettings settings) {
        this.settings = settings;
    }

    public T deserialize(Class<T> scheme, Section content) throws ReflectiveOperationException {
        return deserialize(scheme.getConstructor().newInstance(), content);
    }

    public T deserialize(T instance, Section content) throws ReflectiveOperationException {
        deserializeToSection(instance, content);

        if (instance instanceof DeserializationHandler) {
            DeserializationHandler<T> handler = ObjectUtils.cast(instance);
            instance = handler.handle(ObjectUtils.cast(instance));
        }

        return instance;
    }

    private Object deserializeToSection(Object instance, Section root) throws ReflectiveOperationException {
        for (Field field : instance.getClass().getFields()) {
            deserializeField(instance, field, root);
        }

        for (Method method : instance.getClass().getMethods()) {
            deserializeMethod(instance, method, root);
        }

        return instance;
    }

    private void deserializeField(Object instance, Field field, Section root) throws ReflectiveOperationException {
        if (!CdnUtils.isIgnored(field)) {
            deserializeMember(instance, new FieldMember(field), root);
        }
    }

    private void deserializeMethod(Object instance, Method setter, Section root) throws ReflectiveOperationException {
        try {
            if (!setter.getName().startsWith("set")) {
                return;
            }

            Method getter = instance.getClass().getMethod("get" + setter.getName().substring(3));
            deserializeMember(instance, new MethodMember(setter, getter), root);
        }
        catch (NoSuchMethodException ignored) {
            // cannot set this property, ignore
        }
    }

    private void deserializeMember(Object instance, AnnotatedMember member, Section root) throws ReflectiveOperationException {
        Option<Element<?>> elementValue = root.get(member.getName());

        if (elementValue.isEmpty()) {
            return;
        }

        Element<?> element = elementValue.get();
        Object defaultValue = member.getValue(instance);

        if (member.isAnnotationPresent(Contextual.class)) {
            deserializeToSection(defaultValue, (Section) element);
            return;
        }

        Deserializer<Object> deserializer = CdnUtils.findComposer(settings, member.getType(), member.getAnnotatedType(), member);
        Object value = deserializer.deserialize(settings, element, member.getAnnotatedType(), defaultValue, false);
        member.setValue(instance, value);
    }

}
