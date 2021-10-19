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
import net.dzikoysk.cdn.shared.AnnotatedMember.FieldMember;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class CdnSerializer {

    private final CdnSettings settings;

    public CdnSerializer(CdnSettings settings) {
        this.settings = settings;
    }

    public Configuration serialize(Object entity) {
        Configuration root = new Configuration();

        try {
            serialize(root, entity);
        }
        catch (Exception exception) {
            throw new IllegalStateException("Cannot access serialize member", exception);
        }

        return root;
    }

    public Section serialize(Section root, Object entity) throws Exception {
        Class<?> scheme = entity.getClass();

        for (Field field : scheme.getFields()) {
            if (CdnUtils.isIgnored(field)) {
                continue;
            }

            List<String> description = Arrays.stream(field.getAnnotationsByType(Description.class))
                    .flatMap(annotation -> Arrays.stream(annotation.value()))
                    .collect(Collectors.toList());

            if (field.isAnnotationPresent(Contextual.class)) {
                Section section = new Section(description, CdnConstants.OBJECT_SEPARATOR, field.getName());
                root.append(section);
                serialize(section, field.get(entity));
                continue;
            }

            Object propertyValue = field.get(entity);

            if (propertyValue != null) {
                Serializer<Object> serializer = CdnUtils.findComposer(settings, field.getType(), field.getAnnotatedType(), new FieldMember(field));
                root.append(serializer.serialize(settings, description, field.getName(), field.getAnnotatedType(), propertyValue));
            }
        }

        return root;
    }

}
