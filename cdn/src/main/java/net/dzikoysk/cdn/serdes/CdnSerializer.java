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

package net.dzikoysk.cdn.serdes;

import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.annotation.AnnotatedMember;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import panda.std.stream.PandaStream;
import java.lang.reflect.Field;
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
            throw new IllegalStateException("Cannot serialize " + entity.getClass(), exception);
        }
    }

    public <S extends Section> S serialize(Object entity, S output) throws ReflectiveOperationException {
        Class<?> template = entity.getClass();

        for (Field field : template.getFields()) {
            serializeField(entity, field, output);
        }

        for (AnnotatedMember annotatedMember : settings.getAnnotationResolver().getProperties(entity)) {
            serializeMember(annotatedMember, output);
        }

        return output;
    }

    private void serializeField(Object entity, Field field, Section output) throws ReflectiveOperationException {
        serializeMember(settings.getAnnotationResolver().fromField(entity, field), output);
    }

    private void serializeMember(AnnotatedMember member, Section output) throws ReflectiveOperationException {
        if (member.isIgnored()) {
            return;
        }

        Object propertyValue = member.getValue();
        List<String> description = PandaStream.of(member.getAnnotationsByType(Description.class))
                .flatMap(annotation -> Arrays.asList(annotation.value()))
                .toList();

        if (member.isAnnotationPresent(Contextual.class)) {
            Section section = new Section(description, StandardOperators.OBJECT_SEPARATOR, member.getName());
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
