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

import net.dzikoysk.cdn.CdnException;
import net.dzikoysk.cdn.CdnSettings;
import net.dzikoysk.cdn.CdnUtils;
import net.dzikoysk.cdn.reflect.AnnotatedMember;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.entity.Descriptions;
import net.dzikoysk.cdn.model.Configuration;
import net.dzikoysk.cdn.model.Section;
import net.dzikoysk.cdn.module.standard.StandardOperators;
import net.dzikoysk.cdn.reflect.TargetType;
import panda.std.Blank;
import panda.std.Option;
import panda.std.Result;
import panda.std.stream.PandaStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static panda.std.Result.error;
import static panda.std.Result.ok;

public final class CdnSerializer {

    private final CdnSettings settings;

    public CdnSerializer(CdnSettings settings) {
        this.settings = settings;
    }

    public Result<Configuration, CdnException> serialize(Object entity) {
        return serialize(entity, new Configuration());
    }

    public <S extends Section> Result<S, CdnException> serialize(Object entity, S output) {
        Class<?> type = entity.getClass();
        List<AnnotatedMember> members = new ArrayList<>();
        members.addAll(settings.getAnnotationResolver().getFields(type));
        members.addAll(settings.getAnnotationResolver().getProperties(type));

        for (AnnotatedMember annotatedMember : members) {
            Result<Blank, ? extends Exception> serializeResult = serializeMember(output, annotatedMember, entity);

            if (serializeResult.isErr()) {
                return serializeResult
                        .mapErr(CdnException::new)
                        .projectToError();
            }
        }

        return ok(output);
    }

    private Result<Blank, ? extends Exception> serializeMember(Section output, AnnotatedMember member, Object instance)  {
        if (member.isIgnored()) {
            return ok();
        }

        Result<Option<Object>, ReflectiveOperationException> propertyValueResult = member.getValue(instance);

        if (propertyValueResult.isErr()) {
            return error(propertyValueResult.getError());
        }

        List<String> description = PandaStream.of(member.getAnnotationsByType(Description.class))
                .flatMap(annotation -> Arrays.asList(annotation.value()))
                .toList();

        if (description.isEmpty()) {
            description.addAll(
                    PandaStream.of(member.getAnnotationsByType(Descriptions.class))
                        .flatMapStream(descriptions -> Arrays.stream(descriptions.value()))
                        .flatMapStream(descriptions -> Arrays.stream(descriptions.value()))
                        .toList()
            );
        }

        Option<Object> propertyValue = propertyValueResult.get();

        if (member.isAnnotationPresent(Contextual.class)) {
            Section section = new Section(description, StandardOperators.OBJECT_SEPARATOR, member.getName());
            output.append(section);
            return serialize(propertyValue.get(), section).mapToBlank();
        }

        TargetType targetType = member.getTargetType();

        return propertyValue
                .map(value -> CdnUtils.findComposer(settings, targetType, member)
                        .flatMap(serializer -> serializer.serialize(settings, description, member.getName(), targetType, value))
                        .peek(output::append)
                        .mapToBlank())
                .orElseGet(Result.ok());
    }

}
