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
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.serialization.Serializer;
import net.dzikoysk.cdn.annotation.AnnotatedMember;
import panda.std.stream.PandaStream;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public final class CdnSerializer {

    private final CdnSettings settings;

    public CdnSerializer(CdnSettings settings) {
        this.settings = settings;
    }

    public Configuration serialize(Object entity) {
        try {
            return serialize(entity, new Configuration());
        }
        catch (Exception exception) {
            throw new IllegalStateException("Cannot access serialize member", exception);
        }
    }

    public <S extends Section> S serialize(Object entity, S output) throws ReflectiveOperationException {
        Class<?> template = entity.getClass();

        for (Field field : template.getFields()) {
            serializeField(entity, field, output);
        }

        for (Method method : template.getMethods()) {
            serializeMethod(entity, method, output);
        }

        return output;
    }

    private void serializeField(Object entity, Field field, Section output) throws ReflectiveOperationException {
        if (!CdnUtils.isIgnored(field)) {
            serializeMember(settings.getAnnotationResolver().createMember(entity, field), output);
        }
    }

    private void serializeMethod(Object entity, Method getter, Section output) throws ReflectiveOperationException {
        if (CdnUtils.isIgnored(getter)) {
            return;
        }

        try {
            if (!getter.getName().startsWith("get")) {
                return;
            }

            Method setter = entity.getClass().getMethod("set" + getter.getName().substring(3), getter.getReturnType());
            serializeMember(settings.getAnnotationResolver().createFunction(entity, setter, getter), output);
        }
        catch (NoSuchMethodException ignored) {
            // cannot set this property, ignore
        }
    }

    private void serializeMember(AnnotatedMember member, Section output) throws ReflectiveOperationException {
        Object propertyValue = member.getValue();
        List<String> description = PandaStream.of(member.getAnnotationsByType(Description.class))
                .flatMap(annotation -> Arrays.asList(annotation.value()))
                .toList();

        if (member.isAnnotationPresent(Contextual.class)) {
            Section section = new Section(description, CdnConstants.OBJECT_SEPARATOR, member.getName());
            output.append(section);
            serialize(propertyValue, section);
            return;
        }

        if (propertyValue != null) {
            Serializer<Object> serializer = CdnUtils.findComposer(settings, member.getType(), member.getAnnotatedType(), member);
            output.append(serializer.serialize(settings, description, member.getName(), member.getAnnotatedType(), propertyValue));
        }
    }

}
